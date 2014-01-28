@echo off
:: -----------------------------------------------------------------------------
:: build.bat - Win32 Build Script for the Mica Graphics Framework and Applications
::
:: -----------------------------------------------------------------------------

:: ----- Check Environment Variables ------------------------------------------

if not "%JAVA_HOME%" == "" goto gotJavaHome
echo You must set JAVA_HOME to point at your Java Development Kit installation
goto cleanup
:gotJavaHome

if not "%MICA_HOME%" == "" goto gotMicaHome
set MICA_HOME=..
:gotMicaHome

if not "%ANT_HOME%" == "" goto gotAntHome
set ANT_HOME=%MICA_HOME%/build
:gotAntHome

if not "%JAXP_HOME%" == "" goto gotJaxpHome
set JAXP_HOME=%MICA_HOME%/build/lib
:gotJaxpHome

:: ----- Set Up The Ant Classpath ----------------------------------------------

set CP=%CLASSPATH%
for %%i in ("%ANT_HOME%\lib\*.jar") do call "%MICA_HOME%/build/cp.bat" %%i
set CP=%CP%;%MICA_HOME%\build\lib\preprocessor.jar

:: ----- Get the Command Line Arguments ----------------------------------------

set CMD_LINE_ARGS=
if not %1a==a goto setupArgs
set CMD_LINE_ARGS=all
goto doneSetupArgs

:setupArgs
if %1a==a goto doneSetupArgs
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto setupArgs
:doneSetupArgs

:: ----- Execute The Requested Build -------------------------------------------


@echo on
%JAVA_HOME%\bin\java.exe %ANT_OPTS% -classpath %CP% org.apache.tools.ant.Main -buildfile %MICA_HOME%/build/build.xml -Dant.home=%ANT_HOME% %CMD_LINE_ARGS% -DMICA_HOME=%MICA_HOME% -DJAXP_HOME=%JAXP_HOME%
@echo off

:: ----- Cleanup the environment -----------------------------------------------

:cleanup
set CP=
set CMD_LINE_ARGS=



