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

import java.awt.Toolkit;
import java.awt.Dimension;
import java.io.FileInputStream;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import org.kohsuke.args4j.CmdLineParser;

/**
 * @author Arun Gupta
 * 08/21/06 Joe Wang: Added FastInfoset Support;
 *                    Fit the frame with a smaller resolution and position the window in the middle of the screen
 */
public class Main extends JFrame {

    static MainOptions cmdlineOptions;
//    OptionsViewer options;
    private static JTabbedPane mainPane = null;
    static final int FRAME_WIDTH = 1024;
    static final int FRAME_HEIGHT = 768;
    static Class fiSource = null;
    static boolean FI_SUPPORT = false;
    

    static {
        try {
            fiSource = Class.forName("org.jvnet.fastinfoset.FastInfosetSource");
            FI_SUPPORT = true;
        } catch (ClassNotFoundException e) {
            fiSource = null;
        }

    }

    private Main(List<ConnectionConfiguration> monitorConfiguration) {
        //frame size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int frameWidth = screenSize.width < FRAME_WIDTH ? screenSize.width : FRAME_WIDTH;
        int frameHeight = screenSize.height < FRAME_HEIGHT ? screenSize.height : FRAME_HEIGHT;

        mainPane = new JTabbedPane();
        this.getContentPane().add(mainPane);
        this.setTitle("Web Services SOAP Monitor");
        JFrame.setDefaultLookAndFeelDecorated(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //size and location
        this.setPreferredSize(new Dimension(frameWidth, frameHeight));
        this.setSize(new Dimension(frameWidth, frameHeight));
        this.setLocation((screenSize.width - getWidth()) / 2, (screenSize.height - getHeight()) / 2);
        this.setResizable(true);

        mainPane.setSize(frameWidth, frameHeight);
        for (ConnectionConfiguration connectionConfiguration : monitorConfiguration) {
            ConnectionViewer v = new ConnectionViewer(mainPane, connectionConfiguration);
            Listener l = new Listener(v, connectionConfiguration);
            v.addListener(l);
        }
//        options = new OptionsViewer(mainPane);

        this.pack();
        this.setVisible(true);
    }

    public static void main(String[] args) {
        try {
            MonitorConfiguration monitorConfiguration;
            
            if (args == null || args.length == 0) {
                monitorConfiguration = new MonitorConfiguration();
                monitorConfiguration.add(ConnectionConfiguration.DEFAULT_CONFIGURATION);
            } else {
                cmdlineOptions = new MainOptions();
                CmdLineParser argsParser = new CmdLineParser(cmdlineOptions);
                argsParser.parseArgument(args);

                if (cmdlineOptions.getArguments().isEmpty()) {
                    monitorConfiguration = new MonitorConfiguration();
                    monitorConfiguration.add(ConnectionConfiguration.DEFAULT_CONFIGURATION);
    //                monitorConfiguration = usage();
                } else {
                    FileInputStream fis = new FileInputStream(args[0]);
                    MonitorConfigurationParser parser = new MonitorConfigurationParser();
                    monitorConfiguration = parser.parse(fis);
                }                
            }

            new Main(monitorConfiguration);

        } catch (Throwable exp) {
            exp.printStackTrace();
        }
    }

    private static MonitorConfiguration usage() {
        MonitorConfiguration mc = new MonitorConfiguration();
        mc.add(ConnectionConfiguration.DEFAULT_CONFIGURATION);

        System.err.println("Usage: org.jvnet.wsmonitor.Main config.xml");
        System.err.println("       Using default 4040 --> localhost:8080");

        return mc;
    }
}