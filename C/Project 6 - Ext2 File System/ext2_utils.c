#include "ext2_utils.h"


//pointer to disk image in memory
unsigned char * disk;

// super block pointer sb
struct ext2_super_block *sb;

// block descriptor pointer bd
struct ext2_group_desc *bd;

// inode bitmap pointer inode_p
unsigned char *inode_p;

//block bitmap pointer block_p
unsigned char *block_p;

// points to inode table
struct ext2_inode *pn;  
	
	/*
	Description: to load the disk image and initialize 
				pointers to super block, block group descriptor,
				inode bitmap, block bitmap and inode table
	INPUT: diskimage - pointer to the begining of the disk image
	Return void
	*/
	void loadImage(char * diskimage){
		//unsigned char * disk;
		int fd = open(diskimage, O_RDWR);
		if(fd == -1){
			perror("open");
			exit(1);	
		}
		
		// set global pointe disk to point to the beginning of the disk image in memory
		disk = mmap(NULL, 128 * EXT2_BLOCK_SIZE, PROT_READ | PROT_WRITE, MAP_SHARED, fd, 0);
		if(disk == MAP_FAILED) {
			perror("mmap");
			exit(1);
		}
		
		init_glob_pointers(disk);
	}
	
	/*
	Description: initialize global pointers to main blocks in disk image
	INPUT: diskimage - pointer to the beginning of the disk image
	Return void
	*/
	void init_glob_pointers(unsigned char * disk){
		// super block pointer sb
		sb = (struct ext2_super_block *)(disk + EXT2_BLOCK_SIZE);
		// block descriptor pointer bd
		bd = (struct ext2_group_desc *)(disk + (GROUP_DESCRIPTOR_BLOCK * EXT2_BLOCK_SIZE));
		//block bitmap pointer block_p
		block_p = (char *)(disk +(BLOCK_BITMAP_BLOCK * EXT2_BLOCK_SIZE));
		// inode bitmap pointer inode_p
		inode_p = (char *)(disk +(INODE_BITMAP_BLOCK * EXT2_BLOCK_SIZE));
		// points to inode table
		pn = (struct ext2_inode *)(disk + (INODE_TABLE_BLOCK * EXT2_BLOCK_SIZE));  
	}
	
	/*
	Description: to toggle the bitmap for the given inode
	INPUT: inode - inode number for which inode bitmap to be changed
	Return void
	*/
	void update_ibitmap(int inode){
		int byte_num;
		int bit_num;
		if((inode % 8) == 0){
			byte_num = (inode/8) -1;
			bit_num = 7;
		}
		else{
			byte_num = inode/8;
			bit_num = (inode % 8) - 1;
		}
		
		// is the bit for the inode set?
		if(inode_p[byte_num] & (1<<bit_num)) {
			//if set, clear the bit
			inode_p[byte_num] = inode_p[byte_num] & (~(1<<bit_num));
		}  
		else {
			//set the bit
			inode_p[byte_num] = inode_p[byte_num] | (1<<bit_num);
		}
	}
	
	/*
	Description: to check if bit for the given inode is set.
				This is for error checking incase the bit was not set.
	INPUT: inode - inode number for which inode bit in bitmap
			to be checked
	Return void
	*/
	int check_ibitmap(int inode){
		
		int byte_num;
		int bit_num;
		if((inode % 8) == 0){
			byte_num = (inode/8) -1;
			bit_num = 7;
		}
		else{
			byte_num = inode/8;
			bit_num = (inode % 8) - 1;
		}
		
		// is the bit for the inode set?
		if(inode_p[byte_num] & (1<<bit_num)) {
			return 1;
		}  
		
		// bit is clear
		return 0;
	}
	
	/*
	Description: to toggle the bitmap for the given data block
	INPUT: blocknum - data block number for which block bitmap to be changed
	Return void
	*/
	void update_bbitmap(int blocknum){
		int byte_num;
		int bit_num;
		if((blocknum % 8) == 0){
			byte_num = (blocknum/8) -1;
			bit_num = 7;
		}
		else{
			byte_num = blocknum/8;
			bit_num = (blocknum % 8) - 1;
		}
		
		// is the bit for the block set?
		if(block_p[byte_num] & (1<<bit_num)) {
			//if set, clear the bit
			block_p[byte_num] = block_p[byte_num] & (~(1<<bit_num));
		}  
		else {
			//set the bit
			block_p[byte_num] = block_p[byte_num] | (1<<bit_num);
		}
	}
	
	/*
	Description: to find a free inode from the disk in inode table
	INPUT: void
	Return: if successful, return inode number, 
			otherwise, ENOSPC error and terminate 
	*/
	int find_vacantInode(void){
		int i,j;
		int inode = -1;
		for(i=0; i<=3; i++){
			for(j=0; j<=7; j++){
				//if bit is set
				if((inode_p[i] & (1<<j)) == 0){
					inode = ( (i*8) + j+1 );
					return inode;
				}
			}
		}
		// when we reach this point, no vacant inode will have been found
		//return ENOSPC(aka no space left on device) and terminate
		fprintf( stderr, strerror(ENOSPC));
		fprintf( stderr, "\n");
		exit(1);	
	}
	
	/*
	Description: to find a free data block from the disk
	INPUT: void
	Return: if successful, return a block number, 
			otherwise, ENOMEM error and terminate 
	*/
	int find_vacantblock(void){
		int i,j;
		int block = -1;
		for(i=0; i<=15; i++){
			for(j=0; j<=7; j++){
				//if bit is set
				if((block_p[i] & (1<<j)) == 0){
					block = ( (i*8) + j+1 );
					return block;
				}
			}	
		}
		
		// when we reach this point, no vacant block will have been found
		//return ENOMEM (aka out of memory) and terminate
		fprintf( stderr, strerror(ENOMEM));
		fprintf( stderr, "\n");
		exit(1);
	}
	
	/*
	Description: to tokenize a given path.
		parses absPath into constituting 
		components and puts them into an 
		array of strings with a null entry 
		following the last token in the array.
	INPUT: absPath - path to tokenize
			tokens - array pointer to place tokens
	Return: void
	*/
	void parsePath(char * absPath, char * tokens[]){
		const char s[2] = "/";
		char *token;
	   
		// get the first token
		token = strtok(absPath, s);
		tokens[0] = token;
		
		// walk through other tokens
		int i = 1;
		while( token != NULL ) {
		  token = strtok(NULL, s);
		  tokens[i] = token;
		  i++;
	   }
	}
	
	/*
	Description: finds and returns the inode and
		type of the last object in the given 
		absolute path
	INPUT: tokens - pointer to array of tokens
			comp_i_t - pointer for struct which is updated
			with inode and file type if object is found.
	Return: struct comp_inode_type * comp_i_t
	*/
	struct comp_inode_type * getObjectInode(char * tokens[], struct comp_inode_type *comp_i_t){
		
		if(tokens[0]==NULL){
			comp_i_t->inode = ROOT_INODE;
			comp_i_t->file_type = EXT2_FT_DIR;
			return comp_i_t;
		}
		
		int last_dir_index = 0;
		while(tokens[last_dir_index]!= NULL){
			last_dir_index++;	
		}
		
		last_dir_index = last_dir_index - 2;
		
		int dirCheck = 1;
		int noDirCheck = 0;
		
		//first parent directory is root with inode 2
		comp_i_t->inode = ROOT_INODE; 
		comp_i_t->file_type = 0;
		int i = 0;
			while(tokens[i] != NULL){
				
				/* check if directories exist for given subcomponents
			    in the path before the last component*/ 
				if(i <= last_dir_index){
					getChildInode(tokens[i], comp_i_t->inode, dirCheck, comp_i_t);
				}
				else{
					getChildInode(tokens[i], comp_i_t->inode, noDirCheck, comp_i_t);
				}
				i++;
			}
			
		return comp_i_t;
	}
	
	/*
	Description: Called by getObjectInode function to
				check for existence of a file or a directory.
	INPUT: 	sub_component - an object for which
			parent_inode - parent inode for the sub_component
		    dirCheck - a flag to indicate whether to check
					   if sub_component is a directory.
		    comp_i_t - pointer for struct which is updated
					   with inode and file type if object is found.
	Return: struct comp_inode_type * comp_i_t
	*/
	void getChildInode(char * sub_component, int parent_inode, int dirCheck, struct comp_inode_type *comp_i_t){
		
		int i;
		int inode =0;
		
		int numOfBlocks = pn[parent_inode -1].i_blocks/SECTORS_PER_LB; 
		
		for (i=0; i< numOfBlocks; i++){
			
			struct ext2_dir_entry_2 * dblock_p = 
			(struct ext2_dir_entry_2 *)(disk + (pn[parent_inode - 1].i_block[i] *  EXT2_BLOCK_SIZE));
			
			int offset = 0;
			
			while(offset < EXT2_BLOCK_SIZE){
				if(strncmp(dblock_p->name,sub_component, dblock_p->name_len) == 0){
					
					// make sure sub_component is a directory!
					if(dirCheck){
						if((dblock_p->file_type & ENTRY_TYPE_MASK) != EXT2_FT_DIR){
							
							//return ENOTDIR and terminate
							fprintf( stderr, strerror(ENOTDIR));
							fprintf( stderr, "\n");
							exit(1);
						}	
					}
					
					comp_i_t->inode = dblock_p->inode;
					comp_i_t->file_type = dblock_p->file_type & ENTRY_TYPE_MASK; 
					return;
				} 	
				
				offset += dblock_p->rec_len;
				dblock_p = (struct ext2_dir_entry_2 *) (disk + (pn[parent_inode - 1].i_block[i] * EXT2_BLOCK_SIZE) + offset);
			}
				
		}
		// if you reach this point, then the component wasn't found
			
		//if it's the last component in the path, don't terminate; rather return 0 for inode
		if(dirCheck){
			//print ENOENT and terminate
			fprintf( stderr, strerror(ENOENT));
			fprintf( stderr, "\n");
			exit(1);
		}
		
		comp_i_t->inode=0;		
	}
	
	/*
	Description: Insert a record in a parent directory. 
	INPUT: 	parent_inode - parent inode of record to insert
		    recInode - newly assigned inode. Inode of new Object
		    name - name of the object
			type - type of the object (file or directory)
	Return: void
	*/
	void insertRecord(int parent_inode, int recInode, char* name, unsigned char type){
		
		//check that name is no bigger than 255
		// file name too big
		if(strlen(name) > MAX_FILE_NAME){
			//return ENAMETOOLONG and terminate
			fprintf( stderr, strerror(ENAMETOOLONG));
			fprintf( stderr, "\n");
			exit(1);
		}
		
		int blockNum;
		int numOfBlocks;
		struct ext2_dir_entry_2 *dblock_p;
		/* we allocate a new data block for the record, as per 
		Larry's instruction that each file or directory we 
		create get's its own data block. */
		
		// prone to fail if no vacant blocks are available and reports error 
		blockNum = find_vacantblock(); 
		update_bbitmap(blockNum);
		sb->s_free_blocks_count--;
		bd->bg_free_blocks_count--;
		
		// number of data blocks the parent directory has
		numOfBlocks = pn[parent_inode - 1].i_blocks/SECTORS_PER_LB; 
		
		// we are safe to assume that no directory will use an indirect block
		
		// assign a new data block
		pn[parent_inode - 1].i_block[numOfBlocks] = blockNum;

		//update i_blocks count
		pn[parent_inode - 1].i_blocks += SECTORS_PER_LB; 
		
		//we get the pointer to the data block
		dblock_p = (struct ext2_dir_entry_2 *)(disk + (blockNum * EXT2_BLOCK_SIZE));
		
		//we initialize the content of the directory entry
		dblock_p->inode = recInode;
		dblock_p->file_type = type;  
		dblock_p->name_len = strlen(name);
		
		// entry spans the whole data block, as per Larry's recommendation
		dblock_p->rec_len = EXT2_BLOCK_SIZE; 
		strncpy(dblock_p->name, name, MAX_FILE_NAME);
		dblock_p->name[MAX_FILE_NAME]='\0';
	}
	
	/*
	Description: delete a the object with inode comp_inode
	INPUT: 	parent_inode - parent inode of record to delete
		    comp_inode - inode of component to delete
	Return: void
	*/
	void delObject(int parent_inode, int comp_inode){
		
		int i;
		int found = 0;
		
		//find number of blocks associated with parent directory
		int numOfBlocks = pn[parent_inode -1].i_blocks/SECTORS_PER_LB; 
		
		
		//find component in parent directory
		for (i=0; i< numOfBlocks; i++){
			
			int offset = 0;
			
			// pointer to previous entry
			struct ext2_dir_entry_2 * prev_entry = NULL; 
			
			// pointer to the current entry in the parent directory
			struct ext2_dir_entry_2 * dblock_p = 
			(struct ext2_dir_entry_2 *)(disk + (pn[parent_inode - 1].i_block[i] *EXT2_BLOCK_SIZE));
			
			
			while(offset < EXT2_BLOCK_SIZE){
				
				if(dblock_p->inode == comp_inode){// record found
					
					// the requested delete must not be for a directory 
					if((dblock_p->file_type & ENTRY_TYPE_MASK) == EXT2_FT_DIR){ 
						//return EISDIR and terminate
						fprintf( stderr, strerror(EISDIR));
						fprintf( stderr, "\n");
						exit(1);
					}
				
					found = 1;
					
					// delete record
					if(prev_entry == NULL){
						
						/*only one entry in directory, 
						we deallocate the data block the entry is in*/
						
						update_bbitmap(pn[parent_inode - 1].i_block[i]);
						pn[parent_inode - 1].i_blocks -= SECTORS_PER_LB;
						sb->s_free_blocks_count++;
						bd->bg_free_blocks_count++;	
						
						/*shift i_block indices down to fill in 
						the gap of the deallocated block*/
						
						//get the new number of blocks of parent:
						int newNumOfBlocks = pn[parent_inode -1].i_blocks/SECTORS_PER_LB;
						
						//shift the i_block entries after i downwards
						int j = i;
						
						while(j < newNumOfBlocks){
							pn[parent_inode - 1].i_block[j] = 
							pn[parent_inode - 1].i_block[j+1];
							j++;
						}
						
					}
					else{
						/*Parent directory has previous entry, 
						delete by making the entry a padding for 
						the previous entry both rec_lengths are word aligned!*/
						
						prev_entry->rec_len += dblock_p->rec_len;
					}
					
					//if file or link has more than one link
					if(pn[comp_inode - 1].i_links_count > 1) {
						pn[comp_inode - 1].i_links_count--;
						return; // we're done!
					}
					
					//else only one link for comp_inode
					//clear bit in inode bitmap
					update_ibitmap(comp_inode);
					
					//update free inode count in super block and block descriptor
					sb->s_free_inodes_count++;
					bd->bg_free_inodes_count++;
					
					//deallocate all data blocks of deleted file in  block bitmap
					delete_all_blocks(comp_inode);	
					return;					
				} 	
				
				offset += dblock_p->rec_len;
				prev_entry = dblock_p;
				dblock_p = (struct ext2_dir_entry_2 *) (disk + (pn[parent_inode - 1].i_block[i] * EXT2_BLOCK_SIZE) + offset);
			}
		}
		
		if(!found){
			// if object is not found in parent directory print error
			
			/*this error should be caught in one of the 
			above functions before calling this function*/
			
			//print ENOENT and terminate
			fprintf( stderr, strerror(ENOENT));
			fprintf( stderr, "\n");
			exit(1);
		}
	}
	
	/*
	Description: delete all blocks associated with the given inode.
	INPUT: 	inode - inode number for which to delete all it's data blocks
	Return: void
	*/
	void delete_all_blocks(int inode){
		
		int i=0;
		int min = NUM_DIRECT_BLOCK; // number of direct blocks are 12
		
		//obtain total number of data blocks for this inode
		int numOfBlocks = pn[inode -1].i_blocks/SECTORS_PER_LB;
		
		if(numOfBlocks < min){
			min = numOfBlocks;
		}
		
		//deallocate direct blocks
		while(i < min){
			
			//update corresponding data block bit in block bitmap
			update_bbitmap(pn[inode -1].i_block[i]);
			
			sb->s_free_blocks_count++;
			bd->bg_free_blocks_count++;	
			i++;
		}
		
		if(numOfBlocks > NUM_DIRECT_BLOCK){
			
			// pointer to the singly indirect block of component
			unsigned int * indirBlock_p = 
			(unsigned int *)(disk + (pn[inode -1].i_block[12]* EXT2_BLOCK_SIZE)); 
			
			int num_indirBlocks = numOfBlocks - NUM_DIRECT_BLOCK;
			
			i=0;
			
			/*we can assume that a file can at most 
			use the first 12 direct blocks and the singly 
			indirect block as per Larry's instructions on piazza*/
			
			while(i < num_indirBlocks){
				
				//update corresponding data block bit in block bitmap
				update_bbitmap(indirBlock_p[i]);
				
				sb->s_free_blocks_count++;
				bd->bg_free_blocks_count++;
				i++;
			}
		}
	}
	
	/*
	Description: to extract parent directory path from the full path
				 and store it in parentpath and store last part of path
				 in component
	INPUT: 	fullpath - absolute path
			parentpath - pointer to char for parent directory
			component - pointer to store component
	Return: void
	*/
	void extract_parentpath_component(char * fullpath, char *parentpath, char* component){
		
		char * g= strrchr(fullpath, '/');
		char * l= strrchr(fullpath, '\0') -1;
		if(g == l){
			g[0] = '\0';
		}
		
		char * last = strrchr(fullpath, '/');
		strncpy(component, last+1, MAX_FILE_NAME);
		strncpy(parentpath, fullpath, last - fullpath +1);
		parentpath[g-fullpath+1]='\0';
		
	}