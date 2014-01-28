
javac -classpath %CLASSPATH%;%ANT_HOME%/lib/ant.jar;%MICA_HOME%/classes -d %MICA_HOME%/classes %MICA_HOME%/src/util/Ant*.java
javac -classpath %CLASSPATH%;%MICA_HOME%/classes -d %MICA_HOME%/classes %MICA_HOME%/src/util/Strings.java
jar -cvf %MICA_HOME%/build/lib/preprocessor.jar -C %MICA_HOME%/classes com/swfm/mica/util/AntPreprocessor.class -C %MICA_HOME%/classes com/swfm/mica/util/AntiPreprocessor.class -C %MICA_HOME%/classes com/swfm/mica/util/AntCppPreprocessor.class -C %MICA_HOME%/classes com/swfm/mica/util/Strings.class


