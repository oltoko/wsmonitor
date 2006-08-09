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

import java.text.SimpleDateFormat;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

/**
 * @author Arun Gupta
 */
public class ConnectionModel extends AbstractTableModel {
    protected int numRows = 0;
    protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS yyyy-MM-dd");

    protected String[] columnNames = {
            "Id",
            "Time Sent",
            "Request Encoding",
            "Request Preamble",
            "Request Length",
            "Response Encoding",
            "Response Preamble",
            "Response Length"
    };
    Vector<ConnectionMetadata> data = null;

    public ConnectionModel() {
        data = new Vector<ConnectionMetadata>();
    }

    public void add(ConnectionMetadata c) {
        data.addElement(c);
        numRows++;
        fireTableRowsInserted(numRows, numRows);
    }

    public void update(ConnectionMetadata c) {
        data.setElementAt(c, numRows - 1);
        fireTableRowsUpdated(numRows, numRows);
    }

    public ConnectionMetadata getDataAt(int index) {
        if (data == null)
            return null;

        return data.get(index);
    }

    public synchronized void clear() {
        data.removeAllElements();
        fireTableRowsDeleted(0, numRows);
        numRows = 0;
    }

    public int getRowCount() {
        return numRows;
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public Object getValueAt(int row, int col) {
        ConnectionMetadata c = data.get(row);
        switch (col) {
            case 0:
                return c.getId();
            case 1:
                return c.getTime();
            case 2:
                return c.getRequestEncoding();
            case 3:
                return c.getRequestPreamble();
            case 4:
                return c.getRequestContentLength();
            case 5:
                return c.getResponseEncoding();
            case 6:
                return c.getResponsePreamble();
            case 7:
                return c.getResponseContentLength();
            default:
                return null;
        }
    }

    public boolean isCellEditable(int row, int col) {
        return false;
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }
}
