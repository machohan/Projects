#include <stdio.h>
#include <assert.h>
#include <unistd.h>
#include <getopt.h>
#include <stdlib.h>
#include "pagetable.h"


extern int memsize;

extern int debug;

extern struct frame *coremap;

extern int ref_count;

typedef struct { 
	int timestamp; 
	pgtbl_entry_t * p;  
} LRUtable; 

// tracks a view of memory and what pages are in it
LRUtable memLRU[memsize];

int numberOfPagesInMemory;

/* Page to evict is chosen using the accurate LRU algorithm.
 * Returns the page frame number (which is also the index in the coremap)
 * for the page that is to be evicted.
 */

int lru_evict() {
	
	int oldestStamp = memLRU[0].timestamp;
	int idxOfOldest = 0;
	int idx = 0;
		
	while(idx < memsize){
		if(memLRU[idx].timestamp < oldestStamp){
			idxOfOldest = idx;
			oldestStamp = memLRU[idx].timestamp;
		}
		idx++;
	}
	return memLRU[idxOfOldest].p->frame >> PAGE_SHIFT;
}

/* This function is called on each access to a page to update any information
 * needed by the lru algorithm.
 * Input: The page table entry for the page that is being accessed.
 */
void lru_ref(pgtbl_entry_t *p) {
	int i;
	boolean found = false;
	//Check whether this page is in memory or not.
	for(i=0; i<memsize; i++){
		if(memLRU[i].p == p){
			found = true;
			break;
		}			
	}
	
	if(found == false){// page not found in memory
		int oldestStamp = memLRU[0].timestamp;
		int idxOfOldest = 0;
		int idx = 0;
		
		while(idx < memsize){
			if(memLRU[idx].timestamp < oldestStamp){
				idxOfOldest = idx;
				oldestStamp = memLRU[idx].timestamp;
			}
			idx++;
		}
		memLRU[idxOfOldest].p = p;
		memLRU[idxOfOldest].timestamp = ref_count;
		
		numberOfPagesInMemory++;
	}
	else{//page found in our memLRU
		memLRU[i].timestamp = ref_count;
	}

	return;
}


/* Initialize any data structures needed for this 
 * replacement algorithm 
 */
void lru_init() {
	numberOfPagesInMemory = 0; // initialize number of pages
	
	// initilize our memLRU data structure
	for(i=0; i< memsize; i++){
		memLRU[i].timestamp= 0;
		memLRU[i].p = NULL;
	}
}
