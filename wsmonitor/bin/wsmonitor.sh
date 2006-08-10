#!/bin/sh

#
# The contents of this file are subject to the terms
# of the Common Development and Distribution License
# (the License).  You may not use this file except in
# compliance with the License.
#
# You can obtain a copy of the license at
# https://glassfish.dev.java.net/public/CDDLv1.0.html.
# See the License for the specific language governing
# permissions and limitations under the License.
#
# When distributing Covered Code, include this CDDL
# Header Notice in each file and include the License file
# at https://glassfish.dev.java.net/public/CDDLv1.0.html.
# If applicable, add the following below the CDDL Header,
# with the fields enclosed by brackets [] replaced by
# you own identifying information:
# "Portions Copyrighted [year] [name of copyright owner]"
#
# Copyright 2006 Sun Microsystems Inc. All Rights Reserved
#

if [ -z "$JAVA_HOME" ]; then
    echo "ERROR: Set JAVA_HOME to the path where the J2SE (JDK) is installed (e.g., /usr/java/jdk1.5)"
    exit 1
fi

if [ -z "$WSMONITOR_HOME" ]; then
    PRG=$0
    progname=`basename $0`
    saveddir=`pwd`

    cd `dirname $PRG`

    while [ -h "$PRG" ] ; do
        ls=`ls -ld "$PRG"`
        link=`expr "$ls" : '.*-> \(.*\)$'`
        if expr "$link" : '.*/.*' > /dev/null; then
            PRG="$link"
        else
            PRG="`dirname $PRG`/$link"
        fi
    done

    WSMONITOR_HOME=`dirname "$PRG"`/..
fi

CLASSPATH=$WSMONITOR_HOME/lib/wsmonitor.jar:$WSMONITOR_HOME/lib/jsr173_api.jar:$WSMONITOR_HOME/lib/sjsxp.jar:$WSMONITOR_HOME/lib/args4j-2.0.6.jar

$JAVA_HOME/bin/java -cp "$CLASSPATH" com.sun.tools.ws.wsmonitor.Main "$@"


