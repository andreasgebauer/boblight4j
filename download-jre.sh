JRE_LINUX=http://download.oracle.com/otn-pub/java/jdk/7u1-b08/
JRE_LINUX_X64=jre-7u1-linux-x64.tar.gz
JRE_LINUX_X86=jre-7u1-linux-i586.tar.gz

INSTALL_DIR=jre1.7.0_01

if [ -d jre ]
then
	echo jre directory already exists
	exit 1
fi

if [ $# -eq 0 ]
then
	echo "You have to specify the architecture like x86_64"
	exit 1
fi

if [ $1 = "x86_64" ]
then
	DOWNLOAD=$JRE_LINUX$JRE_LINUX_X64
else
	DOWNLOAD=$JRE_LINUX$JRE_LINUX_X86
fi

echo $DOWNLOAD

FILE=jre.tar.gz

wget -O $FILE $DOWNLOAD

tar -zxvf $FILE 


mv $INSTALL_DIR jre
