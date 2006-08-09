JAX-WS WSMonitor Tool
---------------------

This tool, wsmonitor (Web Services Monitor), intercepts and logs the SOAP 
messages and HTTP headers between a sender and a receiver and displays them
in a graphical user interface. The wsmonitor listens the messages on a
specified port and forwards them to another target host and port.

The wsmonitor listen port, target host and target port are specified in an
XML-based configuration file. In the absence of this configuration file, a
default value of "4040" for listen port, "localhost" for target host, "8080"
for target port is assumed.

To start the wsmonitor tool, go to bin directory and invoke the wsmonitor.bat
script for windows platform and wsmonitor.sh script for Unix-like platform.
Optionally, a configuration file may be provided to the tool as a command-line
argument. A sample configuration file is available in etc directory.

The following exception stack trace might be seen in the console when the 
SOAP messages and HTTP headers are displayed in the wsmonitor graphical
interface:

-- cut here --
org.xml.sax.SAXParseException: Premature end of file.
        at com.sun.org.apache.xerces.internal.parsers.DOMParser.parse(DOMParser.java:264)
        at com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderImpl.parse(DocumentBuilderImpl.java:292)
        at javax.xml.parsers.DocumentBuilder.parse(DocumentBuilder.java:98)
        at com.sun.tools.ws.wsmonitor.PrettyPrinter.convertToXML(PrettyPrinter.java:33)
        at com.sun.tools.ws.wsmonitor.ConnectionViewer.updateResponseUI(ConnectionViewer.java:220)
-- cut here --

and can be ignored.

Contact dev@wsmonitor.dev.java.net for any further questions.


