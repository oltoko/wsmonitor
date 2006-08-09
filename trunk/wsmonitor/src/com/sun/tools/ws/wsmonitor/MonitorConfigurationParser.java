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

import java.io.FileInputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import static com.sun.tools.ws.wsmonitor.Constants.DESCRIPTION;
import static com.sun.tools.ws.wsmonitor.Constants.LISTEN_PORT;
import static com.sun.tools.ws.wsmonitor.Constants.NAME;
import static com.sun.tools.ws.wsmonitor.Constants.QNAME_CONNECTION;
import static com.sun.tools.ws.wsmonitor.Constants.QNAME_MONITOR;
import static com.sun.tools.ws.wsmonitor.Constants.TARGET_HOST;
import static com.sun.tools.ws.wsmonitor.Constants.TARGET_PORT;

/**
 * @author Arun Gupta
 */
public class MonitorConfigurationParser {
    public MonitorConfigurationParser() {
    }

    public MonitorConfiguration parse(FileInputStream fis) {
        try {
            XMLStreamReader reader = XMLInputFactory.newInstance()
                    .createXMLStreamReader(fis);
            return parseConfiguration(reader);
        } catch (XMLStreamException e) {
            throw new MonitorException(e);
        }
    }

    public MonitorConfiguration parseConfiguration(XMLStreamReader reader)
            throws XMLStreamException {
        MonitorConfiguration monitorConfiguration = new MonitorConfiguration();

        if (!reader.hasNext())
            throw new XMLStreamException("configuration.invalidElement");

        reader.nextTag();

        if (!reader.getName().equals(QNAME_MONITOR)) {
            throw new XMLStreamException("Expected: "
                    + QNAME_MONITOR.toString() + ", Got: "
                    + reader.getName().toString());
        }

        reader.nextTag();
        while (!reader.getName().equals(QNAME_MONITOR)) {
            if (reader.getName().equals(QNAME_CONNECTION)) {
                ConnectionConfiguration connectionConfiguration = parseConnectionConfiguration(reader);
                monitorConfiguration.add(connectionConfiguration);
            } else {
                System.out.println(reader.getName());
                throw new XMLStreamException("configuration.unexpectedContent");
            }
            reader.nextTag();
        }

        reader.close();

        return monitorConfiguration;
    }

    ConnectionConfiguration parseConnectionConfiguration(XMLStreamReader reader)
            throws XMLStreamException {
        ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration();

        System.out.println(reader.getName());
        System.out.println(reader.getLocation());
        int count = reader.getAttributeCount();
        if (count < 4)
            throw new XMLStreamException(
                    "Atleast name, listenPort, targetHost, targetPort attributes required");

        for (int i = 0; i < count; i++) {
            String name = reader.getAttributeName(i).getLocalPart();
            String value = reader.getAttributeValue(i);

            if (name.equals(NAME))
                connectionConfiguration.setName(value);
            else if (name.equals(DESCRIPTION))
                connectionConfiguration.setDescription(value);
            else if (name.equals(LISTEN_PORT))
                connectionConfiguration.setListenPort(value);
            else if (name.equals(TARGET_HOST))
                connectionConfiguration.setTargetHost(value);
            else if (name.equals(TARGET_PORT))
                connectionConfiguration.setTargetPort(value);
        }

        if (reader.getEventType() != XMLStreamConstants.END_ELEMENT)
            reader.nextTag();
        
        return connectionConfiguration;
    }
}
