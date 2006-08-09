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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import org.w3c.dom.Document;

/**
 * @author Arun Gupta
 */
public class PrettyPrinter {
    public static String convertToXML(byte[] in) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            if (in != null) {
                XMLSerializer serializer = new XMLSerializer(baos, new OutputFormat("XML", "UTF-8", true));

                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                ByteArrayInputStream bais = new ByteArrayInputStream(in);
                Document doc = db.parse(bais);
                serializer.serialize(doc);
                baos.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (in != null && in.length > 0)
                return new String(in);
        }

        if (in != null && in.length > 0 && baos.toString().length() < 0)
            return new String(in);
        else
            return baos.toString();
    }

    public static String convertToBinary(byte[] in) {
        ArrayList<Dump> list = new ArrayList<Dump>();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(baos);

        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        PrintWriter pw2 = new PrintWriter(baos2);

        for (int i = 0; i < in.length;) {
            Dump dump = new Dump();
            baos.reset();
            pw.printf("%06x", i);
            pw.flush();

            dump.setAddress(baos.toString());
            baos.reset();

            int j = 0;
            for (; j < 16 && i < in.length; j++, i++) {
                pw.printf("%02x ", new Byte(in[i]));
                if (in[i] >= 32 && in[i] <= 127)
                    pw2.printf("%c", new Byte(in[i]));
                else
                    pw2.print(".");
                pw2.flush();
            }
            if (j < 16) {
                for (; j < 16; j++) {
                    pw.printf("   ");
                }
            }
            pw.flush();
            dump.setData(baos.toString());
            dump.setText(baos2.toString());
            list.add(dump);
            baos.reset();
            baos2.reset();
        }

        Dump[] dumpArray = list.toArray(new Dump[0]);
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < dumpArray.length; i++) {
            buffer.append(dumpArray[i].toString() + "\n");
        }

        return buffer.toString();
    }

    static final class Dump {
        private String address;
        private String data;
        private String text;

        public Dump() {
        }

        public Dump(String address, String data, String text) {
            this.address = address;
            this.data = data;
            this.text = text;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String toString() {
            return address + " " + data + " " + text;
        }
    }

}
