#flags
CCFLAGS = gcc -g

all : ext2_ls ext2_cp ext2_mkdir ext2_ln ext2_rm 

#make executables
ext2_ls : ext2_utils.c ext2_ls.c
	$(CCFLAGS) $^ -o $@
		  
ext2_cp : ext2_utils.c ext2_cp.c
	$(CCFLAGS) $^ -o $@

ext2_mkdir : ext2_utils.c ext2_mkdir.c
	$(CCFLAGS) $^ -o $@

ext2_ln : ext2_utils.c ext2_ln.c
	$(CCFLAGS) $^ -o $@

ext2_rm : ext2_utils.c ext2_rm.c
	$(CCFLAGS) $^ -o $@

clean : 
	rm -f *.o ext2_ls ext2_cp ext2_mkdir ext2_ln ext2_rm
