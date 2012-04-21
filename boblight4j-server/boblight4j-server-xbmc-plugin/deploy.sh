#!bin/sh

if [ -z $XBMC_HOST ]; then
    echo "No \$XBMC_HOST environment variable set"
	exit 1
fi

if [ -z $XBMC_USER ]; then
    echo "No \$XBMC_USER environment variable set"
	exit 1
fi

if [ -z $XBMC_ADDONS_DIR ]; then
    echo "No \$XBMC_ADDONS_DIR environment variable set"
	exit 1
fi

REMOTE_ADDONS_DIR=$XBMC_USER@$XBMC_HOST:$XBMC_ADDONS_DIR

HOST="openelec"
ADDONS_PATH="/storage/.xbmc/addons"

scp -r target/resources/* $REMOTE_ADDONS_DIR/service.multimedia.boblight4j-server/
scp target/dependency/boblight4j-*server*.jar $REMOTE_ADDONS_DIR/service.multimedia.boblight4j-server/bin
#scp -r target/dependency/boblight4j-*server*/lib $REMOTE_ADDONS_DIR/service.multimedia.boblight4j-server/bin