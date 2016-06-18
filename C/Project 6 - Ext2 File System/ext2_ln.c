#include "ext2_utils.h"

/*
ext2_ln: 

This program takes three command line arguments. 

The first is the name of an ext2 formatted virtual disk. 
The other two are absolute paths on your ext2 formatted disk. 

The program should work like ln, creating a hard link from the first specified file to the second specified path. If the first location does not exist (ENOENT), if the second location already exists (EEXIST), or if either location refers to a directory (EISDIR), then your program should return the appropriate error. Note that this version of ln only works with files.
*/

//pointer to disk image in memory
extern unsigned char * disk;

//points to inode table
extern struct ext2_inode *pn; 

int main(int argc, char **argv) {
	
	if(argc != 4) {
        fprintf(stderr, "Usage: readimg <image file name> absolute path source file <ex. /dir1/dir2/srcfile> destination for linked file <ex. /dir4/dir5/linkfile>\n");
        exit(1);
    }
	
	loadImage(argv[1]); // loads disk image onto memory
	
	char * tokens_source[MAX_TOKENS];
	char * tokens_dest[MAX_TOKENS];
	char * tokens_dest_parent[MAX_TOKENS];
	char dest_parent_path[MAXPATH_LENGTH];
	char ln_component[MAX_FILE_NAME];
	
	//check to see whether source file exists
	parsePath(argv[2], tokens_source);
	
	struct comp_inode_type source_i_t, parent_i_t, lfile_i_t;
	struct comp_inode_type *source_it_p = &source_i_t;
	struct comp_inode_type *parent_it_p = &parent_i_t;
	struct comp_inode_type *lfile_it_p = &lfile_i_t;
	
	source_i_t.inode = 0;
	source_i_t.file_type = 0;
	
	parent_i_t.inode = 0;
	parent_i_t.file_type = 0;
	
	lfile_i_t.inode = 0;
	lfile_i_t.file_type = 0;
	
	/*is there another object with the same 
	name as the link file to be created in it's
	parent directory?*/
	source_it_p = getObjectInode(tokens_source, source_it_p);
	
	//source file has to exist!
	if(source_it_p->inode == 0) {
		fprintf( stderr, strerror(ENOENT));
		fprintf( stderr, "\n");
		exit(1);
	}
	
	//source component must be a file!
	if(source_it_p->file_type == EXT2_FT_DIR) {
		fprintf( stderr, strerror(EISDIR));
		fprintf( stderr, "\n");
		exit(1);
	}
	
	//get path of parent directory of link file to create
	extract_parentpath_component(argv[3], dest_parent_path, ln_component);
	
	/*does another component with the same name as 
	provided link file already exists?*/
	parsePath(argv[3], tokens_dest);
	
	lfile_it_p = getObjectInode(tokens_dest, lfile_it_p);
	
	//another object already exists with the same name as provided for link file
	if(lfile_it_p->inode != 0) {
		fprintf( stderr, strerror(EEXIST));
		fprintf( stderr, "\n");
		exit(1);
	}
	
	parsePath(dest_parent_path, tokens_dest_parent);
	parent_it_p = getObjectInode(tokens_dest_parent, parent_it_p);
	
	//destination path where link file to be created must be a directory!
	if(parent_it_p->file_type != EXT2_FT_DIR) {
		fprintf( stderr, strerror(ENOENT));
		fprintf( stderr, "\n");
		exit(1);
	}
	
	//create an entry for the link file in its parent directory
	insertRecord(parent_it_p->inode, source_it_p->inode, ln_component, EXT2_FT_REG_FILE);
	
	//increment number of links count for source file
	pn[source_it_p->inode -1].i_links_count++;
	
    return 0;
}