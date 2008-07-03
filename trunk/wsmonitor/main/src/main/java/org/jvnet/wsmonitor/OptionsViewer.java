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
