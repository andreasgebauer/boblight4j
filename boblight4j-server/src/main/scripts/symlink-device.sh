#!/bin/bash

DEV_TGT=/dev/ttyUSB0
DEV_SRC=/dev/ttyACM0

if [ ! -c $DEV_SRC ];then
	if [ ! -c $DEV_TGT ];then
		ln -s $DEV_SRC $DEV_TGT
	fi
else
	echo "Device "$DEV_SRC" not ready"
	exit 1
fi
