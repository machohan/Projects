#!/bin/bash
#(a) The 10.10.10.* network is the public network. The 192.168.0.* is the private network.

#(j) All machines inside the private network have their default route set to 192.168.0.10. All external machines know nothing about the internal network. Their default route can be set to 10.10.10.99 (a non-real machine).

/sbin/iptables -F
/sbin/iptables -F -t nat
/sbin/iptables -F -t mangle

#default policy DROP
/sbin/iptables -P INPUT DROP
/sbin/iptables -P OUTPUT DROP
/sbin/iptables -P FORWARD DROP

/sbin/iptables -A INPUT -m state --state ESTABLISHED,RELATED -j ACCEPT
/sbin/iptables -A OUTPUT -m state --state ESTABLISHED,RELATED -j ACCEPT
/sbin/iptables -A FORWARD -m state --state ESTABLISHED,RELATED -j ACCEPT

#(b) Configuring firewall so that external http, https and smtp traffic can get web/mail server
/sbin/iptables -t nat -A PREROUTING -i eth0 -p tcp --match multiport --dports 80,25,443 -j DNAT --to 192.168.0.100
/sbin/iptables -A FORWARD -d 192.168.0.100 -p tcp --match multiport --dports 80,25,443 -j ACCEPT

#(c) Connect out port 25 to relay email from web/mail server.
/sbin/iptables -A INPUT -i eth1 -p tcp --sport 25 -m state --state ESTABLISHED -j ACCEPT
/sbin/iptables -A OUTPUT -o eth1 -p tcp --dport 25 -m state --state NEW,ESTABLISHED -j ACCEPT

#(d) Allowing 192.168.0.75 to ssh into the firewall. No other access into the firewall is permitted.
/sbin/iptables -A INPUT -p tcp -s 192.168.0.75 --dport 22 -j ACCEPT
/sbin/iptables -A OUTPUT -p tcp -d 192.168.0.75 --dport 22 -j ACCEPT

#(e) Allowing 10.10.10.75 (outside the local network) to ssh into the web/mail server by using port 2222 on the external side side of the firewall.
/sbin/iptables -t nat -A PREROUTING -p tcp -i eth0 -s 10.10.10.75 --dport 2222 -j DNAT --to-destination 192.168.0.100:22
/sbin/iptables -A FORWARD -p tcp -d 192.168.0.100 --dport 22 -m state --state NEW,ESTABLISHED,RELATED -j ACCEPT

#(f) You might want to check that no other IPs outside can ssh into the web/mail server and the firewall - CHECKED.

#(g) The CEO has a windows box inside the private network (192.168.0.33). The CEO (with fixed IP 10.10.10.33) would like to have remote desktop access to his desktop. Configure your firewall so that this is the case.
/sbin/iptables -A FORWARD -p tcp -i eth0 -s 10.10.10.33 -d 192.168.0.100 --dport 3389 -j ACCEPT

#(h) The CEO's windows machine should have RDP restricted so that only their external machine can connect (discuss what should be done to make sure this is the case).
#The firewall riles on the CEO's private windows machine(192.168.0.33) should be set such that the CEO's private machine only accept a connection from 10.10.10.33 on port 3389(RDP port) and drop all other requests.(see g for the firewall rule)

#(i) Sid, would also like RDP access to his windows box (with fixed IP 192.168.0.37) from his home at fixed IP 10.10.10.211. Can IP tables be used to do this as well? What if we want both to use the same RDP port on the firewall? No access to any services from 10.10.10.128 should be allowed.
#Yes, iptables can be used to set rules on firewall to allow Sid to access windows RDP(192168.0.37) from his home machine with ip 10.10.10.211. However, both cannot use same RDP port on the firewall for connections at the same time. To allow both machines to have RDP session, a new RDP port needs to be opened on either 192.168.0.37(Sid's Windows) or on 192.168.0.33(CEO's Windows) and update rules on firewall accordingly.
#No access to any services from 10.10.10.128 should be allowed.
/sbin/iptables -A INPUT -d 10.10.10.128 -j DROP

#(k) Finally, imagine that the only routable IP is 10.10.10.10. All internal machines should share this IP for internet traffic.
/sbin/iptables -t nat -A POSTROUTING -o eth0 -j MASQUERAGE