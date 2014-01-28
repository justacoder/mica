
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


package com.swfm.mica;
import java.awt.Color; 
import java.awt.Image; 
import java.awt.Toolkit; 
import java.util.StringTokenizer;
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;
import java.io.BufferedReader; 
import com.swfm.mica.util.Utility;

/* XPM */
/* SAMPLE
static char * image_name[] = {
"14 14 5 1",
"       c None",
".      c #8617CF3CEBAD",
"X      c red brick",
"o      c gray",
"O      c black",
"         . .  ",
"       . .X . ",
"        X .  X",
"      ooooo . ",
"     o    X X ",
"    OO   . .  ",
"  OOOO        ",
" OOOOOO       ",
"OOOO  OO      ",
"OOOOOo O      ",
"OOOOOo O      ",
"OOOOOO O      ",
" OOOOOOO      ",
"  OOOO        "};

*/

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */

public class MiXpmImage
	{
	protected 	 int[]		colorTable = new int[256];



	public				MiXpmImage()
		{
		}

	public 		boolean		isXpmImage(String filename)
		{
		BufferedReader inputStream = null;
		try	{
			inputStream = MiSystem.getIOManager().getInputTextResourceAsStream(filename);
			}
		catch (Exception e)
			{
			MiDebug.println("XPM image file not found: \"" + filename + "\"");
			return(false);
			}

		try	{
			String firstLine = inputStream.readLine();
			if (firstLine.equals("/* XPM"))
				{
				return(true);
				}
			}
		catch (Exception e)
			{
			e.printStackTrace();
			}
		MiDebug.println("No XPM image found in file: \"" + filename + "\"");
		return(false);
		}
	public 		Image		getImage(String filename)
		{
		BufferedReader inputStream = null;
		try	{
			inputStream = MiSystem.getIOManager().getInputTextResourceAsStream(filename);
			}
		catch (Exception e)
			{
			MiDebug.println("XPM image file not found: \"" + filename + "\"");
			return(null);
			}

		try	{
			String firstLine = inputStream.readLine();
			String line;
			int width	= 0;
			int height	= 0;
			int numColors	= 0;
			int byteDepth	= 1;

			for (int i = 0; i < 256; ++i)
				colorTable[i] = 0;

			if (firstLine.startsWith("/* XPM"))
				{
				line = getNextLineWithContent(inputStream);
				line = line.replace('"', ' ');
				line = line.replace(',', ' ');

				width = Utility.toInteger(Utility.getIthWord(line, 0));
				height = Utility.toInteger(Utility.getIthWord(line, 1));
				numColors = Utility.toInteger(Utility.getIthWord(line, 2));
				byteDepth = Utility.toInteger(Utility.getIthWord(line, 3));

				for (int i = 0; i < numColors; ++i)
					{
					String colorName = "";

					line = getNextLineWithContent(inputStream);
					int colorID = (int )line.charAt(1);
					StringTokenizer t = new StringTokenizer(line);
					t.nextToken();
					String token = t.nextToken();
					while (!token.equals("c"))
						token = t.nextToken();
					while (t.hasMoreTokens())
						{
						token = t.nextToken();
						if (token.equals("s"))
							break;
						colorName = colorName + token;
						}
					colorName = colorName.replace('"', ' ');
					colorName = colorName.replace(',', ' ');
					colorName = colorName.trim();
					Color c = getColorFromName(colorName);
					// If not transparent
					if (c != null)
						colorTable[colorID] = c.getRGB();
					else
						colorTable[colorID] = 0;
					}
				}
			// OLD STYLE
			else if (firstLine.startsWith("#define "))
				{
				byteDepth = Utility.toInteger(Utility.getIthWord(firstLine, 2));
				line = inputStream.readLine().trim();
				// Skip format...
				line = inputStream.readLine().trim();

				height = Utility.toInteger(Utility.getIthWord(line, 2));
				line = inputStream.readLine().trim();
				width = Utility.toInteger(Utility.getIthWord(line, 2));
				line = inputStream.readLine().trim();
				numColors = Utility.toInteger(Utility.getIthWord(line, 2));

				if (byteDepth == 1)
					{
					for (int i = 0; i < numColors; ++i)
						{
						line = getNextLineWithContent(inputStream);

						int colorID = (int )line.charAt(1);
						int index = line.lastIndexOf('"', line.length() - 3);
						String colorName 
							= line.substring(index + 1, line.length() - 2);
						Color c = getColorFromName(colorName);
						// If not transparent
						if (c != null)
							colorTable[colorID] = c.getRGB();
						else
							colorTable[colorID] = 0;
						}
					}
				}
		
//System.out.println("width = " +width);
//System.out.println("height = " +height);
//System.out.println("numColors = " +numColors);
//System.out.println("byteDepth = " +byteDepth);

			int[] pixels = new int[width * height];
			int index = 0;
			for (int row = 0; row < height; ++row)
				{
				line = getNextLineWithContent(inputStream);
				for (int col = 0; col < width; ++col)
					{
					pixels[index++] = colorTable[(int )line.charAt(col + 1)];
					}
				}

			Image image = Toolkit.getDefaultToolkit().createImage(
				new MemoryImageSource(width, height, pixels, 0, width));
			return(image);
			}
		catch (Exception e)
			{
			e.printStackTrace();
			}
		return(null);
		}
	protected 	String		getNextLineWithContent(BufferedReader inputStream) throws Exception
		{
		String line;
		do	{
			line = inputStream.readLine().trim();
			} while (line.charAt(0) != '"');
		return(line);
		}
	protected	Color		getColorFromName(String colorName)
		{
		if (colorName.equalsIgnoreCase("None"))
			{
			return(null);
			}
		else if (colorName.charAt(0) == '#')
			{
			int red;
			int green;
			int blue;
			if (colorName.length() >= 13)
				{
				red = Integer.parseInt(
					colorName.substring(1, 5), 16)/256;
				green = Integer.parseInt(
					colorName.substring(5, 9), 16)/256;
				blue = Integer.parseInt(
					colorName.substring(9, 13), 16)/256;
				}
			else if (colorName.length() >= 7)
				{
				red = Integer.parseInt(
					colorName.substring(1, 3), 16);
				green = Integer.parseInt(
					colorName.substring(3, 5), 16);
				blue = Integer.parseInt(
					colorName.substring(5, 7), 16);
				}
			else 
				{
				red = Integer.parseInt(
					colorName.substring(1, 2), 16) * 16;
				green = Integer.parseInt(
					colorName.substring(2, 3), 16) * 16;
				blue = Integer.parseInt(
					colorName.substring(3, 4), 16) * 16;
				}
			return(new Color(red, green, blue));
			}
		return(MiColorManager.getColor(colorName));
		}

	}


