#include <stdio.h>
#include <assert.h>
#include <unistd.h>
#include <getopt.h>
#include <stdlib.h>
#include "pagetable.h"


extern int memsize;

extern int debug;

extern struct frame *coremap;

typedef struct { 
	int ref; 
	pgtbl_entry_t * p;  
} pageHolder; 

int clock_hand;
pageHolder memClock[memsize];
int numberOfPagesInMemory;

/* Page to evict is chosen using the clock algorithm.
 * Returns the page frame number (which is also the index in the coremap)
 * for the page that is to be evicted.
 */

int clock_evict() {
	while(memClock[clock_hand].ref == 1){
		memClock[clock_hand].ref = 0;
		clock_hand = clock_hand+1 % memsize;  // move the the hand to the next pagehodler
	}
	return memClock[clock_hand].p->frame >> PAGE_SHIFT;
	
}

/* This function is called on each access to a page to update any information
 * needed by the clock algorithm.
 * Input: The page table entry for the page that is being accessed.
 */
void clock_ref(pgtbl_entry_t *p) {
	int i;
	boolean found = false;
	//Check whether this page is in memory or not.
	for(i=0; i < memsize; i++){
		if(memClock[i].p == p){
			found = true;
			break;
		}			
	}
	
	if(found == false){// page not found in memory
		//add page to memClock
		memClock[clock_hand].p = p;
		memClock[clock_hand].ref = 1;
		clock_hand = clock_hand+1 % memsize; 
		
		numberOfPagesInMemory++;
	}
	else{//page found in our queue
		memClock[i].ref = 1;
	}

	return;
}

/* Initialize any data structures needed for this replacement
 * algorithm. 
 */
void clock_init() {
	clock_hand = 0;
	numberOfPagesInMemory = 0;
	
	for(i=0; i < memsize; i++){
		memClock[i].ref= 0;
		memClock[i].p = NULL;
	}
	
}
