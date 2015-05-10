#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/mman.h>
#include "smalloc.h"


void *mem;
struct block *freelist, *freeListNode, *allocated_list, *allocListNode;

/*This function deletes a node in freelist
parameters: pointer to freelist, list
            pointer to node to delete, node
returns   : void
*/
void deleteFreeListNode(struct block *list, struct block *node){
    struct block *preNode, *toDelNode, *postNode;
    preNode = list;     //preNode short for previousNode
    toDelNode = node;   //toDelNode short for toDeleteNode
    postNode = list;

        /*if toDelnode addr mataches first node in the freelist*/
        if((*preNode).addr == (*toDelNode).addr){
            list = (*preNode).next;
            (*toDelNode).next = NULL;
            free(toDelNode);
            toDelNode = NULL;
            freelist = list;
        }

        else{
            /*postNode becomes second node in the freelist*/
            postNode = (*preNode).next;
            if(postNode == NULL){
                (*preNode).next = NULL;
            }
            else{
                /*search for correct addr to match with to */
                while((*postNode).addr != (*toDelNode).addr){
                    preNode = postNode;
                    postNode = (*preNode).next;
                }
                /*restructuring link in linked list of freelist*/
                (*preNode).next = (*postNode).next;
            }

            /*calling free to free space used for toDelNode*/
            free(toDelNode);
            toDelNode = NULL;
            freelist = list;
        }
        
}

/*This function reorders the nodes in freelist when required
parameters: pointer to freelist, list
            pointer to node to insert, node
            int type
returns   : void
*/
void orderFreeList(struct block *list, struct block *node, int type){
    struct block *preNode, *nodeToInsert, *postNode;
    preNode = list;
    nodeToInsert = node;
    postNode = list;

    /*type 2 means node has been freed from allocated_list*/
    if(type == 2){
        (*nodeToInsert).next = NULL;
    }

    /*when freelist linked list is empty*/
    if(freelist == NULL){
        (*node).next = NULL;
        freelist = node;
    }
    else{
        /*when first node's next variable in freelist is empty*/
        if((*preNode).next == NULL){
            /*if first node's addr is less than node to insert's addr*/
             if((*preNode).addr < (*nodeToInsert).addr){

                (*preNode).next = nodeToInsert;
                freelist = preNode;
            }
            /*if first node's addr is greater than node to insert's addr*/
            else if((*preNode).addr > (*nodeToInsert).addr){
                (*nodeToInsert).next = freelist;
                freelist = nodeToInsert;
            }
        }
        else{
            /*if first node's next variable is not enpty and
            first node's addr variable is greater than node
            to insert's addr variable*/
            if((*preNode).addr > (*nodeToInsert).addr){
                (*nodeToInsert).next = freelist;
                freelist = nodeToInsert;
            }
            else{
                /*otherwise, search until end of the freelist or
                until node to insert's addr is less than current node*/
                postNode = (*list).next;
                while((*preNode).next != NULL && (*postNode).addr < (*nodeToInsert).addr ){
                    preNode = postNode;
                    postNode = (*postNode).next;
                }
                /*restructure links in freelist*/
                (*preNode).next = nodeToInsert;
                (*nodeToInsert).next = postNode;
            }
        }
    }
}


/*This function searches for space in freelist
parameters: pointer to freelist, list
            int size
returns   : a pointer to found node
*/
struct block *searchFreeListNode(struct block *list, int size){
    struct block * freeList, *node;
    freeList = list;
    node = list;

    /*when freelist is not empty*/
    if(freelist != NULL){
        /*continue to search for correct size node*/
        while((*node).size < size && (*node).next != NULL){
            node = (*node).next;
        }
        /*if found, node is not equal to NULL and
        node size is either greater or eual to 
        requested size*/
        if(node != NULL && (*node).size >= size){
            if((*node).size >= size){
                void *spacePtr  = (*node).addr; //to return

                /*if exact size node is found and is last node
                in freelist*/
                if((*node).size - size == 0 && (*node).next == NULL){
                    struct block *preNode, *preNodePtr;
                    
                    preNode = list;
                    preNodePtr = preNode;

                    /*search to find previous node of node found*/
                    while((*preNode).addr != (*node).addr ){
                        preNodePtr = preNode;
                        preNode = (*preNode).next;
                    }
                    /*make previous node of node found be last node 
                    in freelist */
                    (*preNodePtr).next = NULL;
                    /*delete the node found  from freelist*/
                    deleteFreeListNode(freelist,node);
                }
                /*if node found has exact space required
                but is node last node in freelist*/
                else if((*node).size - size == 0){
                    deleteFreeListNode(freelist,node);
                }
                /*if node found has greater space than requested*/
                else{
                    int spaceNeeded =size;
                    if(size % 8 > 0){
                        spaceNeeded = size - (size%8) + 8;
                    }
                    (*node).addr = (*node).addr + spaceNeeded;
                    (*node).size = (*node).size - spaceNeeded;
                    /*reorder freelist linked list as addr have changed*/
                    orderFreeList(freelist,node,1);
                }
                return spacePtr;
            }
        }
    }
    /*if no space was found*/
    return NULL;
}

/*This function attempts to find space of requested size
parameters: unsigned int nbytes
returns   : a pointer to found space
*/
void *smalloc(unsigned int nbytes) {

    void *spacePtr;
    int spaceReq = nbytes;
    /*if requested size, nbytes is not multiple of 8*/
    if(nbytes % 8 > 0){
        /*make space required mulitple of 8 greater than nbytes*/
        spaceReq = nbytes- (nbytes%8) + 8;
    }
    /*search for space in freelist*/
    spacePtr = searchFreeListNode(freelist, spaceReq);
    /*if space found in freelist than add to allocated_list*/
    if(spacePtr != NULL){

        allocListNode = malloc(sizeof(struct block));
        (*allocListNode).addr = spacePtr;

        (*allocListNode).size = spaceReq;
        (*allocListNode).next = allocated_list;
        allocated_list = allocListNode;

        return spacePtr;
    }
    /*if space node found in freelist*/
    else{
        return NULL;
    }
}

/*This function frees space allocated by smalloc
parameters: void pointer to space to free
returns   : int 0 if success or int -1 if fails
*/
int sfree(void *addr) {

    /*if space was actually allocated by smalloc*/
    if(addr != NULL){
        struct block *prevNode, *toFreeNode, *postNode;
        prevNode = allocated_list;
        toFreeNode = allocated_list;
        postNode = (*prevNode).next;

        /*if it is first node in allocated_list*/
        if(postNode == NULL){
            allocated_list = NULL;
        }
        /*take out node to free and restructure
        linked list*/
        else if((*prevNode).addr == addr){
            toFreeNode = prevNode;
            allocated_list = (*prevNode).next;
        }
        /*find previous node of node containing
        same addr, take out node containing
        same addr and restructure linked list*/
        else{
            while((*postNode).addr != addr){
                prevNode = postNode;
                postNode = (*prevNode).next;
            }
            toFreeNode = postNode;
            (*prevNode).next = (*postNode).next;
        }
        /*add node freed from allocated_list to
        frelist*/
        orderFreeList(freelist,toFreeNode,2);
        return 0;
    }
    else{
        return -1;
    }
    
}


/* Initialize the memory space used by smalloc,
 * freelist, and allocated_list
 * Note:  mmap is a system call that has a wide variety of uses.  In our
 * case we are using it to allocate a large region of memory. 
 * - mmap returns a pointer to the allocated memory
 * Arguments:
 * - NULL: a suggestion for where to place the memory. We will let the 
 *         system decide where to place the memory.
 * - PROT_READ | PROT_WRITE: we will use the memory for both reading
 *         and writing.
 * - MAP_PRIVATE | MAP_ANON: the memory is just for this process, and 
 *         is not associated with a file.
 * - -1: because this memory is not associated with a file, the file 
 *         descriptor argument is set to -1
 * - 0: only used if the address space is associated with a file.
 */
void mem_init(int size) {
    mem = mmap(NULL, size,  PROT_READ | PROT_WRITE, MAP_PRIVATE | MAP_ANON, -1, 0);
    if(mem == MAP_FAILED) {
         perror("mmap");
         exit(1);
    }

    /*initializing freelist with full space acquired by mmap*/
    freelist = malloc(sizeof(struct block));
    (*freelist).addr = mem;
    (*freelist).size = size;
    (*freelist).next = NULL;

    /*no block in allocated_list just yest*/
    allocated_list = NULL;
}

/*This function frees all space used by this program.
*/
void mem_clean(){
    void *ptr;

    /*free all space used by freelist*/
    while(freelist != NULL){
        ptr = freelist;
        freelist = (*freelist).next;
        free(ptr);
        ptr = NULL;
    }

    /*free all space used by allocated_list*/
    while(allocated_list != NULL){
        ptr = allocated_list;
        allocated_list = (*allocated_list).next;
        free(ptr);
        ptr = NULL;
    }
}
