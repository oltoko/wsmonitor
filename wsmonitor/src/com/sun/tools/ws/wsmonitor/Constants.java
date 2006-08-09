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

import javax.xml.namespace.QName;

/**
 * @author Arun Gupta
 */
public class Constants {
    private static final String NS_NAME = "http://java.sun.com/xml/ns/jax-ws/ri/config/monitor";
    public static final QName QNAME_MONITOR = new QName(NS_NAME, "monitor");
    public static final QName QNAME_CONNECTION = new QName(NS_NAME, "connection");
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String LISTEN_PORT = "listenPort";
    public static final String TARGET_HOST = "targetHost";
    public static final String TARGET_PORT = "targetPort";
}
