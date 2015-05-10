#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/mman.h>
#include "smalloc.h"

/*I plan to test a case where at run time freelist will become empty
* (i.e all space will be allocated) the means allocated_list has maximum
* memory size that can be allocated by smalloc. Once, freelist is empty,
* allocated space will be made free one by one and so allocated_list will
* become empty. It is interested case because it rebuilds the linked list
* structures from begining while program is running.
*/

//define size of memory to acquire from OS.
#define SIZE 4096 * 64

int main(void) {

    mem_init(SIZE); //acquiring memory from OS.
    
    char *ptrs[10];
    int i = 0;
    int random;
    int spaceNeeded;
    int spaceRemn = SIZE;

    printf("\n");
    printf("=====================================\n");
    printf("allocated_list and freelist at start:\n");
    printf("\n");
    printf("List of allocated blocks:\n");
    print_allocated();
    printf("List of free blocks:\n");
    print_free();
    printf("=====================================\n");
    printf("\n");


    //testing smalloc
    while(i < 9){
        random = rand()%100000 + 50000;
        spaceNeeded =random;
        if(random % 8 > 0){
            spaceNeeded = random - (random%8) + 8;
        }
        ptrs[i] = smalloc(spaceNeeded);
        if(ptrs[i] != NULL){
            spaceRemn -= spaceNeeded;
        }
        i++;
    }
    if(spaceRemn >= 0){
        ptrs[9] = smalloc(spaceRemn); 
    }

    printf("\n");
    printf("=====================================\n");
    printf("allocated_list and freelist after smalloc:\n");
    printf("\n");
    printf("List of allocated blocks:\n");
    print_allocated();
    printf("List of free blocks:\n");
    print_free();
    printf("=====================================\n");
    printf("\n");

    //testing sfree
    i =0;
    while(i < 10){
        sfree(ptrs[i]);
        ptrs[i] = NULL;
        i++;
    }

    printf("\n");
    printf("=====================================\n");
    printf("allocated_list and freelist after sfree:\n");
    printf("\n");
    printf("List of allocated blocks:\n");
    print_allocated();
    printf("List of free blocks:\n");
    print_free();
    printf("=====================================\n");
    printf("\n");

    //testing mem_clean();
    mem_clean();
    printf("\n");
    printf("=====================================\n");
    printf("allocated_list and freelist after mem_clean:\n");
    printf("\n");
    printf("List of allocated blocks:\n");
    print_allocated();
    printf("List of free blocks:\n");
    print_free();
    printf("=====================================\n");
    printf("\n");

    return 0;
}
