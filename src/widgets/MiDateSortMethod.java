
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
import com.swfm.mica.util.Strings;
import java.text.DateFormat;
import java.util.Date;

/**
 * table.getSortManager().setColumnSortMethod(1, new MiDateSortMethod());
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiDateSortMethod extends MiNumericalSortMethod implements MiiTableSortMethod
	{
	public				MiDateSortMethod()
		{
		}

	public		boolean		sort(Strings values, int[] sortedPositions, int sortMethod)
		{
		if (sortMethod == MiiTypes.Mi_NONE)
			{
			return(false);
			}

		Strings millisSince1970 = new Strings();
//		DateFormat dateFormat = DateFormat.getDateTimeInstance();
//		dateFormat.setLenient(true);
		for (int i = 0; i < values.size(); ++i)
			{
			try	{
/*
				if (MiSystem.getJDKVersion() >= 1.4)
					{
					millisSince1970.addElement("" + dateFormat.parse(values.get(i)).getTime());
					}
				else
*/
					{
					millisSince1970.addElement("" + Date.parse(values.get(i)));
					}
				}
			catch (Exception e)
				{
				MiDebug.println("Unable to parse date: \"" + values.get(i) + "\"");
				MiDebug.printStackTrace(e);
				}
			}

		return(super.sort(millisSince1970, sortedPositions, sortMethod));
		}
	}


