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

import java.util.Date;

import static com.sun.tools.ws.wsmonitor.ConnectionModel.DATE_FORMAT;

/**
 * @author Arun Gupta
 */
public class ConnectionMetadata {
    private static int dataId = 0;
    private String id;
    private String time;
    private String requestEncoding;
    private String responseEncoding;
    private String requestPreamble;
    private String responsePreamble;
    private String requestContentLength;
    private String responseContentLength;
    private String requestHeader;
    private String responseHeader;
    private byte[] requestBody;
    private byte[] responseBody;

    static final String FAST_ENCODING = "Fast";
    static final String XML_ENCODING = "XML";

    public ConnectionMetadata() {
        this.id = String.valueOf(++dataId);
    }

    public ConnectionMetadata(Date time, String requestEncoding, String requestPreamble, String requestContentLength, String responseEncoding, String responsePreamble, String responseContentLength) {
        this(DATE_FORMAT.format(time), requestEncoding, requestPreamble, requestContentLength, responseEncoding,
             responsePreamble, responseContentLength);
    }

    public ConnectionMetadata(String time, String requestEncoding, String requestPreamble, String requestContentLength, String responseEncoding, String responsePreamble, String responseContentLength) {
        this();
        this.time = time;
        this.requestEncoding = requestEncoding;
        this.requestPreamble = requestPreamble;
        this.requestContentLength = requestContentLength;
        this.responseEncoding = responseEncoding;
        this.responsePreamble = responsePreamble;
        this.responseContentLength = responseContentLength;
    }

    public String getId() {
        return id;
    }

    public void setId(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setTime(Date time) {
        this.time = DATE_FORMAT.format(time);
    }

    public String getRequestEncoding() {
        return requestEncoding;
    }

    public void setRequestEncoding(String requestEncoding) {
        this.requestEncoding = requestEncoding;
    }

    public String getResponseEncoding() {
        return responseEncoding;
    }

    public void setResponseEncoding(String responseEncoding) {
        this.responseEncoding = responseEncoding;
    }

    public String getRequestPreamble() {
        return requestPreamble;
    }

    public void setRequestPreamble(String requestPreamble) {
        this.requestPreamble = requestPreamble;
    }

    public String getResponsePreamble() {
        return responsePreamble;
    }

    public void setResponsePreamble(String responsePreamble) {
        this.responsePreamble = responsePreamble;
    }

    public String getRequestContentLength() {
        return requestContentLength;
    }

    public void setRequestContentLength(String requestContentLength) {
        this.requestContentLength = requestContentLength;
    }

    public String getResponseContentLength() {
        return responseContentLength;
    }

    public void setResponseContentLength(String responseContentLength) {
        this.responseContentLength = responseContentLength;
    }

    public String getRequestHeader() {
        return requestHeader;
    }

    public void setRequestHeader(String requestHeader) {
        this.requestHeader = requestHeader;
    }

    public String getResponseHeader() {
        return responseHeader;
    }

    public void setResponseHeader(String responseHeader) {
        this.responseHeader = responseHeader;
    }

    public byte[] getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(byte[] requestBody) {
        this.requestBody = requestBody;
    }

    public byte[] getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(byte[] responseBody) {
        this.responseBody = responseBody;
    }
}
