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
package org.jvnet.wsmonitor.model;

import org.jvnet.wsmonitor.*;
import java.text.SimpleDateFormat;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

/**
 * @author Arun Gupta
 */
public class ConnectionModel extends AbstractTableModel {
    private int numRows = 0;
    static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS yyyy-MM-dd");

    String[] columnNames = {
            "Id",
            "Time Sent",
            "Request Encoding",
            "Request Preamble",
            "Request Length",
            "Response Encoding",
            "Response Preamble",
            "Response Length"
    };
    private Vector<ConnectionMetadata> data = null;

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
    
    public String[] getColumnNames() {
        return columnNames;
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

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }
}
