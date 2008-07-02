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

import java.io.FileInputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import static org.jvnet.wsmonitor.Constants.DESCRIPTION;
import static org.jvnet.wsmonitor.Constants.LISTEN_PORT;
import static org.jvnet.wsmonitor.Constants.NAME;
import static org.jvnet.wsmonitor.Constants.QNAME_CONNECTION;
import static org.jvnet.wsmonitor.Constants.QNAME_MONITOR;
import static org.jvnet.wsmonitor.Constants.TARGET_HOST;
import static org.jvnet.wsmonitor.Constants.TARGET_PORT;

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

    private MonitorConfiguration parseConfiguration(XMLStreamReader reader)
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

    private ConnectionConfiguration parseConnectionConfiguration(XMLStreamReader reader)
            throws XMLStreamException {
        ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration();

        System.out.println(reader.getName());
        System.out.println(reader.getLocation());
        int count = reader.getAttributeCount();
        if (count < 3)
            throw new XMLStreamException(
                    "Atleast name, listenPort, targetPort attributes required");

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
