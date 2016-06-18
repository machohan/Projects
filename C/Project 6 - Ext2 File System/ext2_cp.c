#include "ext2_utils.h" 

/*
ext2_cp: 

This program takes three command line arguments. 

The first is the name of an ext2 formatted virtual disk. 
The second is the path to a file on your native operating system, 
and the third is an absolute path on your ext2 formatted disk. 

The program should work like cp, copying the file on your native file system onto the specified location on the disk. If the specified file or target location does not exist, then your program should return the appropriate error (ENOENT).
*/


/*
example usage:
./ext2_cp disk.img /home/alice/native.txt /path/to/target.txt 		 #the target file's name is always given

# if target.txt already exists, return error EEXIST
*/

//pointer to disk image in memory
extern unsigned char * disk;

// super block pointer sb
extern struct ext2_super_block *sb;

// block descriptor pointer bd
extern struct ext2_group_desc *bd;

//points to inode table
extern struct ext2_inode *pn; 

int main(int argc, char **argv) {
	
	if(argc != 4) {	
        fprintf(stderr, "Usage: readimg <image file name> absolute path source file <ex. /dir1/dir2/srcfile> destination path <ex. /dir4/dir5/linkfile>\n");
        exit(1);
    }
		
	loadImage(argv[1]); // loads disk image onto memory
	
	FILE * sourceFile;
	long fSize;
	char * buffer;
	size_t result;
	
	char * tokens_dest[MAX_TOKENS];
	char * tokens_dest_parent[MAX_TOKENS];
	
	char dest_parent_path[MAXPATH_LENGTH];
	char dest_component_name[MAX_FILE_NAME];
	
	struct comp_inode_type dest_i_t;
	struct comp_inode_type *dest_it_p = &dest_i_t;
	
	dest_i_t.inode = 0;
	dest_i_t.file_type = 0;
	
	struct comp_inode_type destparent_i_t;
	struct comp_inode_type *destparent_it_p = &destparent_i_t;
	
	destparent_i_t.inode = 0;
	destparent_i_t.file_type= 0;
	
	//open source file
	sourceFile = fopen ( argv[2] , "rb" );
	if (sourceFile==NULL) {
		fprintf( stderr, strerror(ENOENT));
		fprintf( stderr, "\n");
		exit(1);	
	}
	
	// obtain source file size:
	fseek (sourceFile , 0 , SEEK_END);
	fSize = ftell (sourceFile); // ftell gets the size of the source file
	rewind (sourceFile);		//reset file cursor
	
	
	/*if the file to be copied is too large, 
	aka won't fit in our file system, then exit 
	and report error*/
	if(fSize > (bd->bg_free_blocks_count * EXT2_BLOCK_SIZE)){
		fprintf( stderr, strerror(ENOSPC));
		fprintf( stderr, "\n");
		exit(1);
	}	
		
		
	/* extract parent name and component (last part) name of destiantion file e.g. /dir1/dir2 and target.txt in  /dir1/dir2/target.txt   */
	extract_parentpath_component(argv[3], dest_parent_path, dest_component_name);
	
	//make sure the destination file doesn't exist!
	parsePath(argv[3], tokens_dest);
	
	// get inode and type of destination path and check for valid path
	dest_it_p = getObjectInode(tokens_dest, dest_it_p); 
	
	//if another object with same name already exists, report error and exit
	if(dest_it_p->inode != 0) {
		fprintf( stderr, strerror(EEXIST));
		fprintf( stderr, "\n");
		exit(1);
	}
	
	parsePath(dest_parent_path, tokens_dest_parent);
	
	// get inode of the parent directory of destination path
	destparent_it_p = getObjectInode(tokens_dest_parent, destparent_it_p);
	
	//destination path where file to be placed must be a directory!
	if(destparent_it_p->file_type != EXT2_FT_DIR) {
		fprintf( stderr, strerror(ENOENT));
		fprintf( stderr, "\n");
		exit(1);
	}
	
	//get a vacant inode for the file to be created/copied 
	int finode = find_vacantInode();
	
	//create an entry for the file in its parent directory
	insertRecord(destparent_it_p->inode, finode, dest_component_name, EXT2_FT_REG_FILE);
	
	int num_blocksNeeded;
	if((fSize%EXT2_BLOCK_SIZE) == 0){
		num_blocksNeeded = fSize/EXT2_BLOCK_SIZE;
	}
	else{
		num_blocksNeeded = (fSize/EXT2_BLOCK_SIZE )+1;		
	}
	
	// allocate memory to contain the whole file:
	buffer = (char*) calloc (num_blocksNeeded, EXT2_BLOCK_SIZE);
	if (buffer == NULL) {
		fputs ("Memory error\n",stderr); 
		exit (2);
	}
	
	//read content of source file to buffer
	result = fread (buffer, 1, fSize, sourceFile);
	if (result != fSize) {
		fputs ("Reading error\n",stderr); 
		exit (3);
	}
	
	//find vacant data blocks for the file to be read;
	// array that stores data block numbers for the file
	int blocksForFile[MAX_NUM_BLOCKS]; 
	
	int j;
	/* get all the data block numbers for the file to be created and update block bit, super block and block 
	descriptor accordingly */
	
	for(j=0; j < num_blocksNeeded; j++){
		
		blocksForFile[j] = find_vacantblock();
		
		//set the corresponding bit in block bitmap
		update_bbitmap(blocksForFile[j]);
		sb->s_free_blocks_count--;
		bd->bg_free_blocks_count--;
	}
	
	//set number of i_blocks in inode table for the file
	pn[finode -1].i_blocks = num_blocksNeeded * SECTORS_PER_LB;
	
	//set i_block[] block references for the file
	int min = NUM_DIRECT_BLOCK;
	
	if(num_blocksNeeded < min){
		min = num_blocksNeeded;
	}
		
	//set direct block references in i_block[] 
	int i=0; 
	while(i < min){ 
		pn[finode -1].i_block[i] = blocksForFile[i]; 
		i++; 
	}
		
	//write from buffer to direct blocks 
	char * buffer_p = buffer;
	i=0;
	
	while(i < min){
		// write from buffer to direct data blocks of file
		
		//get pointer to data block
		unsigned char * dirBlock_p = 
		(unsigned char *)(disk + (pn[finode -1].i_block[i]* EXT2_BLOCK_SIZE)); 
		
		memcpy (dirBlock_p, buffer_p, EXT2_BLOCK_SIZE );
		i++;
		buffer_p += EXT2_BLOCK_SIZE;
	}
	
	/*if file has has more than 12 blocks, 
	initialize its singly indirect block to 
	references these blocks*/
	if(num_blocksNeeded > NUM_DIRECT_BLOCK){
		
		pn[finode -1].i_block[12] = find_vacantblock();
		
		//set the corresponding bit in block bitmap
		update_bbitmap(pn[finode -1].i_block[12]);
		
		sb->s_free_blocks_count--;
		bd->bg_free_blocks_count--;
		
		// pointer to the singly indirect block of file
		unsigned int * indirBlock_p = 
			(unsigned int *)(disk + (pn[finode -1].i_block[12]* EXT2_BLOCK_SIZE));
			
		int num_indirBlocks = num_blocksNeeded - NUM_DIRECT_BLOCK; 
			
		// j is the index of the first indirect block number in blocksForFile[]
		i=0, j = 12;
		
		/*  we can assume that a file can at most use the first 12 direct blocks and the singly indirect block; per Larry's instructions on piazza */
		while(i < num_indirBlocks){
			indirBlock_p[i] = blocksForFile[j];
			i++;
			j++;
		}
		
		//write from buffer to indirect blocks
		i=0;
		j=12;
		
		// write from buffer to indirect data blocks of file
		while(i < num_indirBlocks){
			
			//get pointer to first singly indirect data block
			unsigned char * indirBlock_p2 = 
			(unsigned char *)(disk + (blocksForFile[j]*EXT2_BLOCK_SIZE)); 
			memcpy (indirBlock_p2, buffer_p, EXT2_BLOCK_SIZE );	
			i++;
			j++;
			buffer_p += EXT2_BLOCK_SIZE;
		}
	
	}
	
	//update file size in inode table
	pn[finode -1].i_size = fSize;
	
	//update free inode count in super block and block descriptor
	bd->bg_free_inodes_count--;
	sb->s_free_inodes_count--;
	
    return 0;
}