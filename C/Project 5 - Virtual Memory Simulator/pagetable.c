#include <assert.h>
#include <string.h> 
#include "sim.h"
#include "pagetable.h"

#define CLEARFRAME 0
#define CLEARSTATUSBITSMASK (0xfffffff0)

// The top-level page table (also known as the 'page directory')
pgdir_entry_t pgdir[PTRS_PER_PGDIR]; 

// Counters for various events.
// Your code must increment these when the related events occur.
int hit_count = 0;
int miss_count = 0;
int ref_count = 0; // hit_count+miss_count
int evict_clean_count = 0;
int evict_dirty_count = 0;

/*
 * Allocates a frame to be used for the virtual page represented by p.
 * If all frames are in use, calls the replacement algorithm's evict_fcn to
 * select a victim frame.  Writes victim to swap if needed, and updates 
 * pagetable entry for victim to indicate that virtual page is no longer in
 * (simulated) physical memory.
 *
 * Counters for evictions should be updated appropriately in this function.
 */
int allocate_frame(pgtbl_entry_t *p) {
	off_t swap_offset;
	int i;
	int frame = -1;
	for(i = 0; i < memsize; i++) {
		if(!coremap[i].in_use) {
			frame = i;
			break;
		}
	}
	if(frame == -1) { // Didn't find a free page.
		// Call replacement algorithm's evict function to select victim
		frame = evict_fcn();

		/* All frames were in use, so a victim frame is chosen to be evicted 
		Write victim page to swap. If needed, update pagetable p
		IMPLEMENTATION NEEDED
		*/
		
		//if the frame is dirty/modified -- 
		if(coremap[frame].pte->frame & PG_DIRTY){
			//write page to disk -- 
			swap_offset = swap_pageout(frame, coremap[frame].pte->swap_off);
			if( swap_offset != INVALID_SWAP){
			//update swap in pte: 
			coremap[frame].pte->swap_off = swap_offset;
			evict_dirty_count++;
			}
			else{
				perror("Failed to allocate aligned memory for page table");
				exit(1);
			}
		}
		else {//write page to disk, if it's not been written to disk before 
			if(coremap[frame].pte->swap_off == INVALID_SWAP){
			
				swap_offset = swap_pageout(frame, coremap[frame].pte->swap_off);
				if(swap_offset != INVALID_SWAP){
					//update swap in pte: 
					coremap[frame].pte->swap_off = swap_offset;
				}
				else{
					perror("Failed to allocate aligned memory for page table");
					exit(1);
				}
			}
			//-- clean page
			evict_clean_count++;
		}
		
		//updates pagetable entry for victim to indicate that virtual page is no longer in physical memory and is in swap area
		
		coremap[frame].pte->frame = 
			coremap[frame].pte->frame & (~PG_VALID); //-clear status bits
		coremap[frame].pte->frame = 
			coremap[frame].pte->frame & (~PG_DIRTY); //-clear status bits
		coremap[frame].pte->frame = 
			coremap[frame].pte->frame & (~PG_REF); //-clear status bits
		coremap[frame].pte->frame = 
			coremap[frame].pte->frame | PG_ONSWAP; 	//- set on_swap bit
			
	} //end if...	
		
		//bind frame to page table entry:	
		
		
		//set pgtb_ent frame memeber to this frame and valid bit to 1
		//p->frame = p->frame | (frame << PAGE_SHIFT);
		 p->frame = p->frame & (~PG_DIRTY);
		// p->frame = p->frame & (~PG_REF);
		// p->frame = p->frame & (~PG_ONSWAP);
		//p->frame = p->frame | PG_VALID;
		
			//coremap updates are done below.... read!!!! */

	// Record information for virtual page that will now be stored in frame
	coremap[frame].in_use = 1;
	coremap[frame].pte = p;

	return frame;
}

/*
 * Initializes the top-level pagetable.
 * This function is called once at the start of the simulation.
 * For the simulation, there is a single "process" whose reference trace is 
 * being simulated, so there is just one top-level page table (page directory).
 * To keep things simple, we use a global array of 'page directory entries'.
 *
 * In a real OS, each process would have its own page directory, which would
 * need to be allocated and initialized as part of process creation.
 */
void init_pagetable() {
	int i;
	// Set all entries in top-level pagetable to 0, which ensures valid
	// bits are all 0 initially.
	for (i=0; i < PTRS_PER_PGDIR; i++) {
		pgdir[i].pde = 0;
	}
}

// For simulation, we get second-level pagetables from ordinary memory
pgdir_entry_t init_second_level() {
	int i;
	pgdir_entry_t new_entry;
	pgtbl_entry_t *pgtbl;

	// Allocating aligned memory ensures the low bits in the pointer must
	// be zero, so we can use them to store our status bits, like PG_VALID
	if (posix_memalign((void **)&pgtbl, PAGE_SIZE,       // the lower 3 bits of pgtbl are zeroes
			   PTRS_PER_PGTBL*sizeof(pgtbl_entry_t)) != 0) {
		perror("Failed to allocate aligned memory for page table");
		exit(1);
	}

	// Initialize all entries in second-level pagetable
	for (i=0; i < PTRS_PER_PGTBL; i++) {
		pgtbl[i].frame = 0; // sets all bits, including valid, to zero
		pgtbl[i].swap_off = INVALID_SWAP;
	}

	// Mark the new page directory entry as valid
	new_entry.pde = (uintptr_t)pgtbl | PG_VALID;    // pgtbl is modified... it's now pgtbl+1...

	return new_entry;
}

/* 
 * Initializes the content of a (simulated) physical memory frame when it 
 * is first allocated for some virtual address.  Just like in a real OS,
 * we fill the frame with zero's to prevent leaking information across
 * pages. 
 * 
 * In our simulation, we also store the the virtual address itself in the 
 * page frame to help with error checking.
 *
 */
void init_frame(int frame, addr_t vaddr) {
	// Calculate pointer to start of frame in (simulated) physical memory
	// we added the cast to char...
	char *mem_ptr = (char *)&physmem[frame*SIMPAGESIZE];
	// Calculate pointer to location in page where we keep the vaddr
        addr_t *vaddr_ptr = (addr_t *)(mem_ptr + sizeof(int));
	
	memset(mem_ptr, 0, SIMPAGESIZE); // zero-fill the frame
	*vaddr_ptr = vaddr;             // record the vaddr for error checking

	return;
}

/*
 * Locate the physical frame number for the given vaddr using the page table.
 *
 * If the entry is invalid and not on swap, then this is the first reference 
 * to the page and a (simulated) physical frame should be allocated and 
 * initialized (using init_frame).  
 *
 * If the entry is invalid and on swap, then a (simulated) physical frame
 * should be allocated and filled by reading the page data from swap.
 *
 * Counters for hit, miss and reference events should be incremented in
 * this function.DD
 */
char *find_physpage(addr_t vaddr, char type) {
	int frameNum = -1;
	pgtbl_entry_t *p=NULL; // pointer to the full page table entry for vaddr
	
	unsigned idx = PGDIR_INDEX(vaddr) & (PGTBL_MASK); // get index into page directory
	unsigned itx = PGTBL_INDEX(vaddr); // get index into pagetable
	
	//check if it's a valid directory entry and it points to a pagetable.
	if (!(pgdir[idx].pde & PG_VALID)){
		pgdir[idx] = init_second_level();
	}
	
	//next, we use PGTBL_INDEX(x) to get the index into the pagetable: 
	p = (pgtbl_entry_t *)((pgdir[idx].pde & (~PG_VALID)) + (itx * sizeof(pgtbl_entry_t) ) );
	//(OC)now we have access to the targeted pgtb_ent p
	
	//DEBUG
	printf("idx: %i\n", idx);
	printf("itx: %i\n", itx);
	printf("p: %lx\n", *p);
	
	
	//(OC)next we check to see if page frame is in memory(valid =1) or onswap.
	//check if page is in memory
	if(p->frame & PG_VALID){
		hit_count++;
		if( type == 'S' || type == 'M') //-- if it's a write access
			p->frame = p->frame | PG_DIRTY; //-- set dirty bit
	} 
	
	//page not in memory
	else {
		miss_count++;
	
		frameNum = allocate_frame(p);
		
		if(p->frame & PG_ONSWAP){//page on swap
			if (swap_pagein(frameNum, p->swap_off) != 0){
				perror("failed to read a swapped page!");
				exit(1);
			}
			if( type == 'S' || type == 'M') //if it's a write access
				p->frame = p->frame | PG_DIRTY; //set dirty bit			
		} 
		else{ // not in memory and not in swap
			init_frame(frameNum, vaddr);			
			p->frame = p->frame | PG_DIRTY; //-- set dirty bit
		}
		
		p->frame = p->frame | (frameNum << PAGE_SHIFT);
		p->frame = p->frame | PG_VALID; //-- set valid bit - now in memory
		p->frame = p->frame & (~PG_ONSWAP); // clear on_swap flag	
			
		
	}
	
	// Call replacement algorithm's ref_fcn for this page
	p->frame = p->frame | PG_REF;	 //-- set reference bit
	ref_fcn(p);
	ref_count++;
	
	
	// Return pointer into (simulated) physical memory at start of frame
	return  &physmem[(p->frame >> PAGE_SHIFT)*SIMPAGESIZE];
		
	
	// Use vaddr to get index into 2nd-level page table and initialize 'p'



	// Check if p is valid or not, on swap or not, and handle appropriately



	// Make sure that p is marked valid and referenced. Also mark it
	// dirty if the access type indicates that the page will be written to.

	

	
}

void print_pagetbl(pgtbl_entry_t *pgtbl) {
	int i;
	int first_invalid, last_invalid;
	first_invalid = last_invalid = -1;

	for (i=0; i < PTRS_PER_PGTBL; i++) {
		if (!(pgtbl[i].frame & PG_VALID) && 
		    !(pgtbl[i].frame & PG_ONSWAP)) {
			if (first_invalid == -1) {
				first_invalid = i;
			}
			last_invalid = i;
		} else {
			if (first_invalid != -1) {
				printf("\t[%d] - [%d]: INVALID\n",
				       first_invalid, last_invalid);
				first_invalid = last_invalid = -1;
			}
			printf("\t[%d]: ",i);
			if (pgtbl[i].frame & PG_VALID) {
				printf("VALID, ");
				if (pgtbl[i].frame & PG_DIRTY) {
					printf("DIRTY, ");
				}
				printf("in frame %d\n",pgtbl[i].frame >> PAGE_SHIFT);
			} else {
				assert(pgtbl[i].frame & PG_ONSWAP);
				printf("ONSWAP, at offset %lu\n",pgtbl[i].swap_off);
			}			
		}
	}
	if (first_invalid != -1) {
		printf("\t[%d] - [%d]: INVALID\n", first_invalid, last_invalid);
		first_invalid = last_invalid = -1;
	}
}

void print_pagedirectory() {
	int i; // index into pgdir
	int first_invalid,last_invalid;
	first_invalid = last_invalid = -1;

	pgtbl_entry_t *pgtbl;

	for (i=0; i < PTRS_PER_PGDIR; i++) {
		if (!(pgdir[i].pde & PG_VALID)) {
			if (first_invalid == -1) {
				first_invalid = i;
			}
			last_invalid = i;
		} else {
			if (first_invalid != -1) {
				printf("[%d]: INVALID\n  to\n[%d]: INVALID\n", 
				       first_invalid, last_invalid);
				first_invalid = last_invalid = -1;
			}
			pgtbl = (pgtbl_entry_t *)(pgdir[i].pde & PAGE_MASK);
			printf("[%d]: %p\n",i, pgtbl);
			print_pagetbl(pgtbl);
		}
	}
}
