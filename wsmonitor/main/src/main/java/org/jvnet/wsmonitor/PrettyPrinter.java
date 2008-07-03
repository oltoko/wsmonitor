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

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.sax.SAXSource;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import org.w3c.dom.Document;
import java.lang.reflect.*;

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

    public static String FIToXML(byte[] in) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            if (in != null) {
                ByteArrayInputStream bais = new ByteArrayInputStream(in);

                Transformer tx = TransformerFactory.newInstance().newTransformer();
//                tx.transform(new FastInfosetSource(bais), new StreamResult(baos));
                Constructor c = Main.fiSource.getConstructor(InputStream.class);
                Object source = c.newInstance(new Object[]{bais});
                tx.transform((SAXSource)source, new StreamResult(baos));
                
                baos.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (in.length > 0 && baos.toString().length() < 0)
            return new String(in);
        else {
            //return baos.toString();
            return convertToXML(baos.toByteArray());
        }
        
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

        @Override
        public String toString() {
            return address + " " + data + " " + text;
        }
    }

}
