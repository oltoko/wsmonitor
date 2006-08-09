@echo off

REM
REM The contents of this file are subject to the terms
REM of the Common Development and Distribution License
REM (the License).  You may not use this file except in
REM compliance with the License.
REM
REM You can obtain a copy of the license at
REM https://glassfish.dev.java.net/public/CDDLv1.0.html.
REM See the License for the specific language governing
REM permissions and limitations under the License.
REM
REM When distributing Covered Code, include this CDDL
REM Header Notice in each file and include the License file
REM at https://glassfish.dev.java.net/public/CDDLv1.0.html.
REM If applicable, add the following below the CDDL Header,
REM with the fields enclosed by brackets [] replaced by
REM you own identifying information:
REM "Portions Copyrighted [year] [name of copyright owner]"
REM
REM Copyright 2006 Sun Microsystems Inc. All Rights Reserved
REM

set MONITOR_BIN_HOME=%~dp0
echo %MONITOR_BIN_HOME%

if defined JAVA_HOME goto CHECK_CMD_LINE_ARGS
echo ERROR: Set JAVA_HOME to the path where the J2SE (JDK) is installed (e.g., D:\jdk1.5)
goto END

:CHECK_CMD_LINE_ARGS

rem Get command line arguments and save them
set CMD_LINE_ARGS=
set DEBUG_OPTIONS=

:SET_ARGS
if "%1"=="" goto DONE_SET_ARGS
if "%1"=="-debug" goto SET_DEBUG
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto SET_ARGS

:SET_DEBUG
set DEBUG_OPTIONS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=8000
shift
goto SET_ARGS

:DONE_SET_ARGS

setlocal

set CLASSPATH=%MONITOR_BIN_HOME%..\lib\wsmonitor.jar;%MONITOR_BIN_HOME%..\lib\jsr173_api.jar;%MONITOR_BIN_HOME%..\lib\sjsxp.jar;%MONITOR_BIN_HOME%..\lib\args4j-2.0.6.jar;

%JAVA_HOME%\bin\java %DEBUG_OPTIONS% -cp "%CLASSPATH%" com.sun.tools.ws.wsmonitor.Main %CMD_LINE_ARGS%

endlocal

:END
