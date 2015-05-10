PORT=25487
CFLAGS= -DPORT=\$(PORT) -g -Wall

battle: battle.c
	gcc $(CFLAGS) -o battle $<

clean: 
	rm -f battle *.o