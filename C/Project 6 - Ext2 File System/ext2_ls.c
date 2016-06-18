#include "ext2_utils.h"

/*
ext2_ls: 
This program takes two command line arguments. 
The first is the name of an ext2 formatted virtual disk. 
The second is an absolute path on the ext2 formatted disk. 

The program should work like ls -1 ("dash one", not the long format), 
printing each directory entry on a separate line. Unlike ls -1, it 
should also print . and ... In other words, it will print one line 
for every directory entry in the directory specified by the absolute 
path. If the directory does not exist print "No such file or directory".
*/

//pointer to disk image in memory
extern unsigned char * disk;

// points to inode table
extern struct ext2_inode *pn; 

int main(int argc, char **argv) {
	
    if(argc != 3) {
        fprintf(stderr, "Usage: readimg <image file name> absolute path <ex. /dir1/dir2/>\n");
        exit(1);
    }
	
	char * tokens[MAX_TOKENS];
	struct comp_inode_type comp_i_t;
	struct comp_inode_type *comp_it_p = &comp_i_t;
	
	parsePath(argv[2], tokens);
	
	comp_i_t.inode=0;
	comp_i_t.file_type=0;
	
	loadImage(argv[1]);
	
	comp_it_p = getObjectInode(tokens, comp_it_p);
   
	if(comp_it_p->inode == 0){
		// directory not found!
		fprintf( stderr, strerror(ENOENT));
		fprintf( stderr, "\n");
		exit(1);
	}
   
	// directory found! We list its entries
	int numOfBlocks = pn[comp_it_p->inode -1].i_blocks/SECTORS_PER_LB; 
	
	int i;
	for (i=0; i< numOfBlocks; i++){
		
		struct ext2_dir_entry_2 * dblock_p = 
		(struct ext2_dir_entry_2 *)(disk + (pn[comp_it_p->inode - 1].i_block[i]*EXT2_BLOCK_SIZE));
		
		int offset = 0;
	
		while(offset < EXT2_BLOCK_SIZE){
			printf("%.*s\n", dblock_p->name_len ,dblock_p->name);
			
			offset += dblock_p->rec_len;
			dblock_p = (struct ext2_dir_entry_2 *) (disk + (pn[comp_it_p->inode - 1].i_block[i]*EXT2_BLOCK_SIZE) + offset);
		}	
	}
   
    return 0;
}