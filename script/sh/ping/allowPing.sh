#!/bin/bash

allowPing=$1

echo $allowPing

if [ $allowPing == true ]; then
	iptables -I INPUT -p icmp --icmp-type echo-request -j ACCEPT
	iptables -A OUTPUT -p icmp --icmp-type echo-reply -j ACCEPT
else
	iptables -D INPUT -p icmp --icmp-type echo-request -j ACCEPT
	iptables -D OUTPUT -p icmp --icmp-type echo-reply -j ACCEPT
fi


service iptables save













