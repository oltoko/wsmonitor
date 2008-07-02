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

import java.util.Date;

import static org.jvnet.wsmonitor.ConnectionModel.DATE_FORMAT;

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
    
    public void clear() {
        requestBody = new byte[0];
        requestContentLength = "";
        requestHeader = "";
        
        responseBody = new byte[0];
        responseContentLength = "";
        responseHeader = "";
        
        dataId = 0;
    }
}
