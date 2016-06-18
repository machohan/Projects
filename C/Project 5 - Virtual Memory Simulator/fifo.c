#include <stdio.h>
#include <assert.h>
#include <unistd.h>
#include <getopt.h>
#include <stdlib.h>
#include "pagetable.h"


extern int memsize;

extern int debug;

extern struct frame *coremap;

int frontOfQIdx; // keeps track of the front of our queue of pages in memeory
int numOfPagesInMemory;
pgtbl_entry_t * memQueue[memsize];

/* Page to evict is chosen using the fifo algorithm.
 * Returns the page frame number (which is also the index in the coremap)
 * for the page that is to be evicted.
 */
int fifo_evict() {
	
	
	
	return 0;
}

/* This function is called on each access to a page to update any information
 * needed by the fifo algorithm.
 * Input: The page table entry for the page that is being accessed.
 */
void fifo_ref(pgtbl_entry_t *p) {
	boolean found= false;
	//is this an existing page in memory, or a new page that was loaded into memory?
	for(i=0; i<memsize; i++){
		if(memQueue[i]== p){
			found = true;
		}			
	}
	if(found == true){
		// do nothing,  page already in memory
	}
	
	return;
}

/* Initialize any data structures needed for this 
 * replacement algorithm 
 */
void fifo_init() {
	// we define a queue represnted by an array of size memsize
	for(i=0; i<memsize; i++){
		memQueue[i]= 0;		
	}
	frontOfQIdx=0;
	numOfPagesInMemory=0;
}
