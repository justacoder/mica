

@echo off

rem Get the command line arguments. 

set CMD_LINE_ARGS=

:setupArgs
rem if %1a==a goto doneArgs
rem set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
rem shift
rem goto setupArgs

:doneArgs

if not "%MICA_HOME%"=="" goto runDemo

if not exist "../lib/mica.jar" goto noMicaHome

set MICA_HOME=../
goto runDemo

:noMicaHome
echo MICA_HOME is not set and mica.jar could not be located. Please set MICA_HOME.
goto end

:runDemo
java -classpath %CLASSPATH%;%MICA_HOME%/lib/mica.jar;%MICA_HOME% -DMi_HOME=%MICA_HOME% examples/%1 %2 %3 %4 %5 %6 %7 %8 %9
goto end

:end
set CMD_LINE_ARGS=




