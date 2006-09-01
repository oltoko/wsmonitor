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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import static java.util.logging.Level.CONFIG;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import static com.sun.tools.ws.wsmonitor.ConnectionMetadata.XML_ENCODING;
import static com.sun.tools.ws.wsmonitor.ConnectionMetadata.FAST_ENCODING;
import static com.sun.tools.ws.wsmonitor.Main.FRAME_WIDTH;

/**
 * @author Arun Gupta
 *
 * 08/21/06 Joe Wang: added FI support.
 */
public class ConnectionViewer {
    private ConnectionModel connectionModel = null;
    private JSplitPane soapPane = null;
    private JSplitPane headersPane = null;
    private JSplitPane fiPane = null;
    private JTabbedPane mainPane = null;
    private JTabbedPane dataPane = null;
    private Listener listener = null;
    int frameWidth  = 500;


    public ConnectionViewer(JTabbedPane mainPane, ConnectionConfiguration connConfig) {
        this.mainPane = mainPane;
        frameWidth = mainPane.getWidth();

        JPanel panel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel(new GridLayout(1,3));
        topPanel.add(new JLabel("Listen port: " + connConfig.getListenPort()));
        topPanel.add(new JLabel("Target host:" + connConfig.getTargetHost()));
        topPanel.add(new JLabel("Target port: " + connConfig.getTargetPort()));
        topPanel.setVisible(true);
        panel.add(topPanel, BorderLayout.NORTH);

        JPanel midPanel = new JPanel(new BorderLayout());
        connectionModel = new ConnectionModel();
        JTable connectionTable = new JTable(connectionModel);
        connectionTable.setColumnSelectionAllowed(false);
        connectionTable.setRowSelectionAllowed(true);
        connectionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        connectionTable.setShowGrid(true);
        connectionTable.setPreferredScrollableViewportSize(new Dimension(frameWidth, 100));
        initColumnSizes(connectionTable);
        ListSelectionModel listSelectionModel = connectionTable.getSelectionModel();
        listSelectionModel.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                ListSelectionModel lsm = (ListSelectionModel) e.getSource();

                if (lsm.isSelectionEmpty())
                    return;

                // list is single selection mode
                // for convenience using getMinSelectionIndex but can use
                // getMaxSelectionIndex alternatively
                int index = lsm.getMinSelectionIndex();
                ConnectionMetadata cm = connectionModel.getDataAt(index);
                updateHeaderAndBodyUI(cm);
            }
        });

        JScrollPane scrollPane = new JScrollPane(connectionTable);
        scrollPane.setPreferredSize(new Dimension(50,100));
        midPanel.add(scrollPane);
        midPanel.setVisible(true);
        panel.add(midPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        dataPane = new JTabbedPane();
        dataPane.setPreferredSize(new Dimension(frameWidth/2,frameWidth/2));
        headersPane = createTextPanel();
        dataPane.addTab("HTTP Headers", null, headersPane, "HTTP Headers");
        soapPane = createTextPanel();
        dataPane.addTab("SOAP", null, soapPane, "SOAP");
        bottomPanel.add(dataPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        mainPane.addTab(connConfig.getName(), null, panel, connConfig.getDescription());        
    }

    private void initColumnSizes(JTable table) {
        ConnectionModel model = (ConnectionModel) table.getModel();
        TableColumn column;
        Component comp;
        int headerWidth;
        int cellWidth;
        String[] longValues = model.columnNames;
        TableCellRenderer headerRenderer =
                table.getTableHeader().getDefaultRenderer();

        for (int i = 0; i < longValues.length; i++) {
            column = table.getColumnModel().getColumn(i);

            comp = headerRenderer.getTableCellRendererComponent(null, column.getHeaderValue(),
                    false, false, 0, 0);
            headerWidth = comp.getPreferredSize().width;

            comp = table.getDefaultRenderer(model.getColumnClass(i)).
                    getTableCellRendererComponent(table, longValues[i],
                            false, false, 0, i);
            cellWidth = comp.getPreferredSize().width;

            column.setPreferredWidth(Math.max(headerWidth, cellWidth));
//            column.sizeWidthToFit();
        }
    }

    private JSplitPane createTextPanel() {
        Font font = new Font("monospaced", Font.PLAIN, 12);

        JTextArea requestText = new JTextArea();
        requestText.setEditable(false);
        requestText.setLineWrap(false);
        requestText.setWrapStyleWord(false);
        requestText.setFont(font);
        JScrollPane requestScrollPane = new JScrollPane(requestText);

        JTextArea responseText = new JTextArea();
        responseText.setEditable(false);
        responseText.setLineWrap(false);
        responseText.setWrapStyleWord(false);
        responseText.setFont(font);
        JScrollPane responseScrollPane = new JScrollPane(responseText);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, requestScrollPane, responseScrollPane);
        splitPane.setDividerLocation(frameWidth/2);
        splitPane.setVisible(true);

        return splitPane;
        
    }

    void update(ConnectionMetadata m) {
        updateHeaderAndBodyUI(m);
    }

    void updateRequest(ConnectionMetadata cm) {
        connectionModel.add(cm);
        updateRequestUI(cm);
    }

    void updateResponse(ConnectionMetadata cm) {
        connectionModel.update(cm);
        updateResponseUI(cm);
    }

    private void updateHeaderAndBodyUI(ConnectionMetadata cm) {
        updateRequestUI(cm);
        updateResponseUI(cm);
    }

    void updateFIPane(int index, byte[] in) {
        if (fiPane == null) {
            fiPane = createTextPanel();
            dataPane.addTab("FastInfoset", null, fiPane, "FI to XML");
        }
        
        JViewport tmp = (JViewport) ((JScrollPane) fiPane.getComponent(index)).getComponent(0);
        JTextArea fitoxmlRequest = (JTextArea) tmp.getView();
        String body = PrettyPrinter.FIToXML(in);
        fitoxmlRequest.setText(body);
        
    }
    
    private void updateRequestUI(ConnectionMetadata cm) {
        if (cm == null)
            return;

        Logger logger = Logger.getAnonymousLogger();

        // request headers panel
        JViewport tmp = (JViewport) ((JScrollPane) headersPane.getLeftComponent()).getComponent(0);
        JTextArea requestHeadersText = (JTextArea) tmp.getView();
        requestHeadersText.setText(cm.getRequestHeader());
        if (logger.isLoggable(CONFIG)) {
            System.out.println("***** Displaying request headers");
            System.out.println(cm.getRequestHeader());
        }

        // SOAP request panel
        tmp = (JViewport) ((JScrollPane) soapPane.getLeftComponent()).getComponent(0);
        JTextArea requestMessageText = (JTextArea) tmp.getView();
        String body = null;

        if (cm.getRequestEncoding() != null && cm.getRequestEncoding().equals(XML_ENCODING))
            body = PrettyPrinter.convertToXML(cm.getRequestBody());
        else if(cm.getRequestBody() != null)
            body = PrettyPrinter.convertToBinary(cm.getRequestBody());
        
        requestMessageText.setText(body);
        if (logger.isLoggable(CONFIG)) {
            System.out.println("***** Displaying request body");
            System.out.println(body);
        }
        
        //FI to XML request panel
        if (Main.FI_SUPPORT) {
            if (cm.getRequestEncoding() != null && cm.getRequestEncoding().equals(FAST_ENCODING)) 
            {
                updateFIPane(0, cm.getRequestBody());
            }            
        }        
        
    }
    
    private void updateResponseUI(ConnectionMetadata cm) {
        if (cm == null)
            return;
        
        Logger logger = Logger.getAnonymousLogger();

        // response headers panel
        JViewport tmp = (JViewport) ((JScrollPane) headersPane.getRightComponent()).getComponent(0);
        JTextArea responseHeadersText = (JTextArea) tmp.getView();
        responseHeadersText.setText(cm.getResponseHeader());
        if (logger.isLoggable(CONFIG)) {
            System.out.println("***** Displaying response headers");
            System.out.println(cm.getResponseHeader());
        }

        // SOAP response panel
        tmp = (JViewport) ((JScrollPane) soapPane.getRightComponent()).getComponent(0);
        JTextArea responseMessageText = (JTextArea) tmp.getView();
        String body;

        if (cm.getResponseEncoding() == null) {
            body = new String(cm.getResponseBody());
        } else {
            if (cm.getResponseEncoding().equals(XML_ENCODING))
                body = PrettyPrinter.convertToXML(cm.getResponseBody());
            else
                body = PrettyPrinter.convertToBinary(cm.getResponseBody());
        }
        responseMessageText.setText(body);
        if (logger.isLoggable(CONFIG)) {
            System.out.println("***** Displaying response body");
            System.out.println(body);
        }
        
        //FI to XML request panel
        if (Main.FI_SUPPORT) {
            if (cm.getResponseEncoding() != null && cm.getResponseEncoding().equals(FAST_ENCODING)) 
            {
                updateFIPane(1, cm.getResponseBody());
            }            
        }        
    }

    public void addListener(Listener l) {
        Listener listener = l;
    }

    public ConnectionModel getConnectionModel() {
        return connectionModel;
    }

    public JSplitPane getSoapPane() {
        return soapPane;
    }

    public JSplitPane getHeadersPane() {
        return headersPane;
    }
}
