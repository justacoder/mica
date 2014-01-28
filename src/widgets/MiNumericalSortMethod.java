
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
import com.swfm.mica.util.IntVector; 
import com.swfm.mica.util.DoubleVector; 
import com.swfm.mica.util.Strings;
import com.swfm.mica.util.Utility;

/**
 * table.getSortManager().setColumnSortMethod(1, new MiNumericalSortMethod());
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiNumericalSortMethod implements MiiTableSortMethod
	{
	public				MiNumericalSortMethod()
		{
		}

	public		boolean		sort(Strings values, int[] sortedPositions, int sortMethod)
		{
		if (sortMethod == MiiTypes.Mi_NONE)
			return(false);

		DoubleVector doubleVector = new DoubleVector();
		for (int i = 0; i < values.size(); ++i)
			{
			doubleVector.addElement(Utility.toDouble(values.elementAt(i)));
			sortedPositions[i] = i;
			}

		boolean unsorted = true;
		boolean wasUnsorted = false;

		double[] array = doubleVector.toArray();
		while (unsorted)
			{
			unsorted = false;
			for (int i = 0; i < array.length - 1; ++i)
				{
				if (array[i] > array[i + 1])
					{
					double value = array[i];
					array[i] = array[i + 1];
					array[i + 1] = value;
					int index = sortedPositions[i];
					sortedPositions[i] = sortedPositions[i + 1];
					sortedPositions[i + 1] = index;
					unsorted = true;
					wasUnsorted = true;
					}
				}
			}
		if (!wasUnsorted)
			{
			if (sortMethod == Mi_FORWARD)
				return(false);
			}
		if (((!wasUnsorted) && (sortMethod == Mi_TOGGLE))
			|| ((wasUnsorted) && (sortMethod == Mi_BACKWARD)))
			{
			doubleVector.reverseOrder();
			IntVector intVector = new IntVector();
			intVector.append(sortedPositions);
			intVector.reverseOrder();
			System.arraycopy(intVector.toArray(), 0, sortedPositions, 0, sortedPositions.length);
			}
		return(true);
		}
	}


