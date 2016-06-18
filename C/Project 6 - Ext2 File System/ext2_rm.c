#include "ext2_utils.h"

/*
ext2_rm: 
This program takes two command line arguments.

The first is the name of an ext2 formatted virtual disk, 
and the second is an absolute path to a file or link 
(not a directory) on that disk. The program should work 
like rm, removing the specified file from the disk. 
If the file does not exist or if it is a directory, then 
your program should return the appropriate error.
*/

//pointer to disk image in memory
extern unsigned char * disk;

//points to inode table
extern struct ext2_inode *pn; 

int main(int argc, char **argv) {
	if(argc != 3) {
        fprintf(stderr, "Usage: readimg <image file name> absolute path <ex. /dir1/dir2/file>\n");
        exit(1);
    }
	
	char * tokens_parent[MAX_TOKENS];
	char * tokens_component[MAX_TOKENS];
	char parentpath[MAXPATH_LENGTH];
	char component[MAX_FILE_NAME];
	
	//get paths of parent directory and component to remove
	extract_parentpath_component(argv[2], parentpath, component);
		
	struct comp_inode_type comp_i_t, parent_i_t;
	struct comp_inode_type *comp_it_p = &comp_i_t;
	struct comp_inode_type *parent_it_p = &parent_i_t;
	
	parsePath(argv[2], tokens_component);
	parsePath(parentpath, tokens_parent);
	
	comp_i_t.inode = 0;
	comp_i_t.file_type = 0;
	
	parent_i_t.inode = 0;
	parent_i_t.file_type = 0;
	
	loadImage(argv[1]); // loads disk image onto memory
	
	comp_it_p = getObjectInode(tokens_component, comp_it_p);
	parent_it_p = getObjectInode(tokens_parent, parent_it_p);
		
	//check if the component to be deleted exists; check the inode bitmap bit for it
    if(check_ibitmap(comp_it_p->inode) == 0){// if doesn't exist
		fprintf( stderr, strerror(ENOENT));
		fprintf( stderr, "\n");
		exit(1);
	} 
	
	//perform check for the component to be deleted
	if(comp_it_p->inode == 0) {// component not found!
		fprintf( stderr, strerror(ENOENT));
		fprintf( stderr, "\n");
		exit(1);
	}
	
	//perform check for the parent directory
	if(parent_it_p->inode == 0) {// directory not found!
		fprintf( stderr, strerror(ENOENT));
		fprintf( stderr, "\n");
		exit(1);
	}
	
	//component must not be directory
	if(comp_it_p->file_type == EXT2_FT_DIR ){
		//error
		fprintf(stderr, strerror(EISDIR));
		fprintf( stderr, "\n");
		exit(1);
	}
	//parent must be a directory
	if(parent_it_p->file_type != EXT2_FT_DIR){
		//error
		fprintf( stderr, strerror(ENOENT));
		fprintf( stderr, "\n");
		exit(1);	
	}
   
	delObject(parent_it_p->inode, comp_it_p->inode);
    
    return 0;
}