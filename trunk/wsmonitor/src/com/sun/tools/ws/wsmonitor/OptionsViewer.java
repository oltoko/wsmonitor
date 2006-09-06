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
 $Id: OptionsViewer.java,v 1.1 2006-09-06 00:06:08 arungupta Exp $

 Copyright (c) 2006 Sun Microsystems, Inc.
 All rights reserved.
*/

package com.sun.tools.ws.wsmonitor;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JTabbedPane;

/**
 * @author Arun Gupta
 */
public class OptionsViewer extends JPanel {
    JRadioButton headerButton, bodyButton, bothButton;
    public enum SOAP_PART { HEADER, BODY, BOTH };

    private SOAP_PART soapPart;

    public OptionsViewer(JTabbedPane mainPane) {
        super(new BorderLayout());

        CustomListener cl = new CustomListener();
        headerButton = new JRadioButton("Only Header");
        headerButton.addActionListener(cl);
        bodyButton = new JRadioButton("Only Body");
        bodyButton.addActionListener(cl);
        bothButton = new JRadioButton("Header + Body");
        bothButton.addActionListener(cl);

        ButtonGroup group = new ButtonGroup();
        group.add(headerButton);
        group.add(bodyButton);
        group.add(bothButton);

        JPanel topPanel = new JPanel(new GridLayout(3,1));
        topPanel.add(headerButton);
        topPanel.add(bodyButton);
        topPanel.add(bothButton);
        topPanel.setVisible(true);
        add(topPanel, BorderLayout.NORTH);

        mainPane.addTab("Options", null, topPanel, "Options");
    }

    class CustomListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JRadioButton btn = (JRadioButton) e.getSource();
            if (btn == headerButton) {
                System.out.println("Only SOAP Header will be displayed.");
                soapPart = SOAP_PART.HEADER;
            } else if (btn == bodyButton) {
                System.out.println("Only SOAP Body will be displayed.");
                soapPart = SOAP_PART.BODY;
            } else if (btn == bothButton) {
                System.out.println("SOAP Header and Body will be displayed.");
                soapPart = SOAP_PART.BOTH;
            }
        }
    }

    public SOAP_PART getSoapPart() {
        return soapPart;
    }
}
