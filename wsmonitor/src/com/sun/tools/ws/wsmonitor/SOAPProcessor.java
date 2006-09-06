/*
 The contents of this file are subject to the terms
 of the Common Development and Distribution License
 (the "License").  You may not use this file except
 in compliance with the License.
 
 You can obtain a copy of the license at
 https://jwsdp.dev.java.net/CDDLv1.0.html
 See the License for the specific language governing
 permissions and limitations under the License.
 
 When distributing Covered Code, include this CDDL
 HEADER in each file and include the License file at
 https://jwsdp.dev.java.net/CDDLv1.0.html  If applicable,
 add the following below this CDDL HEADER, with the
 fields enclosed by brackets "[]" replaced with your
 own identifying information: Portions Copyright [yyyy]
 [name of copyright owner]
*/
/*
 $Id: SOAPProcessor.java,v 1.1 2006-09-06 00:06:08 arungupta Exp $

 Copyright (c) 2006 Sun Microsystems, Inc.
 All rights reserved.
*/

package com.sun.tools.ws.wsmonitor;

import java.io.ByteArrayInputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamConstants;

/**
 * @author Arun Gupta
 */
public class SOAPProcessor {
    XMLInputFactory inputFactory = XMLInputFactory.newInstance();

    XMLStreamReader newReader(String data) throws XMLStreamException {
        return inputFactory.createXMLStreamReader(new ByteArrayInputStream(data.getBytes()));
    }

    public String getHeader(String data) throws XMLStreamException {
        String header = null;

        XMLStreamReader reader = newReader(data);
        int state = reader.nextTag();
        while (state == XMLStreamConstants.START_ELEMENT) {
            
        }

        return header;
    }
}
