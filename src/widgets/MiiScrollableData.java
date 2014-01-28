
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

/**----------------------------------------------------------------------------------------------
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public interface MiiScrollableData
	{
			/**------------------------------------------------------
	 		 * Gets whether actions like scrollLineDown is handled
			 * in this interfaces implementation or whether these type
			 * of methods are merely to be notified of the scrolling
			 * calculated elsewhere and executed by calling
			 * scrollToNormalizedVerticalPosition.
	 		 * @return 	 	true if minor scrolling is implemented here
			 *------------------------------------------------------*/
	boolean		isHandlingScrollingDiscreteAmountsLocally();

			/**------------------------------------------------------
	 		 * Gets the normalized (between 0.0 and 1.0 inclusive)
			 * horizontal position of the data (0.0 is the left side
			 * and 1.0 is the right side).
	 		 * @return 	 	the horizontal position
			 *------------------------------------------------------*/
	double		getNormalizedHorizontalPosition();


			/**------------------------------------------------------
	 		 * Gets the normalized (between 0.0 and 1.0 inclusive)
			 * vertical position of the data (0.0 is the left side
			 * and 1.0 is the right side).
	 		 * @return 	 	the vertical position
			 *------------------------------------------------------*/
	double		getNormalizedVerticalPosition();


			/**------------------------------------------------------
	 		 * Gets the normalized (between 0.0 and 1.0 inclusive)
			 * horizontal size of the data (0.0 indicates none of the
			 * data is visible and 1.0 indicates all of the data's
			 * width is visible).
	 		 * @return 	 	the amount of data visible
			 *------------------------------------------------------*/
	double		getNormalizedHorizontalAmountVisible();


			/**------------------------------------------------------
	 		 * Gets the normalized (between 0.0 and 1.0 inclusive)
			 * vertical size of the data (0.0 indicates none of the
			 * data is visible and 1.0 indicates all of the data's
			 * height is visible).
	 		 * @return 	 	the amount of data visible
			 *------------------------------------------------------*/
	double		getNormalizedVerticalAmountVisible();


			/**------------------------------------------------------
	 		 * Scrolls one line up (conversely, move the data one
			 * line down). If already at the top of the data then
			 * this does nothing. It is up to the implementation of
			 * this interface to determine what a 'line' is.
			 *------------------------------------------------------*/
	void		scrollLineUp();


			/**------------------------------------------------------
	 		 * Scrolls one line down (conversely, move the data one
			 * line up). If already at the bottom of the data then
			 * this does nothing. It is up to the implementation of
			 * this interface to determine what a 'line' is.
			 *------------------------------------------------------*/
	void		scrollLineDown();


			/**------------------------------------------------------
	 		 * Scrolls one line left (conversely, move the data one
			 * line right). If already at the left of the data then
			 * this does nothing. It is up to the implementation of
			 * this interface to determine what a 'line' is.
			 *------------------------------------------------------*/
	void		scrollLineLeft();


			/**------------------------------------------------------
	 		 * Scrolls one line right (conversely, move the data one
			 * line left). If already at the right of the data then
			 * this does nothing. It is up to the implementation of
			 * this interface to determine what a 'line' is.
			 *------------------------------------------------------*/
	void		scrollLineRight();



			/**------------------------------------------------------
	 		 * Scrolls one chunk up (conversely, move the data one
			 * chunk down). If already at the top of the data then
			 * this does nothing. It is up to the implementation of
			 * this interface to determine what a 'chunk' is.
			 *------------------------------------------------------*/
	void		scrollChunkUp();


			/**------------------------------------------------------
	 		 * Scrolls one chunk down (conversely, move the data one
			 * chunk up). If already at the bottom of the data then
			 * this does nothing. It is up to the implementation of
			 * this interface to determine what a 'chunk' is.
			 *------------------------------------------------------*/
	void		scrollChunkDown();


			/**------------------------------------------------------
	 		 * Scrolls one chunk left (conversely, move the data one
			 * chunk right). If already at the left of the data then
			 * this does nothing. It is up to the implementation of
			 * this interface to determine what a 'chunk' is.
			 *------------------------------------------------------*/
	void		scrollChunkLeft();


			/**------------------------------------------------------
	 		 * Scrolls one chunk right (conversely, move the data one
			 * chunk left). If already at the right of the data then
			 * this does nothing. It is up to the implementation of
			 * this interface to determine what a 'chunk' is.
			 *------------------------------------------------------*/
	void		scrollChunkRight();



			/**------------------------------------------------------
	 		 * Scrolls one page up (conversely, move the data one
			 * page down). If already at the top of the data then
			 * this does nothing. It is up to the implementation of
			 * this interface to determine what a 'page' is.
			 *------------------------------------------------------*/
	void		scrollPageUp();


			/**------------------------------------------------------
	 		 * Scrolls one page down (conversely, move the data one
			 * page up). If already at the bottom of the data then
			 * this does nothing. It is up to the implementation of
			 * this interface to determine what a 'page' is.
			 *------------------------------------------------------*/
	void		scrollPageDown();


			/**------------------------------------------------------
	 		 * Scrolls one page left (conversely, move the data one
			 * page right). If already at the left of the data then
			 * this does nothing. It is up to the implementation of
			 * this interface to determine what a 'page' is.
			 *------------------------------------------------------*/
	void		scrollPageLeft();


			/**------------------------------------------------------
	 		 * Scrolls one page right (conversely, move the data one
			 * page left). If already at the right of the data then
			 * this does nothing. It is up to the implementation of
			 * this interface to determine what a 'page' is.
			 *------------------------------------------------------*/
	void		scrollPageRight();



			/**------------------------------------------------------
	 		 * Scrolls to the top of the data.
			 *------------------------------------------------------*/
	void		scrollToTop();


			/**------------------------------------------------------
	 		 * Scrolls to the bottom of the data.
			 *------------------------------------------------------*/
	void		scrollToBottom();


			/**------------------------------------------------------
	 		 * Scrolls to the left side of the data.
			 *------------------------------------------------------*/
	void		scrollToLeftSide();


			/**------------------------------------------------------
	 		 * Scrolls to the right side of the data.
			 *------------------------------------------------------*/
	void		scrollToRightSide();



			/**------------------------------------------------------
	 		 * Scrolls to the given normalized (between 0.0 and 1.0)
			 * horizontal position.
			 * @param normalizedPosition	the new horizontal position
			 *------------------------------------------------------*/
	void		scrollToNormalizedVerticalPosition(double normalizedPosition);



			/**------------------------------------------------------
	 		 * Scrolls to the given normalized (between 0.0 and 1.0)
			 * vertical position.
			 * @param normalizedPosition	the new vertical position
			 *------------------------------------------------------*/
	void		scrollToNormalizedHorizontalPosition(double normalizedPosition);
	}

