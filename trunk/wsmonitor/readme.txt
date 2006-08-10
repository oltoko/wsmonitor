JAX-WS WSMonitor Tool
---------------------

wsmonitor (Web Services Monitor) is a light-weight SOAP and HTTP
traffic monitor. This tool intercepts and logs the SOAP messages
and HTTP headers between a sender and a receiver and displays
them in a graphical user interface.

The tool uses port-forwarding to capture the traffic. In simple
language, when the tool is started it listens on listenPort port
on localhost and brings up a display window. A sender originally
sending request to http://targetHost:targetPort/somepath now sends
the request to http://localhost:listenPort/somepath. The wsmonitor
then forwards the request received at localhost:listenPort to
targetHost:targetPort, without any alteration of the message. In
between, it captures all the inbound and outbound SOAP and HTTP
traffic and displays in a nicely formatted way in the wsmonitor window.


The wsmonitor listen port, target host and target port are specified
in an XML-based configuration file. In the absence of this configuration
file, a default value of "4040" for listen port, "localhost" for target
host, "8080" for target port is assumed. A sample config file, along
with it's schema, is available in the etc directory. Each port forward
opens up as a new tab on the wsmonitor display window.
The name "wsmonitor" is aligned with wsimport and wsgen tools available
in JAX-WS.

The tool is ...
1). easy to use
2). light weight
3). displays formatted SOAP message
4). easily configurable

Download it and use it. Send comments to users@wsmonitor.dev.java.net.
