#!/usr/bin/perl
#tcpclient.pl

use IO::Socket;

$socket = new IO::Socket::INET (
                                  PeerAddr  => '127.0.0.1',
                                  PeerPort  =>  7778,
                                  Proto => 'tcp',
                               )                
or die "Couldn't connect to Server\n";

$input = `perl input.pl`; #launch a shell
$input2 = `perl input2.pl`; #touch

$socket->recv($recv_data,1024);
print "RECIEVED: $recv_data"; 

$socket->send($input);

$socket->recv($recv_data,1024);
print "RECIEVED: $recv_data";                                    

$socket->send($input2);

while (1) {
	
	print "\nSEND(TYPE quit to Quit):";
	
	$send_data = <STDIN>;
	$tmp=$send_data;
	
	chop($tmp); # get rid of new line
	
	if ($tmp ne 'quit') {
		$socket->send($send_data);
	}    else {
		$socket->send($send_data);
			close $socket;
			last;
	}
}     
    
