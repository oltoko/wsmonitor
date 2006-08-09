/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License).  You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the license at
 * https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.tools.ws.wsmonitor;

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

import static com.sun.tools.ws.wsmonitor.ConnectionMetadata.FAST_ENCODING;
import static com.sun.tools.ws.wsmonitor.ConnectionMetadata.XML_ENCODING;

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

    public Talker(ConnectionViewer v, ConnectionConfiguration c, Socket s) {
        this.connViewer = v;
        this.socket = s;
        this.connConfig = c;
        metadata = new ConnectionMetadata();
    }

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
                targetServer.setDoOutput(true);
                targetServer.setDoInput(true);

                // populate headers from "host" to "target"
                Enumeration headerEnum = headersTable.keys();
                while (headerEnum.hasMoreElements()) {
                    String header = (String) headerEnum.nextElement();
                    targetServer.setRequestProperty(header, headersTable.get(header));
                }

                // write request to "target"
                OutputStream toTarget = targetServer.getOutputStream();
                toTarget.write(requestMessage);
                toTarget.flush();
                toTarget.close();

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
            } else
                readAhead = false;
            // do not write \r \n
            if (oneByte[0] != 10 && oneByte[0] != 13)
                baos.write(oneByte);

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
            if (stringArray[i].toLowerCase().startsWith("Content-Length".toLowerCase()))
                processContentLength(stringArray[i]);
            else if (stringArray[i].toLowerCase().startsWith("Content-Type".toLowerCase()))
                metadata.setRequestEncoding(processContentType(stringArray[i]));

            if (i != stringArray.length)
                buffer.append(stringArray[i] + "\n");
        }
        return buffer.toString();
    }

    private void processHeader(ByteArrayOutputStream baos) {
        String str = baos.toString();
        StringTokenizer st = new StringTokenizer(str, ":");
        headersTable.put(st.nextToken(), str.substring(str.indexOf(":")+2, str.length()));
    }

    private void processRequestPreamble(String line) {
        StringTokenizer st = new StringTokenizer(line);
        if (st.hasMoreTokens())
            methodName = st.nextToken();

        if (st.hasMoreTokens())
            fileName = st.nextToken();

        if (st.hasMoreTokens())
            protocolVersion = st.nextToken();

        metadata.setRequestPreamble(line);

        if (st.hasMoreTokens())
            throw new RuntimeException("Unknown value in HTTP header: " + st.nextToken());
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
                if (headerKey.startsWith("Content-Length"))
                    processContentLength(headerKey + ": " + responseHeaders.get(headerKey).get(0));

                if (header.toLowerCase().startsWith("Content-Type".toLowerCase()))
                    metadata.setResponseEncoding(processContentType(header));
            }
        }

        // collect the response headers
        String[] header = headerList.toArray(new String[0]);
        metadata.setResponsePreamble(header[0]);
        StringBuffer responseHeader = new StringBuffer();
        for (String h : header)
            responseHeader.append(h + "\r\n");

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

        if (is == null || contentLength == 0)
            return new byte[0];

        byte[] oneByte = new byte[1];
        int totalRead = 0;
        int read;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while (true) {
            read = is.read(oneByte);
            totalRead += read;
            baos.write(oneByte);
            if (totalRead == contentLength)
                break;
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

        if (istream == null)
            return new byte[0];
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
        return ((line.indexOf("text/xml") != -1) || (line.indexOf("application/soap+xml") != -1) || (line.indexOf(
                "application/xop+xml") != -1)) ? XML_ENCODING : FAST_ENCODING;
    }

    /*
     * Will throw an exception instead of returning 'false' if there is no
     * return message to be processed (i.e., in the case of an UNAUTHORIZED
     * response from the servlet or 404 not found)
     */
    private boolean checkResponseCode(HttpURLConnection httpConnection)
            throws IOException {
        boolean isFailure = false;
        try {

            int statusCode = httpConnection.getResponseCode();
//            context.setProperty(StubPropertyConstants.HTTP_STATUS_CODE,
//                    Integer.toString(statusCode));
            if ((httpConnection.getResponseCode()
                    == HttpURLConnection.HTTP_INTERNAL_ERROR)) {
                isFailure = true;
                //added HTTP_ACCEPT for 1-way operations
            } else if (
                    httpConnection.getResponseCode()
                            == HttpURLConnection.HTTP_UNAUTHORIZED) {

                // no soap message returned, so skip reading message and throw exception
                throw new MonitorTransportException("http.client.unauthorized",
                                                    httpConnection.getResponseMessage());
            } else if (
                    httpConnection.getResponseCode()
                            == HttpURLConnection.HTTP_NOT_FOUND) {

                // no message returned, so skip reading message and throw exception
                throw new MonitorTransportException("http.not.found",
                                                    httpConnection.getResponseMessage());
            } else if (
                    (statusCode == HttpURLConnection.HTTP_MOVED_TEMP) ||
                            (statusCode == HttpURLConnection.HTTP_MOVED_PERM)) {
                isFailure = true;

//                if (!redirect || (redirectCount <= 0)) {
//                    throw new ClientTransportException("http.status.code",
//                            new Object[]{
//                                new Integer(statusCode),
//                                getStatusMessage(httpConnection)});
//                }
            } else if (
                    statusCode < 200 || (statusCode >= 303 && statusCode < 500)) {
                throw new MonitorTransportException("http.status.code",
                                                    statusCode, getStatusMessage(httpConnection));
            } else if (statusCode >= 500) {
                isFailure = true;
            }
        } catch (IOException e) {
            // on JDK1.3.1_01, we end up here, but then getResponseCode() succeeds!
            if (httpConnection.getResponseCode()
                    == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                isFailure = true;
            } else {
                throw e;
            }
        }

        return isFailure;

    }

    private String getStatusMessage(HttpURLConnection httpConnection)
            throws IOException {
        int statusCode = httpConnection.getResponseCode();
        String message = httpConnection.getResponseMessage();
        if (statusCode == HttpURLConnection.HTTP_CREATED
                || (statusCode >= HttpURLConnection.HTTP_MULT_CHOICE
                && statusCode != HttpURLConnection.HTTP_NOT_MODIFIED
                && statusCode < HttpURLConnection.HTTP_BAD_REQUEST)) {
            String location = httpConnection.getHeaderField("Location");
            if (location != null)
                message += " - Location: " + location;
        }
        return message;
    }

    private void log(String msg) {
        if (Main.options.isVerbose())
            System.out.println(new Date() + ": " + msg);
    }
}

