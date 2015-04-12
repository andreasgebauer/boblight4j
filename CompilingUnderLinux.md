# Preparation #

install necessary dependencies:

`sudo apt-get install sun-java6-jre maven2`
# Checkout #

`svn checkout http://boblight4j.googlecode.com/svn/trunk/ boblight4j`

This will check out boblight4j to the current working directory under the folder boblight4j.
# Compile #

cd into the created directory:

`cd boblight4j`

`mvn clean package`

will compile the code for the current platform.