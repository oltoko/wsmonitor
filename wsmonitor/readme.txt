JAX-WS WSMonitor Tool
---------------------

wsmonitor (Web Services Monitor) is a light-weight SOAP and HTTP
traffic monitor. This tool intercepts and logs the SOAP messages
and HTTP headers between a sender and a receiver and displays
them in a graphical user interface. The wsmonitor listens the messages
on a specified port and forwards them to another target host and port.

The wsmonitor listen port, target host and target port are specified
in an XML-based configuration file. In the absence of this
configuration file, a default value of "4040" for listen port, "localhost"
for target host, "8080" for target port is assumed.

To start the wsmonitor tool, go to bin directory and invoke the
wsmonitor.bat script for windows platform and wsmonitor.sh script for
Unix-like platform.

Optionally, a configuration file may be provided to the tool as a
command-line argument. A sample configuration file is available in etc
directory.

The tool can be run in verbose mode by specifying -verbose command-line
option when invoking the script.

The name "wsmonitor" is aligned with wsimport and wsgen tools available
in JAX-WS.

Send comments to users@wsmonitor.dev.java.net.
