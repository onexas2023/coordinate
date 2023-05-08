#!/bin/bash

#put this file to /var/lib/boot2docker/bootlocal.sh in docker-machine's vm and chmod +x it
#it will be call when booting the docker vm

sudo kill `more /var/run/udhcpc.eth1.pid`

#change the ip to static one in your dev env
sudo ifconfig eth1 192.168.99.9 netmask 255.255.255.0 broadcast 192.168.99.255 up

sudo mkdir -p /docker-volumes

#change the sdb1 to disk to real one in your vm
sudo mount /dev/sdb1 /docker-volumes