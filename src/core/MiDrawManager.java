
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
import com.swfm.mica.util.CacheVector;

/*
	TO DO:

	Attachments of root window are sometimes writen over in error
	because child draw managers do not allow damaged areas to be
	returned to root window (they manage them themselves) and
	therefore the attachments do not get damaged. For example
	move a node in an editor under a tour tip. Another is color
	names in the pulldown color chooser in the drawing toolbar.
	So: only root window should have a draw manager... simpler
	and maybe then we woould not need all that extra code (or as
	much anyway) to handle graphics that overlaps child opaqueRectangles.
	Also make opaqueRectangles seperate from having a drawManager.

	If overlapping graphics, then cannot do bit blt scrolling

	Attachments of anything are not displayed if the anything has a child part that is an subwindow (i.e. editor).

	Need to rewrite this...
*/

/**----------------------------------------------------------------------------------------------
 * This class manages the drawing of all windows, from the root window
 * to the top window, for all exposed and damaged areas. Graphics 
 * overlapping multiple windows is supported (i.e. windows and graphics
 * can be freely inter-mixed [this feature causes the implmentation of
 * this class to be overly compicated]).
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiDrawManager
	{
	private		int		numberOfAttachmentAreasToExclude;
	private		int		numberOfGlobalAreasToExclude;
	private		MiBoundsList	invalidAreas			= new MiBoundsList();
	private static	MiBounds 	sTmpBounds 			= new MiBounds();
	private		MiBounds 	tmpBounds 			= new MiBounds();
	private		MiBounds 	tmpBounds2 			= new MiBounds();
	private		MiBounds 	tmpArea 			= new MiBounds();
	private		MiBounds 	totalRootInvalidArea		= new MiBounds();
	private		MiBounds 	exposedScollXBounds		= new MiBounds();
	private		MiBounds 	exposedScollYBounds		= new MiBounds();
	private		boolean	 	drawing;
	private		boolean	 	debugging;
	private		MiPart	 	target;
	private		boolean		optimizeInvalidAreasEnabled	= true;
	private		boolean		totallyInvalidArea;
	private		boolean		targetIsRootWindow;
	private		boolean		targetIsManuallyScrolling;
	private		MiParts		localPartsToExclude		= new MiParts();
	private		MiBoundsList	localAreasToExclude		= new MiBoundsList();
	private		MiBoundsList	attachmentAreasToExclude	= new MiBoundsList();
	private		MiBoundsList	tmpAreasToRender		= new MiBoundsList();
	private		MiBoundsList	areasToRender			= new MiBoundsList();
	private		MiBoundsList	areasToRenderStrayPartsOnTopOfSubwindows
									= new MiBoundsList();
	private		MiBoundsList	globalAreasToExclude		= new MiBoundsList();
	private		MiBoundsList	totalAreasToExcludeInDevice 	= new MiBoundsList();
	private		MiBoundsList	totalAreasToIncludeInDevice 	= new MiBoundsList();
	private		MiBoundsList	totalAreasToIncludeInWorld 	= new MiBoundsList();
	private		MiTransforms	transforms			= new MiTransforms();
	private		MiPoint		tmpPoint			= new MiPoint();
	private		MiVector	tmpVector			= new MiVector();
//	private		MiDeviceVector	tmpDVector			= new MiDeviceVector();
//	private		MiVector	scrollError			= new MiVector();
	private		MiVector	scroll				= new MiVector();
//	private		boolean		lastDrawWasAScroll;
	private		boolean		drawingPartsOverlappingSubWindows;
	private		boolean		searchingForSubWindowsOnly;



	public				MiDrawManager(MiPart target)
		{
		this.target = target;
		if ((target instanceof MiWindow) && (((MiWindow )target).isRootWindow()))
			targetIsRootWindow = true;
		}
	public		void		setDrawingPartsOverlappingSubWindows(boolean flag)
		{
		drawingPartsOverlappingSubWindows = flag;
		}
	public		boolean		getDrawingPartsOverlappingSubWindows()
		{
		return(drawingPartsOverlappingSubWindows);
		}
	public		void		invalidateTotalArea(MiBounds invalidArea)
		{
//System.out.println("invalidArea totally = " + invalidArea);
//System.out.println("target = " + target);
		if (!drawing)
			{
{
//MiDebug.println("invalidArea totally = " + invalidArea);
//MiDebug.println("target = " + target);
//MiDebug.printStackTrace();
}
			invalidAreas.removeAllElements();
			invalidAreas.addElement(invalidArea);
			totallyInvalidArea = true;
			setThisOrPartHasInvalidArea(target);
			}
		}
	public		void		invalidateArea(MiBounds invalidArea)
		{
//MiDebug.println("invalidArea = " + invalidArea);
//System.out.println("target = " + target);

		if ((!drawing) && (!invalidArea.isReversed()))
			{
{
//MiDebug.println("invalidateArea = " + invalidArea);
//MiDebug.println("target = " + target);
//MiDebug.printStackTrace();
}
			int size = invalidAreas.size();
			if ((size > 0) && (invalidAreas.elementAt(size - 1).equals(invalidArea)))
				{
				return;
				}
			if (totallyInvalidArea)
				{
				invalidAreas.removeAllElements();
				invalidAreas.addElement(target.getDrawBounds(tmpBounds));
				}
			else
				{
				if (MiDebug.debug 
					&& (MiDebug.isTracing(target, MiDebug.TRACE_AREA_INVALIDATION)))
					{
					MiDebug.println(target + ": Invalidate area: " + invalidArea);
					//MiDebug.printStackTrace();
					}
if ((!invalidArea.intersectsIncludingEdges(target.getDrawBounds(tmpBounds))) && (debugging))
{
MiDebug.println("*****************************************************************");
MiDebug.println("               Mica: INTERNAL WARNING");
MiDebug.println("  Invalid area extends outside target draw bounds");
MiDebug.println("*****************************************************************");
MiDebug.println("target = " + target);
MiDebug.println("target.drawBounds = " + target.getDrawBounds(new MiBounds()));
MiDebug.println("target.bounds = " + target.getBounds(new MiBounds()));
MiDebug.println("target.invalidArea = " + invalidArea);
MiDebug.println("Current target invalidAreas = " + invalidAreas);
MiDebug.printStackTrace();
//MiDebug.dump(target);
return;
}

//MiDebug.println("ADDED invalidArea = " + invalidArea);
				invalidAreas.addElement(invalidArea);
				}
			setThisOrPartHasInvalidArea(target);
			}
		}

	public		void		invalidateBackToFront(MiBounds invalidArea)
		{
		if (!invalidArea.isReversed())
			{
			invalidateArea(invalidArea);
			if (target.containsOpaqueRectangles())
				invalidateBackToFront(target, invalidArea);
			}
		}

	public		void		scrollTotalArea(MiDistance tx, MiDistance ty)
		{
		if (invalidAreas.size() == 0)
			{
			scroll.x -= tx;
			scroll.y -= ty;
			setThisOrPartHasInvalidArea(target);
			}
		else
			{
			invalidateTotalArea(target.getDrawBounds(tmpBounds));
			}
		}
	public		void		setOptimizeInvalidAreasEnabled(boolean flag)
		{
		optimizeInvalidAreasEnabled = flag;
		}
	public		boolean		getOptimizeInvalidAreasEnabled()
		{
		return(optimizeInvalidAreasEnabled);
		}
	protected	void		setThisOrPartHasInvalidArea(MiPart part)
		{
		for (int i = 0; i < part.getNumberOfContainers(); ++i)
			{
			// but if part was clipped this will not be cleared so cannot do this...???
			//if (part.getContainer(i).thisOrPartHasInvalidArea)
				//return;

			setThisOrPartHasInvalidArea(part.getContainer(i));
			}
		part.setThisOrPartHasInvalidArea(true);
		}
	public		boolean		isTotallyValidArea()
		{
		return(invalidAreas.size() == 0);
		}

	public		void		setTargetIsManuallyScrolling(boolean flag)
		{
		targetIsManuallyScrolling = flag;
		}
	public		boolean		getTargetIsManuallyScrolling()
		{
		return(targetIsManuallyScrolling);
		}

	protected	boolean		isDrawing()
		{
		return(drawing);
		}
	protected	void		draw(
						MiRenderer renderer, 
						MiBoundsList globalAreasToExcludeInDevice,
						MiBoundsList globalAreasToIncludeInDevice)
		{
//MiDebug.printStackTrace();
//MiDebug.println("\n\n ---->  Renderering TARGET: " + target);
//MiDebug.println("invalidAreas = " + invalidAreas);
		drawing = true;

		if ((totallyInvalidArea) || (!scroll.isZero()))
			{
			invalidAreas.removeAllElements();
			invalidAreas.addElement(target.getDrawBounds(tmpBounds));
			}

		// ----------------------------------------------------
		// Determine areas to exclude consisting of
		// overlapping windows, subwindows and subwindow parts.
		// FIX: Do this only when layout has changed either above
		// or below or at the target
		// ----------------------------------------------------
		numberOfAttachmentAreasToExclude = 0;
		numberOfGlobalAreasToExclude = globalAreasToExcludeInDevice.size();
		globalAreasToExclude.removeAllElements();
		globalAreasToExcludeInDevice.dtow(renderer.getTransform(), globalAreasToExclude);

		localAreasToExclude.removeAllElements();
		localPartsToExclude.removeAllElements();
		determineLocalAreasToExclude(target, transforms);

		// When overlapping area is not damaged but abuts a damaged area
		// (i.e. damage is underneath, make sure that the pixels on the
		// edge of the overlapping area remain undamaged).
		localAreasToExclude.expandToIncludeDeviceTargetArea(renderer.getTransform());

//System.out.println("Local Areas to exclude: " + localAreasToExclude);
//System.out.println("globalAreasToExclude to exclude: " + globalAreasToExclude);

		totalAreasToExcludeInDevice.removeAllElements();
		localAreasToExclude.wtod(renderer.getTransform(), totalAreasToExcludeInDevice);


		totalAreasToExcludeInDevice.append(globalAreasToExcludeInDevice);


		// ----------------------------------------------------
		// Determine damaged areas that need to be rendered
		// ----------------------------------------------------

//System.out.println("drawArea = " + target.getDrawBounds(new MiBounds()));
//System.out.println("invalidAreas = " + invalidAreas);

		invalidAreas.removeRedundancies();
		invalidAreas.clip(target.getDrawBounds(tmpBounds));
		//////// BUT BROWSING NEAR SEPARATORS IS UGLY invalidAreas.expandToIncludeDeviceTargetArea(renderer.getTransform());
//System.out.println("\n\nBEFORE invalidAreas = " + invalidAreas);
		if (optimizeInvalidAreasEnabled)
			invalidAreas.optimize();
//System.out.println("AFTER invalidAreas = " + invalidAreas);


		invalidAreas.getTotalBounds(totalRootInvalidArea);
//System.out.println("invalidAreas = " + invalidAreas);
//System.out.println("invalidAreas.size = " + invalidAreas.size());


		MiBounds b = target.getInnerBounds(tmpBounds);
		boolean justScroll = ((!scroll.isZero())
				&& (Math.abs(scroll.x) < b.getWidth())
				&& (Math.abs(scroll.y) < b.getHeight()));

		// If overlapping graphics, then cannot do bit blt.
		// TO DO: if (b.intersects(globalAreasToExcludeInWorld))
		//	justScroll = false;

//System.out.println("scroll = " + scroll);
//System.out.println("justScroll = " + justScroll);

		areasToRenderStrayPartsOnTopOfSubwindows.removeAllElements();
		areasToRender.removeAllElements();
		if ((justScroll) || (targetIsManuallyScrolling))
			{
			invalidAreas.exclude(globalAreasToExclude, areasToRender);
			}
		else
			{
			localAreasToExclude.append(globalAreasToExclude);
			invalidAreas.exclude(localAreasToExclude, areasToRender);
			}


		if (globalAreasToIncludeInDevice.size() > 0)
			{
			totalAreasToIncludeInWorld.removeAllElements();
			globalAreasToIncludeInDevice.dtow(
				renderer.getTransform(), totalAreasToIncludeInWorld);
			for (int i = 0; i < totalAreasToIncludeInWorld.size(); ++i)
				{
				areasToRender.clip(totalAreasToIncludeInWorld.elementAt(i));
				}
			}

//System.out.println("areasToRender = " + areasToRender);
		for (int i = 0; i < areasToRender.size(); ++i)
			{
			tmpArea.copy(areasToRender.elementAt(i));
//MiDebug.println("areas To render: " + tmpArea);
//MiDebug.println("target.drawBounds = " + target.getDrawBounds(new MiBounds()));
			if (!tmpArea.intersectionWith(target.getDrawBounds(tmpBounds)))
				continue;
			renderer.setClipBounds(tmpArea);

//System.out.println("renderer.setClipBounds = " + renderer.getClipBounds());
//System.out.println("renderer.boundsClipped(drawBounds) = " + renderer.boundsClipped(target.getDrawBounds(new MiBounds())));
//System.out.println("DRAW: " + tmpArea);

			if (justScroll)
				{
//System.out.println("\n\n ---->  Scrolling TARGET: " + target);
//System.out.println("Scroll : " + scroll);

				exposedScollXBounds.reverse();
				exposedScollYBounds.reverse();
				if (target.getTransform() != null)
					renderer.pushTransform(target.getTransform());

				b = target.getInnerBounds(tmpBounds);

				int oldWriteMode = renderer.getWriteMode();
				renderer.setWriteMode(MiiTypes.Mi_COPY_WRITEMODE);

				if (scroll.y != 0)
					{
					if (scroll.y > 0)
						{
						b.ymax -= scroll.y;
						renderer.moveImageArea(b, 0, scroll.y);
						b.ymax = b.ymin + scroll.y;
						}
					else
						{
						b.ymin -= scroll.y;
						renderer.moveImageArea(b, 0, scroll.y);
						b.ymin = b.ymax + scroll.y;
						}
					exposedScollYBounds.copy(b);
					}
				if (scroll.x != 0)
					{
					b = target.getInnerBounds(tmpBounds);
					if (scroll.x > 0)
						{
						b.xmax -= scroll.x;
						renderer.moveImageArea(b, scroll.x, 0);
						b.xmax = b.xmin + scroll.x;
						}
					else
						{
						b.xmin -= scroll.x;
						renderer.moveImageArea(b, scroll.x, 0);
						b.xmin = b.xmax + scroll.x;
						}
					exposedScollXBounds.copy(b);
					}

				renderer.setWriteMode(oldWriteMode);

				scroll.zeroOut();

				if (target.getTransform() != null)
					renderer.popTransform();
				renderer.getClipBounds(tmpArea);

				if (!exposedScollXBounds.isReversed())
					{
					if (target.getTransform() != null)
						{
						target.getTransform().wtod(
							exposedScollXBounds, exposedScollXBounds);
						}
					invalidateBackToFront(exposedScollXBounds);
					if (exposedScollXBounds.intersectionWith(tmpArea))
						{
						renderer.setClipBounds(exposedScollXBounds);
						target.draw(renderer);
						}
					}
				if (!exposedScollYBounds.isReversed())
					{
					if (target.getTransform() != null)
						{
						target.getTransform().wtod(
							exposedScollYBounds, exposedScollYBounds);
						}
					invalidateBackToFront(exposedScollYBounds);
					if (exposedScollYBounds.intersectionWith(tmpArea))
						{
						renderer.setClipBounds(exposedScollYBounds);
						target.draw(renderer);
						}
					}
				}
			else
				{
				target.draw(renderer);
				}
			}

		// Add targets bounds as another clipBounds 
		renderer.getTransform().wtod(target.getBounds(tmpBounds), tmpBounds);
		globalAreasToIncludeInDevice.addElement(tmpBounds);
		totalAreasToIncludeInDevice = globalAreasToIncludeInDevice;

//System.out.println("SOON invalidAreas = " + invalidAreas);
//System.out.println("SOON areasToRender = " + areasToRender);
		if (localPartsToExclude.size() > 0)
			{
			searchingForSubWindowsOnly = true;
			drawSubWindows(target, renderer);
			}
		else if (target.getNumberOfAttachments() > 0)
			{
			if (target.getTransform() != null)
				renderer.pushTransform(target.getTransform());
//System.out.println("MAIN CALL DRAW ATTACHMENTS invalidAreas = " + invalidAreas);
			drawAttachments(target, renderer);
			if (target.getTransform() != null)
				renderer.popTransform();
			}

		globalAreasToIncludeInDevice.removeElementAt(globalAreasToIncludeInDevice.size() - 1);

		// Only flush if this particular drawManager had invalid areas...
		if (!totalRootInvalidArea.isReversed())
			renderer.flush(totalRootInvalidArea);

		if (targetIsRootWindow)
			{
			renderer.clearClipBounds();
			clearInvalidAreasOfWholeTree(target);
			}
		drawing = false;
		targetIsManuallyScrolling = false;
//System.out.println("\n\n---->  END OF Renderering TARGET: " + target);
		}



	public		void		getAreasToRender(MiBoundsList list)
		{
		list.append(areasToRender);
		}





	protected	void		drawSubWindows(MiPart container, MiRenderer renderer)
		{
		if ((drawingPartsOverlappingSubWindows)
			&& (!searchingForSubWindowsOnly)
			&& (container.getDrawManager() == null) 
			&& (areasToRenderStrayPartsOnTopOfSubwindows.size() > 0))
			{
			tmpAreasToRender.removeAllElements();
			areasToRenderStrayPartsOnTopOfSubwindows.exclude(
				totalAreasToExcludeInDevice, tmpAreasToRender);
			for (int i = 0; i < totalAreasToIncludeInDevice.size(); ++i)
				{
				tmpAreasToRender.clip(totalAreasToIncludeInDevice.elementAt(i));
				}
			for (int i = 0;i < tmpAreasToRender.size(); ++i)
				{
				renderer.getTransform().dtow(tmpAreasToRender.elementAt(i), tmpBounds2);
				if (container.getDrawBounds(tmpBounds).intersectionWith(tmpBounds2))
					{
					renderer.setClipBounds(tmpBounds);
					container.draw(renderer);
					}
				}
			if (!container.containsOpaqueRectangles())
				{
				return;
				}
			searchingForSubWindowsOnly = true;
			}

		if (container.getTransform() != null)
			{
			renderer.pushTransform(container.getTransform());
			}
		for (int i = 0; i < container.getNumberOfParts(); ++i)
			{
			MiPart part = container.getPart(i);
			if ((part.isVisible()) && (!part.isHidden()))
				{
				if (part.getDrawManager() != null)
					{
	if (!localPartsToExclude.contains(part))
		{
		MiDebug.println("A part has been added while this draw manager is drawing the graph.");
		MiDebug.println("This usually occurs when a thread that does not have a lock on the");
		MiDebug.println("scene graph is modifying the scene while this class is drawing it.");
		MiDebug.println("See MiPart#getAccessLock");

		throw new RuntimeException(
			"A part has been added while this draw manager is drawing the graph.\n"
			+ "This usually occurs when a thread that does not have a lock on the\n"
			+ "scene graph is modifying the scene while this class is drawing it.\n"
			+ "See MiPart#getAccessLock");
		}
					while (localPartsToExclude.elementAt(0) != part)
						{
						if (drawingPartsOverlappingSubWindows)
							{
							areasToRenderStrayPartsOnTopOfSubwindows.addElement(
								totalAreasToExcludeInDevice.elementAt(0));
							}
						totalAreasToExcludeInDevice.removeElementAt(0);
						localPartsToExclude.removeElementAt(0);
						}
					if (part.getThisOrPartHasInvalidArea())
						{
						totalAreasToExcludeInDevice.removeElementAt(0);
						localPartsToExclude.removeElementAt(0);
						part.getDrawManager().draw(
							renderer, 
							totalAreasToExcludeInDevice, 
							totalAreasToIncludeInDevice);
						searchingForSubWindowsOnly = false;
						// Get areasToRender from subWindow just drawn
						if (drawingPartsOverlappingSubWindows)
							{
							tmpAreasToRender.removeAllElements();
							part.getDrawManager().getAreasToRender(tmpAreasToRender);
							tmpAreasToRender.wtod(renderer.getTransform());
							areasToRenderStrayPartsOnTopOfSubwindows.append(
								tmpAreasToRender);
							areasToRenderStrayPartsOnTopOfSubwindows.append(
								part.getDrawManager().
								areasToRenderStrayPartsOnTopOfSubwindows);
							// Render each non-subWindow part that follows into
							// each of these areas
							}
						}
					else 
						{
//System.out.println("Undamaged sub-window is being added to list of areas to draw subsequent non-sub-window parts into." + localPartsToExclude.elementAt(0)  + ", : " + totalAreasToExcludeInDevice.elementAt(0));
						if (drawingPartsOverlappingSubWindows)
							{
							areasToRenderStrayPartsOnTopOfSubwindows.addElement(
							totalAreasToExcludeInDevice.elementAt(0));
	
							}
						totalAreasToExcludeInDevice.removeElementAt(0);
						localPartsToExclude.removeElementAt(0);
						}
					} // part is a subWindow
				else 
					{
					if (((drawingPartsOverlappingSubWindows) 
							&& (!searchingForSubWindowsOnly))
						|| (part.containsOpaqueRectangles()))
						{
						drawSubWindows(part, renderer);
						}
					}
				}// part is visible
			}
		searchingForSubWindowsOnly = false;
//System.out.println("Overlay CALL DRAW ATTACHMENTS invalidAreas = " + invalidAreas);
		drawAttachments(container, renderer);

		if (container.getTransform() != null)
			{
			renderer.popTransform();
			}
		}

	protected	void		drawAttachments(MiPart container, MiRenderer renderer)
		{
		if ((container == target) && (target.getNumberOfAttachments() > 0))
			{
			if (container.getTransform() != null)
				renderer.popTransform();

			while (localPartsToExclude.size() 
				> numberOfAttachmentAreasToExclude + numberOfGlobalAreasToExclude)
				{
				totalAreasToExcludeInDevice.removeElementAt(0);
				localPartsToExclude.removeElementAt(0);
				}
			attachmentAreasToExclude.removeAllElements();
	
			totalAreasToExcludeInDevice.dtow(
				renderer.getTransform(), attachmentAreasToExclude);
			invalidAreas.exclude(attachmentAreasToExclude, areasToRender);
			MiBounds attachmentsBounds = target.getAttachments().getDrawBounds(tmpBounds);
//System.out.println("Target Attachments attachmentAreasToExclude = " + attachmentAreasToExclude);
//System.out.println("Target Attachments invalidAreas = " + invalidAreas);
//System.out.println("Target Attachments areasToRender = " + areasToRender);
			for (int i = 0; i < areasToRender.size(); ++i)
				{
				tmpArea.copy(areasToRender.elementAt(i));
				renderer.setClipBounds(tmpArea);
//System.out.println("MiDrawManager: Drawing attachment");
				target.drawAttachments(renderer);
				}
			if (container.getTransform() != null)
				renderer.pushTransform(container.getTransform());
			}
		for (int i = 0; i < container.getNumberOfAttachments(); ++i)
			{
			MiPart part = container.getAttachment(i);
			if (((part.getThisOrPartHasInvalidArea())
				|| (areasToRenderStrayPartsOnTopOfSubwindows.size() > 0))
				&& (part.isVisible()) && (!part.isHidden()))
				{
				if (part.getDrawManager() != null)
					{
	if ((localPartsToExclude.size() == 0) || (!localPartsToExclude.contains(part)))
		{
		MiDebug.println("An attachment has been added while this draw manager is drawing the graph.");
		MiDebug.println("This usually occurs when a thread that does not have a lock on the");
		MiDebug.println("scene graph is modifying the scene while this class is drawing it.");
		MiDebug.println("See MiPart#getAccessLock");

		throw new RuntimeException(
			"An attachment has been added while this draw manager is drawing the graph.\n"
			+ "This usually occurs when a thread that does not have a lock on the\n"
			+ "scene graph is modifying the scene while this class is drawing it.\n"
			+ "See MiPart#getAccessLock");
		}

					while (localPartsToExclude.elementAt(0) != part)
						{
						totalAreasToExcludeInDevice.removeElementAt(0);
						localPartsToExclude.removeElementAt(0);
						}
					totalAreasToExcludeInDevice.removeElementAt(0);
					localPartsToExclude.removeElementAt(0);
					part.getDrawManager().draw(renderer, 
						totalAreasToExcludeInDevice, totalAreasToIncludeInDevice);
					part.getDrawManager().getAreasToRender(areasToRenderStrayPartsOnTopOfSubwindows);
					}
				else 
					{
					if (localPartsToExclude.size() > 0)
						drawSubWindows(part, renderer);
					}
				}
			}
		}

	protected	void		clearInvalidAreasOfWholeTree(MiPart container)
		{
		for (int i = 0; i < container.getNumberOfParts(); ++i)
			{
			MiPart part = container.getPart(i);
			if (part.getThisOrPartHasInvalidArea())
				clearInvalidAreasOfWholeTree(part);
			}
		for (int i = 0; i < container.getNumberOfAttachments(); ++i)
			{
			MiPart part = container.getAttachment(i);
			if (part.getThisOrPartHasInvalidArea())
				clearInvalidAreasOfWholeTree(part);
			}
		if (container.getDrawManager() != null)
			container.getDrawManager().clearInvalidAreas();
		container.setThisOrPartHasInvalidArea(false);
		scroll.zeroOut();
		}
	protected	void		clearInvalidAreas()
		{
		invalidAreas.removeAllElements();
		totallyInvalidArea = false;
		}
	protected	void		determineLocalAreasToExclude(MiPart container, MiTransforms transform)
		{
		if (container.getTransform() != null)
			transform.pushTransform(container.getTransform());

		for (int i = 0; i < container.getNumberOfParts(); ++i)
			{
			MiPart part = container.getPart(i);
//System.out.println("looking at part for exlusion: " + part);
			if (part.isVisible() && (!part.isHidden()))
				{
				if (part.getDrawManager() != null)
					{
					part.getDrawBounds(tmpBounds);
					transforms.wtod(tmpBounds, tmpBounds);
					localAreasToExclude.addElement(tmpBounds);
//System.out.println("Exluding: " + part);
					localPartsToExclude.addElement(part);
					}
				else 
					{
					determineLocalAreasToExclude(part, transform);
					}
				}
			}

		if (container == target)
			numberOfAttachmentAreasToExclude = localPartsToExclude.size();

		for (int i = 0; i < container.getNumberOfAttachments(); ++i)
			{
			MiPart part = container.getAttachment(i);
			if (part.isVisible() && (!part.isHidden()))
				{
				if (part.getDrawManager() != null)
					{
//System.out.println("Adding attachment for exlusion: " + part);
					part.getDrawBounds(tmpBounds);
					transforms.wtod(tmpBounds, tmpBounds);
					localAreasToExclude.addElement(tmpBounds);
//System.out.println("Exluding attachment: " + part);
					localPartsToExclude.addElement(part);
					}
				else 
					{
					determineLocalAreasToExclude(part, transform);
					}
				}

			}
		if (container == target)
			{
			numberOfAttachmentAreasToExclude 
				= localPartsToExclude.size() - numberOfAttachmentAreasToExclude;
			}
		if (container.getTransform() != null)
			transform.popTransform();
		}
	public		MiBoundsList	getInvalidAreas(MiRenderer renderer)
		{
		MiBoundsList boundsList = new MiBoundsList();
		getInvalidAreas(renderer, boundsList);
		return(boundsList);
		}
	protected	void		getInvalidAreas(
						MiRenderer renderer, 
						MiBoundsList boundsList)
		{
		invalidAreas.dtow(renderer.getTransform(), boundsList);
		boundsList.removeRedundancies();
		if (target.containsOpaqueRectangles())
			getInvalidAreas(target, renderer, boundsList);
		}
	protected	void		getInvalidAreas(
						MiPart container,
						MiRenderer renderer, 
						MiBoundsList boundsList)
		{
		for (int i = 0; i < container.getNumberOfParts(); ++i)
			{
			MiPart part = container.getPart(i);
			if (part.getDrawManager() !=  null)
				{
				part.getDrawManager().getInvalidAreas(renderer, boundsList);
				}
			else if (part.containsOpaqueRectangles())
				{
				getInvalidAreas(part, renderer, boundsList);
				}
			}
		}
	public static	void		invalidateBackToFront(MiPart container, MiBounds invalidArea)
		{
		if (container.getTransform() != null)
			container.getTransform().dtow(invalidArea, invalidArea);

		for (int i = 0; i < container.getNumberOfParts(); ++i)
			{
			MiPart part = container.getPart(i);
			if ((part.getDrawBounds(sTmpBounds).intersects(invalidArea)) && (part.isVisible()))
				{
				if (part.getDrawManager() != null)
					{
					part.getDrawManager().invalidateBackToFront(invalidArea);
					}
				else 
					{
					if (part.containsOpaqueRectangles())
						invalidateBackToFront(part, invalidArea);
					}
				}
			}
		for (int i = 0; i < container.getNumberOfAttachments(); ++i)
			{
			MiPart part = container.getAttachment(i);
			if ((part.getDrawBounds(sTmpBounds).intersects(invalidArea))
				&& (part.isVisible()) && (!part.isHidden()))
				{
				if (part.getDrawManager() != null)
					{
					part.getDrawManager().invalidateBackToFront(invalidArea);
					}
				else 
					{
					if (part.containsOpaqueRectangles())
						invalidateBackToFront(part, invalidArea);
					}
				}
			}
		if (container.getTransform() != null)
			container.getTransform().wtod(invalidArea, invalidArea);
		}
	public		void		invalidateFrontToBack(MiPart part, MiBounds invalidArea)
		{
		for (int i = 0; i < part.getNumberOfContainers(); ++i)
			{
			MiPart container = part.getContainer(i);

			if (container.getDrawManager() != null)
				{
				if (container.getTransform() != null)
					container.getTransform().wtod(invalidArea, invalidArea);
				container.invalidateArea(invalidArea);
				if (container.getTransform() != null)
					container.getTransform().dtow(invalidArea, invalidArea);
				}
				
			int indexOfThis = container.getIndexOfPart(part);
			for (int j = indexOfThis - 1; j >= 0; --j)
				{
				MiPart containerPart = container.getPart(j);
				if ((containerPart.getDrawBounds(tmpBounds).intersects(invalidArea)) 
					&& (containerPart.isVisible()))
					{
					if (containerPart.getDrawManager() != null)
						containerPart.getDrawManager().invalidateArea(invalidArea);
					if (containerPart.containsOpaqueRectangles())
						invalidateBackToFront(containerPart, invalidArea);

					for (int k = 0; k < containerPart.getNumberOfAttachments(); ++k)
						{
						MiPart attachment = containerPart.getAttachment(k);
						if (attachment.getDrawManager() != null)
							{
							attachment.getDrawManager().invalidateArea(
								invalidArea);
							}
						if (attachment.containsOpaqueRectangles())
							invalidateBackToFront(attachment, invalidArea);
						}
					}
				}
			if (container.getTransform() != null)
				container.getTransform().wtod(invalidArea, invalidArea);
			invalidateFrontToBack(container, invalidArea);
			if (container.getTransform() != null)
				container.getTransform().dtow(invalidArea, invalidArea);
			}
		}
/* FUTURE 
	public		void		refreshListOfSubWindows()
		{
		subWindows.removeAllElements();
		searchForSubWindows(target, subWindows);
		}


	protected	void		searchForSubWindows(
						MiPart container, 
						MiParts parts, 
						MiiTransform transform, 
						MiTransformsList transforms)
		{
		for (int i = 0; i < container.getNumberOfParts(); ++i)
			{
			MiPart part = container.getPart(i);
			if (part.getDrawManager() != null)
				{
				transforms.addElement(transform.copy());
				parts.addElement(part);
				}
			else 
				{
				if (part.getTransform() != null)
					transform.pushTransform(part.getTransform());

				searchForSubWindows(part, parts, transform, transforms);

				if (part.getTransform() != null)
					transform.popTransform();
				}
			}
		for (int i = 0; i < container.getNumberOfAttachments(); ++i)
			{
			MiPart part = container.getAttachment(i);
			if (part.getDrawManager() != null)
				{
				transforms.addElement(transform.copy());
				parts.addElement(part);
				}
			else 
				{
				if (part.getTransform() != null)
					transform.pushTransform(part.getTransform());

				searchForSubWindows(part, parts, transform, transforms);

				if (part.getTransform() != null)
					transform.popTransform();
				}
			}
		}
		
*** FUTURE */

	}

