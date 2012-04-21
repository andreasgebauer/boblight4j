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

scp -r target/resources/* $REMOTE_ADDONS_DIR/service.multimedia.boblight4j-client-X11/
scp -r target/dependency/*.jar $REMOTE_ADDONS_DIR/service.multimedia.boblight4j-client-X11/bin