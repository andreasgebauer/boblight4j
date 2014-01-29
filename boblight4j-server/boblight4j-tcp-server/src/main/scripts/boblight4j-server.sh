#!/bin/bash

BOBLIGHT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

JAVA=java
CONF=$BOBLIGHT_DIR/conf/boblight.conf

# DEBUG
DEBUGPORT=8000
DEBUG="-Xdebug -Xrunjdwp:transport=dt_socket,address="$DEBUGPORT",server=y,suspend=n"

DEVICE=$(grep output $CONF)
DEVICE=${DEVICE#output}

JMX_OPTIONS="-Djava.rmi.server.hostname=raspberrypi -Dcom.sun.management.jmxremote.port=9000 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false"
JVM_OPTIONS="-Dgnu.io.rxtx.SerialPorts=$DEVICE $DEBUG"

$JAVA $JVM_OPTIONS -Djava.library.path=$BOBLIGHT_DIR/lib/native -cp $BOBLIGHT_DIR/conf;$BOBLIGHT_DIR/${project.artifactId}-${project.version}.jar org.boblight4j.server.BoblightDaemon -c $CONF &
