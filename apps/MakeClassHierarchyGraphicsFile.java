
/*
 ***************************************************************************
 *                  Mica - the Java(tm) Graphics Framework                 *
 ***************************************************************************
 * NOTICE: Permission to use, copy, and modify this software and its       *
 * documentation is hereby granted provided that this notice appears in    *
 * all copies.                                                             *
 *                                                                         *
 * Permission to distribute un-modified copies of this software and its    *
 * documentation is hereby granted provided that no fee is charged and     *
 * that this notice appears in all copies.                                 *
 *                                                                         *
 * SOFTWARE FARM MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE          *
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING, BUT  *
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR  *
 * A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SOFTWARE FARM SHALL NOT BE   *
 * LIABLE TO ANY PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR       *
 * CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE, MODIFICATION OR           *
 * DISTRIBUTION OF THIS SOFTWARE OR ITS DERIVATIVES.                       *
 *                                                                         *
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, AND THE AUTHORS AND      *
 * DISTRIBUTORS HAVE NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT,        *
 * UPDATES, ENHANCEMENTS, OR MODIFICATIONS.                                *
 *                                                                         *
 ***************************************************************************
 *   Copyright (c) 1997-2004 Software Farm, Inc.  All Rights Reserved.     *
 ***************************************************************************
 */


import com.swfm.mica.*; 
import java.io.*;
import java.util.StringTokenizer;
import com.swfm.mica.util.FastVector;
import com.swfm.mica.util.IntVector;
import com.swfm.mica.util.LongVector;
import com.swfm.mica.util.Strings;
import com.swfm.mica.util.Utility;

/**----------------------------------------------------------------------------------------------
 * This class is a command-line utility that parses source code
 * files written in Java and generates a file suitable for viewing
 * using the Mica MiGraph graphics editor.
 * 
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.0(Beta)
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MakeClassHierarchyGraphicsFile
	{
	private static final String	DEFAULT_OUTPUT_FILENAME = "class_hierarchy." 
						+ MiGraphicsWindow.Mi_MICA_GRAPHICS_FILENAME_EXTENSION;

					/**------------------------------------------------------
	 				 * The entry point for applications. This creates a new
					 * MakeClassHierarchyGraphicsFile. 
					 * -output		the file to generate
					 * -extension		the extension of the source files
					 * -help		print help about this application
					 * <...>		the directories to look in
					 *------------------------------------------------------*/
	public static 	void 		main(String args[])
		{
		String outputFile = DEFAULT_OUTPUT_FILENAME;
		Strings directories = new Strings();
		String extension = "java";
		for (int i = 0; i < args.length; ++i)
			{
			if (args[i].equals("-output"))
				{
				if (i + 1 < args.length)
					{
					outputFile = args[i + 1];
					++i;
					}
				else
					printUsage();
				}
			else if (args[i].equals("-extension"))
				{
				if (i + 1 < args.length)
					{
					extension = args[i + 1];
					++i;
					}
				else
					printUsage();
				}
			else if ((args[i].equals("-help")) 
				|| (args[i].equals("help")) 
				|| (args[i].equals("?")))
				{
				printUsage();
				}
			else
				{
				directories.addElement(args[i]);
				}
			}

		if (directories.size() == 0)
			directories.addElement(".");
		new SourceCodeClassHierarchyFileMaker(directories, outputFile, extension);
		}
					/**------------------------------------------------------
	 				 * Print a message about this utiltiy and exit.
					 *------------------------------------------------------*/
	static		void		printUsage()
		{
		System.out.println("Usage: Java MakeClassHierarchyGraphicsFile [-output <filename>]"
			+ " [-extension <extension>] [-help] <directory names>");
		System.out.println("\nDefaults: output filename = " + DEFAULT_OUTPUT_FILENAME 
			+ ", extension = java, directory names = .");
		System.out.println("\nVersion 0.9  Copyright 1998 Software Farm, Inc. All rights reserved.");
		System.out.println("\nThis program parses the source code files in the directory(s)");
		System.out.println("and generates a graphics file suitable for viewing using MiGraph.");
		System.exit(0);
		}
	}

/**----------------------------------------------------------------------------------------------
 * A class of the MICA Graphics Appletcation Suite
 * <p>
 * This class parses source code files written in Java and 
 * generates a file suitable for viewing using the Mica MiGraph 
 * graphics editor.
 *
 *----------------------------------------------------------------------------------------------*/
class SourceCodeClassHierarchyFileMaker implements MiiNames
	{
					/**------------------------------------------------------
	 				 * Contructs a new SourceCodeClassHierarchyFileMaker.
					 * @param directories 	the directories to get the source
					 *			files from
					 * @param outputFile 	the file to generate
					 * @param extension 	the extension of the source code files
					 *------------------------------------------------------*/
	public				SourceCodeClassHierarchyFileMaker(
						Strings directories, String outputFile, String extension)
		{
		SourceCodeClassExtractor analyser = new SourceCodeClassExtractor();
		ClassDefinition[] classDefs = analyser.generateClassDefinitionsFromFilesInDirectory
								(directories, extension);

		PrintWriter file = Utility.openOutputFile(outputFile);
		if (file == null)
			return;

		file.print("<!Mica-FormattedFile ViewManager = com.swfm.mica.MiGraphicsTreeViewManager>\n");
		for (int i = 0; i < classDefs.length; ++i)
			{
			ClassDefinition classDef = classDefs[i];

			file.print("name = " + classDef.name);

			file.print(", file = " + classDef.filename);

			if (classDef.packageName != null)
				file.print(", packageName = " + classDef.packageName);

			file.print(", scope = ");
			if (classDef.isPublic)
				file.print("public");
			else if (classDef.isProtected)
				file.print("protected");
			else if (classDef.isPrivate)
				file.print("private");
			else if (classDef.isPackagePrivate)
				file.print("packagePrivate");

			file.print(", kind = ");
			if (classDef.isAbstract)
				file.print("abstract, backgroundColor = darkWhite");
			else if (classDef.isInterface)
				file.print("interface, backgroundColor = white");
			else
				file.print("class");

			file.print(", lineNumber = " + classDef.lineNumber);

			file.print(", numberOfCharacters = " + classDef.size);

			if ((classDef.parentClasses.size() > 0) || (classDef.parentInterfaces.size() > 0))
				{
				file.print(", " + Mi_CONNECTED_FROM_NAME + " = \"");
				for (int j = 0; j < classDef.parentClasses.size(); ++j)
					{
					if (j != 0)
						file.print(", ");
					file.print((String )classDef.parentClasses.elementAt(j));
					}
				for (int j = 0; j < classDef.parentInterfaces.size(); ++j)
					{
					if ((j != 0) || (classDef.parentClasses.size() > 0))
						file.print(", ");
					file.print((String )classDef.parentInterfaces.elementAt(j));
					}
				file.print("\"");
				}
			file.print("\n");
			}
		}
	}

/**----------------------------------------------------------------------------------------------
 * A class of the MICA Graphics Appletcation Suite
 * <p>
 * This class extract information about each class found in source
 * code files in the given directories. 
 * 
 *----------------------------------------------------------------------------------------------*/
class SourceCodeClassExtractor
	{
	private		String 		acceptableFilenameExtension;
	private		FastVector 	classDefs = new FastVector();
	
					/**------------------------------------------------------
	 				 * Constructs a new SourceCodeClassExtractor.
					 *------------------------------------------------------*/
	public				SourceCodeClassExtractor()
		{
		}
					/**------------------------------------------------------
	 				 * Creates an array of instances of ClassDefinition, each
					 * of which has information about a class.
					 * @param directoryNames 	the directories to get the 
					 *				source code files
					 * @param fileNamesExtension	the extension of the source 
					 *				code filenames
					 *------------------------------------------------------*/
	public 		ClassDefinition[] generateClassDefinitionsFromFilesInDirectory(
							Strings directoryNames, String fileNamesExtension)
		{
		for (int i = 0; i < directoryNames.size(); ++i)
			{
			String directoryName = directoryNames.elementAt(i);
			if (!directoryName.endsWith(File.separator))
				directoryName += File.separator;
			Strings	filenames = Utility.getFileNames(directoryName, "*." + fileNamesExtension);

			for (int j = 0; j < filenames.size(); ++j)
				{
				FastVector defs = parseFileForClassDefinitions(
							directoryName + filenames.elementAt(j));

				System.out.println("Found " + defs.size() 
					+ (defs.size() == 1 ? " class" : " classes")
					+ " in " + filenames.elementAt(j));

				for (int k = 0; k < defs.size(); ++k)
					classDefs.addElement(defs.elementAt(k));
				}
			}

		ClassDefinition[] classDefArray = new ClassDefinition[classDefs.size()];
		classDefs.copyInto(classDefArray);
		return(classDefArray);
		}
					/**------------------------------------------------------
	 				 * Parses a single file and returns 0 or more instances
					 * of ClassDefinition.
					 * @param filename	the source code file to parse
					 *------------------------------------------------------*/
	private		FastVector	parseFileForClassDefinitions(String filename)
		{
		IntVector lineNumbers			= new IntVector(); 
		LongVector numberOfCharsBetweenLines 	= new LongVector();
		Strings linesOutsideAllClasses 		= getLinesOutsideAllClasses(
								filename,
								lineNumbers, 
								numberOfCharsBetweenLines);
		FastVector classes = new FastVector();

		int index;
		String packageName = null;
		ClassDefinition classDef = null;
		for (int i = 0; i < linesOutsideAllClasses.size(); ++i)
			{
			boolean inClassDef = false;
			String line = linesOutsideAllClasses.elementAt(i);
			if ((index = line.indexOf("package")) >= 0)
				{
				StringTokenizer t = new StringTokenizer(
							line.substring(index + "package".length()), " \t;");
				if (t.hasMoreTokens())
					{
					String str = t.nextToken().trim();
					packageName = str;
					}
				}
			if ((index = line.indexOf("class")) >= 0)
				{
				inClassDef = true;
				classDef = new ClassDefinition();

				if (line.indexOf("public") >= 0)
					classDef.isPublic = true;
				if (line.indexOf("private") >= 0)
					classDef.isPrivate = true;
				if (line.indexOf("protected") >= 0)
					classDef.isProtected = true;
				if (line.indexOf("abstract") >= 0)
					classDef.isAbstract = true;
				if (classDef.isPublic || classDef.isPrivate || classDef.isProtected)
					{
					classDef.isPackagePrivate = false;
					}

				classDef.packageName = packageName;
				classDef.filename = filename;
				classDef.lineNumber = lineNumbers.elementAt(i) + 1;
				classDef.size = numberOfCharsBetweenLines.elementAt(i + 1);
				StringTokenizer t = new StringTokenizer(
					line.substring(index + "class".length()), " \t,{}");
				if (t.hasMoreTokens())
					classDef.name = t.nextToken();
				}
			else if ((index = line.indexOf("interface")) >= 0)
				{
				inClassDef = true;
				classDef = new ClassDefinition();
				classDef.isInterface = true;
				if (line.indexOf("public") >= 0)
					{
					classDef.isPublic = true;
					classDef.isPackagePrivate = false;
					}
				classDef.packageName = packageName;
				classDef.filename = filename;
				classDef.lineNumber = lineNumbers.elementAt(i) + 1;
				classDef.size = numberOfCharsBetweenLines.elementAt(i + 1);
				StringTokenizer t = new StringTokenizer(
					line.substring(index + "interface".length()), " \t,{}");
				if (t.hasMoreTokens())
					classDef.name = t.nextToken();
				}
			if (inClassDef)
				{
				if ((index = line.indexOf("extends")) >= 0)
					{
					StringTokenizer t = new StringTokenizer(
						line.substring(index + "extends".length()), " \t,{}");
					if (t.hasMoreTokens())
						{
						String str = t.nextToken().trim();
						if (str.equals("implements"))
							break;
						classDef.parentClasses.addElement(str);
						}
					}
				if ((index = line.indexOf("implements")) >= 0)
					{
					StringTokenizer t = new StringTokenizer(
						line.substring(index + "implements".length()), " \t,{}");
					while (t.hasMoreTokens())
						{
						String str = t.nextToken().trim();
						if (str.equals("extends"))
							break;
						classDef.parentInterfaces.addElement(str);
						}
					}
				classes.addElement(classDef);
				inClassDef = false;
				}
			
			}
		return(classes);
		}
					/**------------------------------------------------------
	 				 * Parses a single file and returns all lines of text that
					 * are outside of any class definition.
					 * @param filename	the source code file to parse
					 * @param lineNumber	the (returned) line number of each
					 *			of the returned text strings
					 * @param numberOfCharsBetweenLines	
					 *			the (returned) number of characters
					 *			between each of the returned text 
					 *			strings
					 *------------------------------------------------------*/
	private		Strings		getLinesOutsideAllClasses(
						String filename, 
						IntVector lineNumbers, 
						LongVector numberOfCharsBetweenLines)
		{
		FileInputStream openedFile = null;
		BufferedInputStream file;
		try
			{
			openedFile = new FileInputStream(filename);
			file = new BufferedInputStream(new DataInputStream(openedFile), 10000);
			}
		catch (IOException e)
			{
			System.out.println("Unable to open file: " + filename);
			if (openedFile != null)
			try	{
				openedFile.close();
				}
			catch (Exception e2)
				{
				}
			return(new Strings());
			}

		long 		fileSize		= 0;
		Strings 	linesOutsideAllClasses 	= new Strings();
		StringBuffer 	currentLine	 	= new StringBuffer();
		int		currentLinesNumber	= -1;
		int 		charNum 		= 0;
		int 		lineNum 		= 0;
		char 		nextCh 			= '\0';
		int 		depth	 		= 0;
		long 		numCharsBetweenLines	= 0;

		try	{
			// Get number of ints in the file
			fileSize = file.available();
			}
		catch (Exception e)
			{
			}
		try {
		    while (charNum <= fileSize)
			{
			char ch = nextCh;
		    	if (charNum < fileSize)
				nextCh = (char )file.read();
			else
				nextCh = '\0';
			++charNum;
			if ((ch == '/') && (nextCh == '/'))
				{
				while ((ch != '\n') && (ch != '\r'))
					{
					if (charNum < fileSize)
						{
						ch = (char )file.read();
						++charNum;
						}
					else
						{
						numberOfCharsBetweenLines.addElement(numCharsBetweenLines);
						openedFile.close();
						return(linesOutsideAllClasses);
						}
					}
				ch = (char )file.read();
				++charNum;
				nextCh = (char )file.read();
				++charNum;
				}
			else if ((ch == '/') && (nextCh == '*'))
				{
				while ((ch != '*') || (nextCh != '/'))
					{
					if (charNum < fileSize)
						{
						ch = nextCh;
						nextCh = (char )file.read();
						++charNum;
						if ((ch == '\n') || (ch == '\r'))
							++lineNum;
						}
					else
						{
						numberOfCharsBetweenLines.addElement(numCharsBetweenLines);
						openedFile.close();
						return(linesOutsideAllClasses);
						}
					}
				ch = (char )file.read();
				++charNum;
				nextCh = (char )file.read();
				++charNum;
				}
			else if (ch == '{')
				++depth;
			else if (ch == '}')
				--depth;
			if (ch == '\n')
				{
				++lineNum;
				}
			++numCharsBetweenLines;
			if (((depth == 0) && (ch == ';')) || ((depth == 1) && (ch == '{')))
				{
				linesOutsideAllClasses.addElement(currentLine.toString());
				lineNumbers.addElement(currentLinesNumber);
				numberOfCharsBetweenLines.addElement(numCharsBetweenLines);

				numCharsBetweenLines = 0;
				currentLine = new StringBuffer();
				currentLinesNumber = -1;
				}

			if ((depth == 0) 
				&& (ch != ';') 
				&& (ch != '\0') 
				&& (ch != '\n') 
				&& (ch != '\r') 
				&& (ch != '}'))
				{
				if (currentLinesNumber == -1)
					currentLinesNumber = lineNum;
				currentLine.append(ch);
				}
			}
		    }
		catch (Exception e)
			{
			e.printStackTrace();
			}
		numberOfCharsBetweenLines.addElement(numCharsBetweenLines);
		try	{
			openedFile.close();
			}
		catch (Exception e2) { }
		return(linesOutsideAllClasses);
		}
	}
/**----------------------------------------------------------------------------------------------
 * A class of the MICA Graphics Appletcation Suite
 * <p>
 * This class contains information about a single class.
 * 
 *----------------------------------------------------------------------------------------------*/
class ClassDefinition
	{
	public		String		name;
	public		String		filename;
	public		String		packageName;
	public		int		lineNumber;
	public		long		size;

	public		boolean		isPublic		= false;
	public		boolean		isProtected		= false;
	public		boolean		isAbstract		= false;
	public		boolean		isPrivate		= false;
	public		boolean		isPackagePrivate	= true;
	public		boolean		isInterface		= false;
	public		FastVector	parentClasses		= new FastVector();
	public		FastVector	parentInterfaces	= new FastVector();
	}



