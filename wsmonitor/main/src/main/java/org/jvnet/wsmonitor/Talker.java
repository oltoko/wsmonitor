/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 *
 * Contributor(s):
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.jvnet.wsmonitor;

import org.jvnet.wsmonitor.config.ConnectionConfiguration;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jvnet.wsmonitor.model.ConnectionMetadata;
import static org.jvnet.wsmonitor.Constants.FAST_ENCODING;
import static org.jvnet.wsmonitor.Constants.XML_ENCODING;

/**
 * @author Arun Gupta
 */
public class Talker extends Thread {

    private Socket socket = null;
    private ConnectionConfiguration connConfig = null;
    private int contentLength = 0;
    private Hashtable<String, String> headersTable = new Hashtable<String, String>();
    private String methodName = null;
    private String fileName = null;
    private String protocolVersion = null;
    private ConnectionViewer connViewer = null;
    private ConnectionMetadata metadata = null;
    private static Logger logger = Logger.getLogger("org.jvnet.wsmonitor");

    public Talker(ConnectionViewer v, ConnectionConfiguration c, Socket s) {
        this.connViewer = v;
        this.socket = s;
        this.connConfig = c;
        metadata = new ConnectionMetadata();
    }

    @Override
    public void run() {
        try {
            metadata.setTime(new Date());

            //prepare the streams from host
            InputStream fromHost = socket.getInputStream();
            OutputStream toHost = socket.getOutputStream();

            // process request headers from "host"
            String requestHeaders = processRequestHeaders(fromHost);
            metadata.setRequestHeader(requestHeaders);

            // process request body from "host"
            byte[] requestMessage = processRequestBody(fromHost);
            metadata.setRequestBody(requestMessage);
            connViewer.updateRequest(metadata);

            HttpURLConnection targetServer;
            try {
                System.out.println(new Date() + ": Connecting to: " + connConfig.getTargetHost() + ":" + connConfig.getTargetPort() + fileName);

                URL url = new URL("http", connConfig.getTargetHost(), Integer.parseInt(connConfig.getTargetPort()),
                        fileName);
                targetServer = (HttpURLConnection) url.openConnection();
                targetServer.setRequestMethod(methodName);
                targetServer.setDoInput(true);

                // populate headers from "host" to "target"
                Enumeration headerEnum = headersTable.keys();
                while (headerEnum.hasMoreElements()) {
                    String header = (String) headerEnum.nextElement();
                    targetServer.setRequestProperty(header, headersTable.get(header));
                }

                if (methodName.contains("POST")) {
                    // open the output stream only for POST
                    targetServer.setDoOutput(true);
                    // write request to "target"
                    OutputStream toTarget = targetServer.getOutputStream();
                    toTarget.write(requestMessage);
                    toTarget.flush();
                    toTarget.close();
                }

                // check for HTTP response code
                boolean isFailure = checkResponseCode(targetServer);

                // process headers from "target"
                String responseHeader = processResponseHeaders(targetServer);
                metadata.setResponseHeader(responseHeader);

                // write response header to "host"
                toHost.write(responseHeader.concat("\n").getBytes());

                // process response body from "target"
                InputStream is = isFailure ? targetServer.getErrorStream() : targetServer.getInputStream();
                if (is != null) {
                    byte[] responseBuffer = processResponseBody(is);
                    metadata.setResponseBody(responseBuffer);
                    // write response body to "host"
                    toHost.write(responseBuffer);
                }

                toHost.flush();
                toHost.close();

            } catch (UnknownHostException e) {
                metadata.setResponseBody(e.getMessage().getBytes());
                e.printStackTrace();
            } catch (ConnectException e) {
                metadata.setResponseBody(e.getMessage().getBytes());
                e.printStackTrace();
            } catch (IOException e) {
                metadata.setResponseBody(e.getMessage().getBytes());
                e.printStackTrace();
            } catch (SecurityException e) {
                metadata.setResponseBody(e.getMessage().getBytes());
                e.printStackTrace();
            }

        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            connViewer.updateResponse(metadata);
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected String processRequestHeaders(InputStream is) throws IOException {
        log("***** Processing request headers");

        ArrayList<String> list = new ArrayList<String>();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] oneByte = new byte[1];
        boolean readAhead = false;
        boolean preamble = true;
        while (true) {
            if (!readAhead) {
                is.read(oneByte);
            } else {
                readAhead = false;
            // do not write \r \n
            }
            if (oneByte[0] != 10 && oneByte[0] != 13) {
                baos.write(oneByte);
            }
            if (oneByte[0] == 13) {
                continue;
            }
            if (oneByte[0] == 10) {
                readAhead = true;
                is.read(oneByte);
                if (oneByte[0] == 13) {
                    is.read(oneByte);       // read the "10"
                    processHeader(baos);
                    list.add(baos.toString());
                    baos.reset();
                    break;
                } else {
                    baos.flush();
                    if (preamble) {
                        processRequestPreamble(baos.toString());
                        preamble = false;
                    } else {
                        processHeader(baos);
                    }
                    list.add(baos.toString());
                    baos.reset();
                }
            }
        }

        String[] stringArray = list.toArray(new String[0]);
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < stringArray.length; i++) {
            if (stringArray[i].toLowerCase().startsWith("Content-Length".toLowerCase())) {
                processContentLength(stringArray[i]);
            } else if (stringArray[i].toLowerCase().startsWith("Content-Type".toLowerCase())) {
                metadata.setRequestEncoding(processContentType(stringArray[i]));
            }
            if (i != stringArray.length) {
                buffer.append(stringArray[i] + "\n");
            }
            log(stringArray[i]);
        }
        return buffer.toString();
    }

    private void processHeader(ByteArrayOutputStream baos) {
        String str = baos.toString();
        StringTokenizer st = new StringTokenizer(str, ":");
        headersTable.put(st.nextToken(), str.substring(str.indexOf(":") + 2, str.length()));
    }

    private void processRequestPreamble(String line) {
        StringTokenizer st = new StringTokenizer(line);
        if (st.hasMoreTokens()) {
            methodName = st.nextToken();
        }
        if (st.hasMoreTokens()) {
            fileName = st.nextToken();
        }
        if (st.hasMoreTokens()) {
            protocolVersion = st.nextToken();
        }
        metadata.setRequestPreamble(line);

        if (st.hasMoreTokens()) {
            throw new RuntimeException("Unknown value in HTTP header: " + st.nextToken());
        }
    }

    protected String processResponseHeaders(HttpURLConnection targetServer) throws Exception {
        log("***** Processing response headers");

        // read response headers from "target"
        Map<String, List<String>> responseHeaders = targetServer.getHeaderFields();
        ArrayList<String> headerList = new ArrayList<String>();

        // TODO: How to recreate "HTTP/1.1" string from response
        // For now, just copy the field from the request
        headerList.add(
                protocolVersion + " " + targetServer.getResponseCode() + " " + targetServer.getResponseMessage());
        for (String headerKey : responseHeaders.keySet()) {
            String header = "";
            if (headerKey != null) {
                if (headerKey.toLowerCase().equals("Transfer-Encoding".toLowerCase())) {
                    continue;
                }

                header = headerKey + ": ";
                for (String headerValue : responseHeaders.get(headerKey)) {
                    header += headerValue;
                }
                headerList.add(header);
                if (headerKey.startsWith("Content-Length")) {
                    processContentLength(headerKey + ": " + responseHeaders.get(headerKey).get(0));
                }
                if (header.toLowerCase().startsWith("Content-Type".toLowerCase())) {
                    metadata.setResponseEncoding(processContentType(header));
                }
            }
        }

        // collect the response headers
        String[] header = headerList.toArray(new String[0]);
        metadata.setResponsePreamble(header[0]);
        StringBuffer responseHeader = new StringBuffer();
        for (String h : header) {
            responseHeader.append(h + "\r\n");
            log(h);
        }

        return responseHeader.toString();
    }

    /**
     * Read from the input stream until Content-Length
     *
     * @param is
     * @return
     * @throws IOException
     */
    protected byte[] processRequestBody(InputStream is) throws IOException {
        log("***** Processing request body");

        if (is == null || contentLength == 0) {
            return new byte[0];
        }
        byte[] oneByte = new byte[1];
        int totalRead = 0;
        int read;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while (true) {
            read = is.read(oneByte);
            totalRead += read;
            baos.write(oneByte);
            if (totalRead == contentLength) {
                break;
            }
        }

        return baos.toByteArray();
    }

    /**
     * Read from the input stream until EOF
     *
     * @param istream
     * @return
     * @throws IOException
     */
    protected byte[] processResponseBody(InputStream istream) throws IOException {
        log("***** Processing response body");

        if (istream == null) {
            return new byte[0];
        }
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int num = 0;
        while ((num = istream.read(buf)) != -1) {
            bout.write(buf, 0, num);
        }
        byte[] ret = bout.toByteArray();
        metadata.setResponseContentLength(String.valueOf(ret.length));
        return ret;
    }

    /**
     * Process the Content-Length header to extract the content length
     *
     * @param line
     */
    private void processContentLength(String line) {
        StringTokenizer st = new StringTokenizer(line);
        String token = "";
        while (st.hasMoreTokens()) {
            token = st.nextToken();
        }

        // last token is the value of Content-Length header
        contentLength = Integer.parseInt(token);
        metadata.setRequestContentLength(token);
    }

    /**
     * Process Content-Type header to extract the content type of the body
     *
     * @param line
     * @return content type of the body
     */
    String processContentType(String line) {
        if (line.indexOf("text/xml") != -1) {
            return XML_ENCODING;
        }
        if (line.indexOf("application/soap+xml") != -1) {
            return XML_ENCODING;
        }
        if (line.indexOf("application/xop+xml") != -1) {
            return XML_ENCODING;
        }
        if (line.indexOf("application/fastinfoset") != -1) {
            return FAST_ENCODING;
        }
        return "";
    }

    /**
     * Returns false if there is an error received from the endpoint
     */
    private boolean checkResponseCode(HttpURLConnection httpConnection)
            throws IOException {
        boolean isFailure = false;
        try {
            int statusCode = httpConnection.getResponseCode();

            if ((statusCode == HttpURLConnection.HTTP_INTERNAL_ERROR) ||
                    (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) ||
                    (statusCode == HttpURLConnection.HTTP_NOT_FOUND) ||
                    (statusCode == HttpURLConnection.HTTP_MOVED_TEMP) ||
                    (statusCode == HttpURLConnection.HTTP_MOVED_PERM) ||
                    (statusCode < 200) ||
                    (statusCode >= 303 && statusCode < 500) ||
                    (statusCode >= 500)) {
                isFailure = true;
            }
        } catch (IOException e) {
            // on JDK1.3.1_01, we end up here, but then getResponseCode() succeeds!
            if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                isFailure = true;
            } else {
                throw e;
            }
        }

        return isFailure;
    }

    private void log(String msg) {
        logger.log(Level.INFO, new Date() + ": " + msg);
    }
}

