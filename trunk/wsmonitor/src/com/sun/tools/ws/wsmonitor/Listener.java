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

import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;

/**
 * @author Arun Gupta
 */
public class Listener extends Thread {
    protected final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    ConnectionConfiguration connConfig = null;
    ConnectionViewer connViewer = null;

    public Listener(ConnectionViewer v, ConnectionConfiguration connConfig) {
        this.connConfig = connConfig;
        this.connViewer = v;
        start();
    }

    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(Integer.parseInt(connConfig.getListenPort()));
            System.out.println("Waiting for connections at " + serverSocket.getLocalPort());
            while (true) {
                Socket socket = serverSocket.accept();
                Talker talker = new Talker(connViewer, connConfig, socket);
                talker.start();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}
