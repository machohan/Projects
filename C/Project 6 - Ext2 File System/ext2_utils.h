#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <sys/mman.h>
#include <string.h>
#include <errno.h>
#include "ext2.h"

#define ROOT_INODE 2
#define ENTRY_TYPE_MASK	0x07

#define SUPER_BLOCK 1 
#define GROUP_DESCRIPTOR_BLOCK 2 
#define BLOCK_BITMAP_BLOCK 3
#define INODE_BITMAP_BLOCK 4
#define INODE_TABLE_BLOCK 5
#define SECTORS_PER_LB 2
#define MAXPATH_LENGTH 512
#define MAX_TOKENS 64
#define MAX_FILE_SIZE 44 * 1024
#define MAX_FILE_NAME 255
#define NUM_DIRECT_BLOCK 12
#define MAX_NUM_BLOCKS 	44

struct comp_inode_type{
	unsigned int	inode;
	unsigned char	file_type;
};

void init_glob_pointers(unsigned char * disk);

void loadImage(char * diskimage);

void update_ibitmap(int inode);

void update_bbitmap(int blocknum);

int check_ibitmap(int inode);

int find_vacantInode();

int find_vacantblock();

void parsePath(char * absPath, char * tokens[]);

struct comp_inode_type * getObjectInode(char * tokens[], struct comp_inode_type *comp_i_t);

void getChildInode(char * sub_component, int parent_inode, int dirCheck, struct comp_inode_type *comp_i_t);

void insertRecord(int parent_inode, int recInode, char* name, unsigned char type);

void delObject(int parent_inode, int comp_inode);

void delete_all_blocks(int inode);

void extract_parentpath_component(char * fullpath, char *parentpath, char* component);
