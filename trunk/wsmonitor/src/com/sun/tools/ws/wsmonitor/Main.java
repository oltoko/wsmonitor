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

import java.awt.Dimension;
import java.io.FileInputStream;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import org.kohsuke.args4j.CmdLineParser;

/**
 * @author Arun Gupta
 */
public class Main extends JFrame {
    static final int FRAME_WIDTH = 1200;
    static MainOptions options;

    private Main(List<ConnectionConfiguration> monitorConfiguration) {
        JTabbedPane mainPane = new JTabbedPane();
        this.getContentPane().add(mainPane);

        for (ConnectionConfiguration connectionConfiguration : monitorConfiguration) {
            ConnectionViewer v = new ConnectionViewer(mainPane, connectionConfiguration);
            Listener l = new Listener(v, connectionConfiguration);
            v.addListener(l);
        }

        this.setTitle("JAX-WS SOAP Monitor");
        JFrame.setDefaultLookAndFeelDecorated(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(FRAME_WIDTH,700));
        this.setResizable(true);
        this.pack();
        this.setVisible(true);
    }

    public static void main(String[] args) {
        try {
            MonitorConfiguration monitorConfiguration;
            options = new MainOptions();
            CmdLineParser argsParser = new CmdLineParser(options);
            argsParser.parseArgument(args);

            if (options.getArguments().isEmpty()) {
                System.err.println("Missing config file.");
                monitorConfiguration = usage();
            } else {
                FileInputStream fis = new FileInputStream(args[0]);
                MonitorConfigurationParser parser = new MonitorConfigurationParser();
                monitorConfiguration = parser.parse(fis);
            }

            new Main(monitorConfiguration);

        } catch (Throwable exp) {
            exp.printStackTrace();
        }
    }

    private static MonitorConfiguration usage() {
        MonitorConfiguration mc = new MonitorConfiguration();
        ConnectionConfiguration cc = new ConnectionConfiguration();
        cc.setName("Default");
        cc.setListenPort("4040");
        cc.setTargetHost("localhost");
        cc.setTargetPort("8080");
        cc.setDescription("Default");
        mc.add(cc);

        System.err.println("Usage: com.sun.tools.ws.wsmonitor.Main config.xml");
        System.err.println("       Using default 4040 --> localhost:8080");

        return mc;
    }
}
