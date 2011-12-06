#!/bin/bash

BOBLIGHT_DIR=/storage/programs/boblight4j-0.0.2-SNAPSHOT-linux64/server/boblight4j-server-0.0.2-SNAPSHOT
DEBUGPORT=8000
DEBUG="-Xdebug -Xrunjdwp:transport=dt_socket,address="$DEBUGPORT",server=y,suspend=n"
JAVA=/storage/programs/java/jre/bin/java

usage(){
	echo "Usage"
}


$JAVA -Djava.rmi.server.hostname=zbox.local -Dcom.sun.management.jmxremote.port=9000 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false "$DEBUG" -Djava.library.path=$BOBLIGHT_DIR/lib/native -jar $BOBLIGHT_DIR/boblight4j-server-0.0.2-SNAPSHOT.jar -c $BOBLIGHT_DIR/boblight.15pc.conf &


