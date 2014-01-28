
                     Software Farm Mica Graphics Framework

Overview

    This is the Software Farm Mica Graphics Framework for JAVA.
    It provides a complete graphics user-interface toolkit for Java
    and also contains various useful graphics applications and the
    support to make similar applications. Source code is provided.

    This version of the framework was built against the 1.2 JDK. The
    1.2 version of the JDK included some features which were not
    100% backwards compatible with older versions of the JDK, so the
    classes in this framework may not behave correctly with JDK
    version 1.0.2. 

    This framework provides a user-interface toolkit which contains
    most of the popular widgets. These include tables/grids, combo boxes,
    gauges(progress bars), pie charts, status bars, toolbars, ...etc.
    Any widget can contain or be contained by any widget or graphics.

    This framework provides graphics shapes and manipulation routines
    such as rectangles, ellipses, polygons, polypoints, arrows, images,
    text, etc. shapes and zoom in/out, pan, select, move, etc. manipulators
    and world to device transforms. Any widget or graphics shape can be
    manipulated or transformed.

    This framework provides a number of larger part assemblies such as
    many pre-built menubar menus, many types of property sheets, drag-
    and-drop palettes, font/color/line width/line end choosers, etc.
    These can be used by an application developer in order to speed
    development.

    This framework provides a number of applications that can be used
    as written or customized by an application developer in order to 
    speed development.
    
Legalities

    Please see the accompanying license agreement for information about
    using and distributing this package.

Installation

    Application programs are contained in the apps subdirectory.
    Example programs are contained in the examples subdirectory.

    To conveniently run the application programs modify your 'path'
    environment variable to point to the mica/bin subdirectory. Or,
    just double click the .bat files if you are running MSWindows.

    Documentation in text and pdf format is contained in the docs
    subdirectory. Further API documentation is found in the
    docs/javadocs subdirectory.

    There are two ways to use the classes which comprise the framework.

    The first is to install the jar files containing the classes.
    Installation of this type is described in the "Installation of 
    Jar File" section below.

    The second way is to install the classes themselves. This is
    described in the "Installation of Raw Classes" section.

Installation of Jar Files

    The jar class file "mica.jar" is located in the lib subdirectory 
    of this package.

    You can copy the jar file anywhere on the target machine or leave
    it where it is. After installing the jar file on the target machine,
    you must set the CLASSPATH environment variable to include the jar 
    files before you run Java, Netscape, appletviewer, javac or other 
    programs which would need to access the framework.

    For example, if the jar files are in Mica "lib" directory and Mica
    is installed in c:\, then you would need to modify your CLASSPATH
    environment variable to include the jar files of the Mica Graphics 
    Framework:

    set CLASSPATH=%CLASSPATH%;c:\mica\lib\mica.jar

    Under UNIX, you need to perform basically the same thing. If Mica is
    installed in /home then you would need to modify your CLASSPATH 
    environment variable to include the jar files of the Mica Graphics
    Framework:

    setenv CLASSPATH "${CLASSPATH}:/home/mica/lib/mica.jar

    Note that under UNIX, you should use colons to separate entries and
    under Windows, you should use semi-colons to separate entries.

    You can run the Mica applications after setting your CLASSPATH to 
    include the directory where they reside:

    set CLASSPATH=%CLASSPATH%;c:\mica\apps

    or, on Unix:

    set CLASSPATH=${CLASSPATH};/home/mica/apps

    You can run the Mica examples after setting your CLASSPATH to 
    include the directory where they reside:

    set CLASSPATH=%CLASSPATH%;c:\mica\examples

    or, on Unix:

    set CLASSPATH=${CLASSPATH};/home/mica/examples

Installation of the Property Files

    Mica looks for property files in the current directory and in your
    home directory. The property file: "defaults.mica" is normally only
    used for internationalization. The property file: "properties.mica"
    contains at least two important properties that Mica needs to find
    it's icons and other files: Mi_HOME and Mi_IMAGES_HOME. These will
    need to be modified and copied to either the directory where Mica
    applications will be started from (for example: in the Mica 'bin'
    subdirectory) or your home directory.

    The default values of these properties are:

    Mi_HOME         = /home/mica
    Mi_IMAGES_HOME  = ${Mi_HOME}/images

Bugs

    Please report any bugs you find to support@swfm.com, include the
    platform and reproduction information if possible.


