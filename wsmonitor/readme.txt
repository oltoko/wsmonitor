Web Services Monitor Tool
-------------------------

wsmonitor (Web Services Monitor) is a light-weight SOAP and HTTP
traffic monitor. This tool intercepts and logs the SOAP messages
and HTTP headers between a sender and a receiver and displays
them in a graphical user interface. The wsmonitor listens the messages
on a specified port and forwards them to another target host and port.

The wsmonitor listen port, target host and target port are specified
in an XML-based configuration file. In the absence of this
configuration file, a default value of "4040" for listen port, "localhost"
for target host, "8080" for target port is assumed.

The name "wsmonitor" is aligned with wsimport and wsgen tools available
in JAX-WS.

The tool logs messages using JDK logging.

How to build ?
--------------

mvn install

How to run ?
------------

java -jar main/target/wsmonitor-main-1.0-SNAPSHOT-jar-with-dependencies.jar

How to send comments ?
----------------------

Send comments to users@wsmonitor.dev.java.net.

