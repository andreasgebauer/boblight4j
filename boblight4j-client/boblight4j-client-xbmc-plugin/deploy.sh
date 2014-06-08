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

BASEDIR="$( cd "$( dirname "$0" )" && pwd )"

REMOTE_ADDONS_DIR=$XBMC_USER@$XBMC_HOST:$XBMC_ADDONS_DIR

scp -r $BASEDIR/target/resources/* $REMOTE_ADDONS_DIR/service.multimedia.boblight4j-client/
scp $BASEDIR/target/service.multimedia.*.jar $REMOTE_ADDONS_DIR/service.multimedia.boblight4j-client/bin