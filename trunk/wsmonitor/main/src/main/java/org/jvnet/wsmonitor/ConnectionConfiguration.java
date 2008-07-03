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

/**
 * @author Arun Gupta
 */
class ConnectionConfiguration {
    public static final ConnectionConfiguration DEFAULT_CONFIGURATION = new ConnectionConfiguration();
    
    static {
        DEFAULT_CONFIGURATION.setName("Default");
        DEFAULT_CONFIGURATION.setListenPort("4040");
        DEFAULT_CONFIGURATION.setTargetHost("localhost");
        DEFAULT_CONFIGURATION.setTargetPort("8080");
        DEFAULT_CONFIGURATION.setDescription("Default");
    }
    
    private String name;
    private String description;
    private String listenPort;
    private String targetHost = "localhost";
    private String targetPort;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getListenPort() {
        return listenPort;
    }

    public void setListenPort(String listenPort) {
        this.listenPort = listenPort;
    }

    public String getTargetHost() {
        return targetHost;
    }

    public void setTargetHost(String targetHost) {
        this.targetHost = targetHost;
    }

    public String getTargetPort() {
        return targetPort;
    }

    public void setTargetPort(String targetPort) {
        this.targetPort = targetPort;
    }
    
    @Override
    public String toString() {
        return getName() + " " + getListenPort() + " -> " + 
                getTargetHost() + ":" + getTargetPort();
    }
}
