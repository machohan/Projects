#include <stdio.h>
#include <assert.h>
#include <unistd.h>
#include <getopt.h>
#include <stdlib.h>
#include "pagetable.h"
#include "sim.h";

extern int memsize;

extern int debug;

extern struct frame *coremap;

FILE *traceStream;

addr_t  * allAccess;

 typedef struct { 
	int access_marker; 
	int frameNum;  
} traceStruct;  

//the evict function updates this variable every time it finds a page to evict
pgtbl_entry_t * pageToReplace = NULL;

int lineCount;

// keeps track of which memory access we are at in allAccess
int allAccessCursor; 

/* Page to evict is chosen using the optimal (aka MIN) algorithm. 
 * Returns the page frame number (which is also the index in the coremap)
 * for the page that is to be evicted.
 */
int opt_evict() {
	traceStruct t;
	t.frame = -1;
	t.access_marker = 0;
	addr_t vaddr;
	char *memptr;
	addr_t *checkaddr;
	int tempCursor = allAccessCursor+1;
	
	for(i = 0; i < memsize; i++) {
		memptr = &(physmem[(p->frame >> PAGE_SHIFT)*SIMPAGESIZE]);
		checkaddr = (addr_t *)(memptr + sizeof(int));
		vaddr = *checkaddr; // get virtual address written inside the frame
		
		while(tempCursor < lineCount){
			if(vaddr == allAccess[tempCursor]) 
				if(t.access_marker < (tempCursor - allAccessCursor)){
				t.access_marker = tempCursor - allAccessCursor;
				t.frame = i;
				break;
			}
			tempCursor++;
		}
	}
	
	return t.frame;
	
	
	
	//return 0;
}

/* This function is called on each access to a page to update any information
 * needed by the opt algorithm.
 * Input: The page table entry for the page that is being accessed.
 */
void opt_ref(pgtbl_entry_t *p) {
	//update cursor to point to index of the current page being accessed
	allAccessCursor++;
		
	return;
}

/* Initializes any data structures needed for this
 * replacement algorithm.
 */
void opt_init() {
	allAccessCursor = -1;
	int j;
	int allAccessIndex=0;
	lineCount=0;
	char buf[MAXLINE];
	addr_t vaddr = 0;
	char type;
	
	// open the trace file for reading
		if((traceStream = fopen(tracefile, "r")) == NULL) {
			perror("Error opening tracefile: by OPT");
			exit(1);
		}
		
	// get total number of memory accesses
	while(fgets(buf, MAXLINE, traceStream) != NULL) {
		if(buf[0] != '=') {
		lineCount++;
		}
	}
	
	// will hold all the memory access in the trace file
	allAccess = malloc(lineCount * sizeof(struct addr_t)); 
	 
	 if (lseek(traceStream, 0, SEEK_SET) != 0) {
		perror("failed to reset read position in OPT");
		exit(1);
	}
	
	// read all memory reference from trace file to allAccesses
	while(fgets(buf, MAXLINE, traceStream) != NULL) {
		if(buf[0] != '=') {
			sscanf(buf, "%c %lx", &type, &vaddr);
			
			allAccess[allAccessIndex] = vaddr;
			allAccessIndex++;
		}

	}
}

