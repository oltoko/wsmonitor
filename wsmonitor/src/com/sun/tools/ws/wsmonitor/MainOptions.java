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
 $Id: MainOptions.java,v 1.1 2006-08-09 22:07:28 arungupta Exp $

 Copyright (c) 2006 Sun Microsystems, Inc.
 All rights reserved.
*/

package com.sun.tools.ws.wsmonitor;

import java.util.List;
import java.util.ArrayList;

import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.Argument;

/**
 * @author Arun Gupta
 */
public class MainOptions {
    @Option(name="-verbose", usage="display verbose information")
    private boolean verbose;

    @Argument
    private List arguments = new ArrayList();

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public List getArguments() {
        return arguments;
    }
}
