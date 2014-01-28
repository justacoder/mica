
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
import com.swfm.mica.util.Utility;
import com.swfm.mica.util.StringIntPair;

// FIX: What about dangling contraints!!! This is observer of deletions??
// tabLayout.removeContraintsForObject(label); ??
/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiRelativeLocationConstraint extends MiLayoutConstraint
	{
	private		MiPart		subject;
	private		MiPart		master;
	private		int		type;
	private		MiBounds	tmpBounds	= new MiBounds();
	private		MiSize		tmpSize		= new MiSize();

/*
SAME_TOP_AS
FITS_INSIDE

*/
	public final static	int	LEFT_OF				= 0;
	public final static	int	RIGHT_OF			= 1;
	public final static	int	TOP_OF				= 2;
	public final static	int	BOTTOM_OF			= 3;
	public final static	int	INSIDE_LEFT_OF			= 4;
	public final static	int	INSIDE_RIGHT_OF			= 5;
	public final static	int	INSIDE_TOP_OF			= 6;
	public final static	int	INSIDE_BOTTOM_OF		= 7;
	public final static	int	CENTER_OF			= 8;
	public final static	int	INSIDE_OF			= 9;
	public final static	int	OUTSIDE_OF			= 10;
	public final static	int	SAME_ROW_AS			= 11;
	public final static	int	SAME_COLUMN_AS			= 12;
	public final static	int	SAME_WIDTH_AS			= 13;
	public final static	int	SAME_HEIGHT_AS			= 14;
	public final static	int	SAME_WIDTH_AS_PRESERVE_ASPECT	= 15;
	public final static	int	SAME_HEIGHT_AS_PRESERVE_ASPECT	= 16;
	public final static	int	SAME_SIZE_AS			= 17;
	public final static	int	SAME_SW_INSIDE_CORNER		= 18;
	public final static	int	SAME_SE_INSIDE_CORNER		= 19;
	public final static	int	SAME_NW_INSIDE_CORNER		= 20;
	public final static	int	SAME_NE_INSIDE_CORNER		= 21;
	public final static	int	SAME_SW_OUTSIDE_CORNER		= 22;
	public final static	int	SAME_SE_OUTSIDE_CORNER		= 23;
	public final static	int	SAME_NW_OUTSIDE_CORNER		= 24;
	public final static	int	SAME_NE_OUTSIDE_CORNER		= 25;
	public final static	int	INSIDE_LEFT_CENTER_OF		= 26;
	public final static	int	INSIDE_RIGHT_CENTER_OF		= 27;
	public final static	int	INSIDE_TOP_CENTER_OF		= 28;
	public final static	int	INSIDE_BOTTOM_CENTER_OF		= 29;
	//public final static	int	SAME_PREFERRED_WIDTH_AS		= 30;
	//public final static	int	SAME_PREFERRED_HEIGHT_AS	= 31;
	
	public final static	String	LEFT_OF_NAME		= "leftOf";
	public final static	String	RIGHT_OF_NAME		= "rightOf";
	public final static	String	TOP_OF_NAME		= "topOf";
	public final static	String	BOTTOM_OF_NAME		= "bottomOf";
	public final static	String	INSIDE_LEFT_OF_NAME	= "insideLeftOf";
	public final static	String	INSIDE_RIGHT_OF_NAME	= "insideRightOf";
	public final static	String	INSIDE_TOP_OF_NAME	= "insideTopOf";
	public final static	String	INSIDE_BOTTOM_OF_NAME	= "insideBottomOf";
	public final static	String	CENTER_OF_NAME		= "centerOf";
	public final static	String	INSIDE_OF_NAME		= "insideOf";
	public final static	String	OUTSIDE_OF_NAME		= "outsideOf";
	public final static	String	SAME_ROW_AS_NAME	= "sameRowAs";
	public final static	String	SAME_COLUMN_AS_NAME	= "sameColumnAs";
	public final static	String	SAME_WIDTH_AS_NAME	= "sameWidthAs";
	public final static	String	SAME_HEIGHT_AS_NAME	= "sameHeightAs";
	public final static	String	SAME_WIDTH_AS_PRESERVE_ASPECT_NAME= "sameWidthPreservingAspect";
	public final static	String	SAME_HEIGHT_AS_PRESERVE_ASPECT_NAME= "sameHeightPreservingAspect";
	public final static	String	SAME_SIZE_AS_NAME		= "sameSizeAs";
	public final static	String	SAME_SW_INSIDE_CORNER_NAME	= "sameSWInsideCorner";
	public final static	String	SAME_SE_INSIDE_CORNER_NAME	= "sameSEInsideCorner";
	public final static	String	SAME_NW_INSIDE_CORNER_NAME	= "sameNWInsideCorner";
	public final static	String	SAME_NE_INSIDE_CORNER_NAME	= "sameNEInsideCorner";
	public final static	String	SAME_SW_OUTSIDE_CORNER_NAME	= "sameSWOutsideCorner";
	public final static	String	SAME_SE_OUTSIDE_CORNER_NAME	= "sameSEOutsideCorner";
	public final static	String	SAME_NW_OUTSIDE_CORNER_NAME	= "sameNWOutsideCorner";
	public final static	String	SAME_NE_OUTSIDE_CORNER_NAME	= "sameNEOutsideCorner";
	public final static	String	INSIDE_LEFT_CENTER_OF_NAME	= "insideLeftCenterOf";
	public final static	String	INSIDE_RIGHT_CENTER_OF_NAME	= "insideRightCenterOf";
	public final static	String	INSIDE_TOP_CENTER_OF_NAME	= "insideTopCenterOf";
	public final static	String	INSIDE_BOTTOM_CENTER_OF_NAME	= "insideBottomCenterOf";
	//public final static	String	SAME_PREFERRED_WIDTH_AS_NAME	= "samePreferredWidth";
	//public final static	String	SAME_PREFERRED_HEIGHT_AS_NAME	= "samePreferredHeight";



	
	public				MiRelativeLocationConstraint(MiPart subject, String type, MiPart master, MiDistance margin)
		{
		this.subject = subject;
		this.master = master;
		this.type = lookUpType(type);
		this.margin = margin;
		}
	public				MiRelativeLocationConstraint(MiPart subject, String type, MiPart master)
		{
		this(subject, type, master, 0);
		}
	public				MiRelativeLocationConstraint(MiPart subject, int type, MiPart master)
		{
		this(subject, type, master, 0);
		}

	public				MiRelativeLocationConstraint(MiPart subject, int type, MiPart master, MiDistance margin)
		{
		this.subject = subject;
		this.master = master;
		this.type = type;
		this.margin = margin;
		}

	public 		int		reCalc()
		{
		MiBounds b;
		MiDistance loc;

		switch (type)
			{
			case LEFT_OF:
				loc = master.getBounds(tmpBounds).xmin - margin;
				if (subject.getXmax() > loc)
					{
					subject.setXmax(loc);
					return(1);
					}
				return(0);

			case RIGHT_OF:
				loc = master.getBounds(tmpBounds).xmax + margin;
				if (subject.getXmin() < loc)
					{
					subject.setXmin(loc);
					return(1);
					}
				return(0);

			case BOTTOM_OF:
				loc = master.getBounds(tmpBounds).ymin - margin;
				if (subject.getYmax() > loc)
					{
					subject.setYmax(loc);
					return(1);
					}
				return(0);

			case TOP_OF:
				loc = master.getBounds(tmpBounds).ymax + margin;
				if (subject.getYmin() < loc)
					{
					subject.setYmin(loc);
					return(1);
					}
				return(0);

			case INSIDE_LEFT_OF:
				loc = master.getInnerBounds(tmpBounds).xmin + margin;
				if (subject.getXmin() != loc)
					{
					subject.setXmin(loc);
					return(1);
					}
				return(0);

			case INSIDE_RIGHT_OF:
				loc = master.getInnerBounds(tmpBounds).xmax - margin;
				if (subject.getXmax() != loc)
					{
					subject.setXmax(loc);
					return(1);
					}
				return(0);

			case INSIDE_BOTTOM_OF:
				loc = master.getInnerBounds(tmpBounds).ymin + margin;
				if (subject.getYmin() != loc)
					{
					subject.setYmin(loc);
					return(1);
					}
				return(0);

			case INSIDE_TOP_OF:
				loc = master.getInnerBounds(tmpBounds).ymax - margin;
				if (subject.getYmax() != loc)
					{
					subject.setYmax(loc);
					return(1);
					}
				return(0);

			case SAME_COLUMN_AS:
				loc = master.getCenterX();
				if (subject.getCenterX() != loc)
					{
					subject.setCenterX(loc);
					return(1);
					}
				return(0);

			case SAME_ROW_AS:
				loc = master.getCenterY();
				if (subject.getCenterY() != loc)
					{
					subject.setCenterY(loc);
					return(1);
					}
				return(0);

			case CENTER_OF:
				MiPoint pt1 = master.getCenter();
				MiPoint pt2 = subject.getCenter();
				if ((pt2.x != pt1.x) || (pt2.y != pt1.y))
					{
					subject.setCenter(pt1);
					return(1);
					}
				return(0);

			case INSIDE_OF:
				break;

			case OUTSIDE_OF:
				break;
/****
			case SAME_PREFERRED_WIDTH_AS:
				loc = master.getPreferredSize(tmpSize).width - 2 * margin;
				if (subject.getPreferredSize(tmpSize).width != loc)
					{
					if (loc < 0)
						loc = 0;
					subject.setPreferredSize(null);
					subject.getPreferredSize(tmpSize);
					tmpSize.setWidth(loc);
					subject.setPreferredSize(tmpSize);
					return(1);
					}
				return(0);
			case SAME_PREFERRED_HEIGHT_AS:
				loc = master.getPreferredSize(tmpSize).height - 2 *margin;
				if (subject.getPreferredSize(tmpSize).height != loc)
					{
					if (loc < 0)
						loc = 0;
					subject.setPreferredSize(null);
					subject.getPreferredSize(tmpSize);
					tmpSize.setHeight(loc);
					subject.setPreferredSize(tmpSize);
					return(1);
					}
				return(0);
***/
			case SAME_WIDTH_AS:
				loc = master.getWidth() - 2 *margin;
				if (subject.getWidth() != loc)
					{
					if (loc < 0)
						master.setWidth(loc); //???
					subject.setWidth(loc);
					//subject.getPreferredSize(tmpSize).setWidth(loc);
					//subject.setPreferredSize(tmpSize);
					return(1);
					}
				return(0);
			case SAME_HEIGHT_AS:
				loc = master.getHeight() - 2 *margin;
				if (subject.getHeight() != loc)
					{
					if (loc < 0)
						master.setHeight(loc); //???
					subject.setHeight(loc);
					return(1);
					}
				return(0);
			case SAME_WIDTH_AS_PRESERVE_ASPECT:
				loc = master.getWidth() - 2 *margin;
				if (subject.getWidth() != loc)
					{
					double scaleFactor = loc/subject.getWidth();
					subject.scale(scaleFactor, scaleFactor);
					return(1);
					}
				return(0);
			case SAME_HEIGHT_AS_PRESERVE_ASPECT:
				loc = master.getHeight() - 2 *margin;
				if (subject.getHeight() != loc)
					{
					double scaleFactor = loc/subject.getHeight();
					subject.scale(scaleFactor, scaleFactor);
					return(1);
					}
				return(0);
			case SAME_SIZE_AS:
				b = master.getInnerBounds(tmpBounds);
				b.subtractMargins(margin);
				if (!subject.getBounds().equals(b))
					{
					subject.setBounds(b);
					return(1);
					}
				return(0);

			case SAME_SW_INSIDE_CORNER:
				b = master.getInnerBounds(tmpBounds);
				b.subtractMargins(margin);
				if ((!Utility.equals(subject.getXmin(), b.getXmin()))
				   || (!Utility.equals(subject.getYmin(), b.getYmin())))
					{
					subject.setXmin(b.getXmin());
					subject.setYmin(b.getYmin());
					return(1);
					}
				return(0);

			case SAME_SE_INSIDE_CORNER:
				b = master.getInnerBounds(tmpBounds);
				b.subtractMargins(margin);
				if ((!Utility.equals(subject.getXmax(), b.getXmax()))
				   || (!Utility.equals(subject.getYmin(), b.getYmin())))
					{
					subject.setXmax(b.getXmax());
					subject.setYmin(b.getYmin());
					return(1);
					}
				return(0);

			case SAME_NW_INSIDE_CORNER:
				b = master.getInnerBounds(tmpBounds);
				b.subtractMargins(margin);
				if ((!Utility.equals(subject.getXmin(), b.getXmin()))
				   || (!Utility.equals(subject.getYmax(), b.getYmax())))
					{
					subject.setXmin(b.getXmin());
					subject.setYmax(b.getYmax());
					return(1);
					}
				return(0);

			case SAME_NE_INSIDE_CORNER:
				b = master.getInnerBounds(tmpBounds);
				b.subtractMargins(margin);
				if ((!Utility.equals(subject.getXmax(), b.getXmax()))
				   || (!Utility.equals(subject.getYmax(), b.getYmax())))
					{
					subject.setXmax(b.getXmax());
					subject.setYmax(b.getYmax());
					return(1);
					}
				return(0);


			case SAME_SW_OUTSIDE_CORNER:
				b = master.getBounds(tmpBounds);
				b.subtractMargins(margin);
				if ((!Utility.equals(subject.getXmin(), b.getXmin()))
				   || (!Utility.equals(subject.getYmin(), b.getYmin())))
					{
					subject.setXmin(b.getXmin());
					subject.setYmin(b.getYmin());
					return(1);
					}
				return(0);

			case SAME_SE_OUTSIDE_CORNER:
				b = master.getBounds(tmpBounds);
				b.subtractMargins(margin);
				if ((!Utility.equals(subject.getXmax(), b.getXmax()))
				   || (!Utility.equals(subject.getYmin(), b.getYmin())))
					{
					subject.setXmax(b.getXmax());
					subject.setYmin(b.getYmin());
					return(1);
					}
				return(0);

			case SAME_NW_OUTSIDE_CORNER:
				b = master.getBounds(tmpBounds);
				b.subtractMargins(margin);
				if ((!Utility.equals(subject.getXmin(), b.getXmin()))
				   || (!Utility.equals(subject.getYmax(), b.getYmax())))
					{
					subject.setXmin(b.getXmin());
					subject.setYmax(b.getYmax());
					return(1);
					}
				return(0);

			case SAME_NE_OUTSIDE_CORNER:
				b = master.getBounds(tmpBounds);
				b.subtractMargins(margin);
				if ((!Utility.equals(subject.getXmax(), b.getXmax()))
				   || (!Utility.equals(subject.getYmax(), b.getYmax())))
					{
					subject.setXmax(b.getXmax());
					subject.setYmax(b.getYmax());
					return(1);
					}
				return(0);

			case INSIDE_LEFT_CENTER_OF:
				loc = master.getInnerBounds(tmpBounds).xmin + margin;
				if (subject.getXmin() != loc)
					{
					subject.setXmin(loc);
					return(1);
					}
				loc = tmpBounds.getCenterY();
				if (subject.getCenterY() != loc)
					{
					subject.setCenterY(loc);
					return(1);
					}
				return(0);

			case INSIDE_RIGHT_CENTER_OF:
				loc = master.getInnerBounds(tmpBounds).xmax - margin;
				if (subject.getXmax() != loc)
					{
					subject.setXmax(loc);
					return(1);
					}
				loc = tmpBounds.getCenterY();
				if (subject.getCenterY() != loc)
					{
					subject.setCenterY(loc);
					return(1);
					}
				return(0);

			case INSIDE_BOTTOM_CENTER_OF:
				loc = master.getInnerBounds(tmpBounds).ymin + margin;
				if (subject.getYmin() != loc)
					{
					subject.setYmin(loc);
					return(1);
					}
				loc = tmpBounds.getCenterX();
				if (subject.getCenterX() != loc)
					{
					subject.setCenterX(loc);
					return(1);
					}
				return(0);

			case INSIDE_TOP_CENTER_OF:
				loc = master.getInnerBounds(tmpBounds).ymax - margin;
				if (subject.getYmax() != loc)
					{
					subject.setYmax(loc);
					return(1);
					}
				loc = tmpBounds.getCenterX();
				if (subject.getCenterX() != loc)
					{
					subject.setCenterX(loc);
					return(1);
					}
				return(0);

			}
		return(0);
		}
	public		int		lookUpType(String type)
		{
		for (int i = 0; i < lookUpTable.length; ++i)
			{
			if (lookUpTable[i].string.equals(type))
				return(lookUpTable[i].value);
			}
		throw new IllegalArgumentException(type);
		}
	public		String		toString()
		{
		for (int i = 0; i < lookUpTable.length; ++i)
			{
			if (lookUpTable[i].value == type)
				{
				return(MiDebug.getMicaClassName(this) + "[(subject:" + subject 
					+ ")->" + lookUpTable[i].string + "(master:" + master +")]");
				}
			}
		return(getClass().getName() + "[Unknown constraint type]");
		}
	private final static	StringIntPair[]		lookUpTable =
		{
		new StringIntPair(LEFT_OF_NAME			, LEFT_OF),
		new StringIntPair(RIGHT_OF_NAME			, RIGHT_OF),
		new StringIntPair(TOP_OF_NAME			, TOP_OF),
		new StringIntPair(BOTTOM_OF_NAME		, BOTTOM_OF),
		new StringIntPair(INSIDE_LEFT_OF_NAME		, INSIDE_LEFT_OF),
		new StringIntPair(INSIDE_RIGHT_OF_NAME		, INSIDE_RIGHT_OF),
		new StringIntPair(INSIDE_TOP_OF_NAME		, INSIDE_TOP_OF),
		new StringIntPair(INSIDE_BOTTOM_OF_NAME		, INSIDE_BOTTOM_OF),
		new StringIntPair(CENTER_OF_NAME		, CENTER_OF),
		new StringIntPair(INSIDE_OF_NAME		, INSIDE_OF),
		new StringIntPair(OUTSIDE_OF_NAME		, OUTSIDE_OF),
		new StringIntPair(SAME_ROW_AS_NAME		, SAME_ROW_AS),
		new StringIntPair(SAME_COLUMN_AS_NAME		, SAME_COLUMN_AS),
		new StringIntPair(SAME_WIDTH_AS_NAME		, SAME_WIDTH_AS),
		new StringIntPair(SAME_HEIGHT_AS_NAME		, SAME_HEIGHT_AS),
		new StringIntPair(SAME_WIDTH_AS_PRESERVE_ASPECT_NAME, SAME_WIDTH_AS_PRESERVE_ASPECT),
		new StringIntPair(SAME_HEIGHT_AS_PRESERVE_ASPECT_NAME, SAME_HEIGHT_AS_PRESERVE_ASPECT),
		new StringIntPair(SAME_SIZE_AS_NAME		, SAME_SIZE_AS),
		new StringIntPair(SAME_SW_INSIDE_CORNER_NAME	, SAME_SW_INSIDE_CORNER),
		new StringIntPair(SAME_SE_INSIDE_CORNER_NAME	, SAME_SE_INSIDE_CORNER),
		new StringIntPair(SAME_NW_INSIDE_CORNER_NAME	, SAME_NW_INSIDE_CORNER),
		new StringIntPair(SAME_NE_INSIDE_CORNER_NAME	, SAME_NE_INSIDE_CORNER),
		new StringIntPair(SAME_SW_OUTSIDE_CORNER_NAME	, SAME_SW_OUTSIDE_CORNER),
		new StringIntPair(SAME_SE_OUTSIDE_CORNER_NAME	, SAME_SE_OUTSIDE_CORNER),
		new StringIntPair(SAME_NW_OUTSIDE_CORNER_NAME	, SAME_NW_OUTSIDE_CORNER),
		new StringIntPair(SAME_NE_OUTSIDE_CORNER_NAME	, SAME_NE_OUTSIDE_CORNER),
		new StringIntPair(INSIDE_LEFT_CENTER_OF_NAME	, INSIDE_LEFT_CENTER_OF),
		new StringIntPair(INSIDE_RIGHT_CENTER_OF_NAME	, INSIDE_RIGHT_CENTER_OF),
		new StringIntPair(INSIDE_TOP_CENTER_OF_NAME	, INSIDE_TOP_CENTER_OF),
		new StringIntPair(INSIDE_BOTTOM_CENTER_OF_NAME	, INSIDE_BOTTOM_CENTER_OF),
		//new StringIntPair(SAME_PREFERRED_WIDTH_AS_NAME	, SAME_PREFERRED_WIDTH_AS),
		//new StringIntPair(SAME_PREFERRED_HEIGHT_AS_NAME	, SAME_PREFERRED_HEIGHT_AS),
		};


	}


