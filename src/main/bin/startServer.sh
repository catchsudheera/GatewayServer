#!/bin/sh

# Environment Variable Prerequisites
#
#   JAVA_HOME      Must point at your Java Development Kit installation (i.e. > JDK 1.7.x)
#
#

# if JAVA_HOME is not set 
if [ -z "$JAVA_HOME" ]; then
  echo "You must set the JAVA_HOME variable to a Java 1.7.x or higher JDK"
  exit 1
fi

# Check Java version
jdk_17=`$JAVA_HOME/bin/java -version 2>&1 | grep 1.7`

if [ -z "$jdk_17" ]; then
    echo "ServerGateway is currently certified against the Java Development Kit version 1.7.x for production use. It may work well with later versions as well."
fi

echo "Starting ServerGateway ..."
echo "Using JAVA_HOME  : $JAVA_HOME"

$JAVA_HOME/bin/java -jar GatewayServer.jar