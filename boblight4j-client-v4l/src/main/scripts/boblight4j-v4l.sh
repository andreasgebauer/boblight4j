#!/bin/bash

VIDEODEVNAME=/dev/video0
DEBUGPORT=8000
VERSION="0.0.2-SNAPSHOT"

DEBUG="-Xdebug -Xrunjdwp:transport=dt_socket,address="$DEBUGPORT",server=y,suspend=n"
RMI="-Djava.rmi.server.hostname=zbox.local -Dcom.sun.management.jmxremote.port=9001 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false"
DEBUGPORT=8001
DEBUG="-Xdebug -Xrunjdwp:transport=dt_socket,address="$DEBUGPORT",server=y,suspend=n"
V4LBASECMD=" -Djava.library.path=/home/agebauer/Documents/boblight/lib -jar /home/agebauer/Documents/boblight/boblight4j-client-v4l-"$VERSION".jar -c "$VIDEODEVNAME" -i 1 -w 120x96 -o speed=60.0 -o interpolation=true -o threshold=8 -o saturation=2 -o value=2.5"
V4LCMDNODEBUG="java "$V4LBASECMD
V4LCMD="java "$RMI" "$DEBUG" "$V4LBASECMD

# wait for video device
if [ ! -c $VIDEODEVNAME ]
then
printf $VIDEODEVNAME" does not exist"
while [ ! -c $VIDEODEVNAME ]
  do
  printf "." 
  sleep 1
done
fi

echo "found " $VIDEODEVNAME

$V4LCMDNODEBUG &

