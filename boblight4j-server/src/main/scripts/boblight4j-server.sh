#!/bin/bash

usage(){

}


DEVNAME=/dev/ttyUSB0
VIDEODEVNAME=/dev/video0
BOBLIGHT_DIR=/home/andi/Programs/boblight/boblight
DEBUGPORT=8000
DEBUG="-Xdebug -Xrunjdwp:transport=dt_socket,address="$DEBUGPORT",server=y,suspend=n"
PW='mi98'

if [ ! -c $DEVNAME ]
then
echo $PW | sudo -S ln -s /dev/ttyACM0 /dev/ttyUSB0
fi

java -Dlog4j.configuration= -Djava.rmi.server.hostname=zbox.local -Dcom.sun.management.jmxremote.port=9000 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false "$DEBUG" -Djava.library.path=$BOBLIGHT_DIR/lib -jar $BOBLIGHT_DIR/boblight4j-server-0.0.2-SNAPSHOT.jar -c $BOBLIGHT_DIR/boblight.15pc.conf &


