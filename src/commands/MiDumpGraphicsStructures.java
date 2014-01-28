
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
import com.swfm.mica.util.Utility; 
import java.util.Enumeration; 
import java.util.Properties; 

/**----------------------------------------------------------------------------------------------
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiDumpGraphicsStructures extends MiCommandHandler
	{
	public static final String	DETAILED_INFO		= "Details";
	public static final String	DETAILED_INFO_BUT_NOT_CONTENTS		= "DetailsNotContents";
	public static final String	NORMAL_INFO		= "Normal";
	public static final String	STRUCTURE_INFO_ONLY	= "StructureOnly";
	public static final String	ATTRIBUTE_CACHE		= "AttributeCache";
	public static final String	PROPERTIES		= "Properties";

	private		boolean		printBounds		= true;
	private		boolean		printPreferredBounds	= true;
	private		boolean		printSpecialBounds	= true;
	private		boolean		printConnections	= true;
	private		boolean		printHelpInfo		= true;
	private		boolean		printAttachments	= true;
	private		boolean		printEventHandlers	= true;
	private		boolean		printActionHandlers	= true;
	private		boolean		printTransforms		= true;
	private		boolean		printState		= true;
	private		boolean		printLayoutDetails	= true;
	private		boolean		printEventHandlersEvents= true;
	private		boolean		printMajorAttributes	= true;
	private		boolean		printMinorAttributes	= true;
	private		boolean		printResources		= true;
	private		boolean		printValidity		= true;
	private		boolean		printObjectContents	= true;
	private		boolean		printManagedPointManagers= true;
	private		int		depth			= 0;
	private		String		indent			= new String();
	private		MiBounds	tmpBounds		= new MiBounds();
	private		MiVector	tmpVector		= new MiVector();
	private		MiScale		tmpScale		= new MiScale();





	public				MiDumpGraphicsStructures()
						{
						}

	public		void		processCommand(String arg)
		{
		MiPart obj = (MiPart )getTargetOfCommand();
		depth = 0;

		if (arg == null)
			arg = DETAILED_INFO;

		if ((arg.equals(DETAILED_INFO)) || (arg.equals(DETAILED_INFO_BUT_NOT_CONTENTS)))
			{
			printBounds 		= true;
			printPreferredBounds	= true;
			printSpecialBounds 	= true;
			printConnections 	= true;
			printHelpInfo 		= true;
			printAttachments 	= true;
			printEventHandlers 	= true;
			printActionHandlers	= true;
			printTransforms 	= true;
			printState 		= true;
			printLayoutDetails	= true;
			printMajorAttributes	= true;
			printMinorAttributes	= true;
			printResources		= true;
			printValidity		= true;
			printManagedPointManagers= true;
			if (arg.equals(DETAILED_INFO_BUT_NOT_CONTENTS))
				printObjectContents	= false;
			else
				printObjectContents	= true;
			}
		else if ((arg.equals(NORMAL_INFO)) || (arg.equals(STRUCTURE_INFO_ONLY)))
			{
			printBounds 		= true;
			printPreferredBounds	= true;
			printSpecialBounds 	= false;
			printConnections 	= false;
			printHelpInfo 		= false;
			printAttachments 	= false;
			printEventHandlers 	= false;
			printActionHandlers	= false;
			printTransforms 	= false;
			printState 		= true;
			printLayoutDetails	= false;
			printMajorAttributes	= false;
			printMinorAttributes	= false;
			printResources		= false;
			printEventHandlersEvents= false;
			printValidity		= false;
			printManagedPointManagers= false;
			printObjectContents	= true;
			if (arg.equals(STRUCTURE_INFO_ONLY))
				{
				printPreferredBounds = false;
				}
			}
		else if (arg.equals(ATTRIBUTE_CACHE))
			{
			printAttributeCache();
			return;
			}
		else if (arg.equals(PROPERTIES))
			{
			printProperties();
			return;
			}
		MiDebug.setMicaPrefixEnabled(false);
		MiDebug.println(" ******* Printing Mica Graphics Structures *******");
		calcIndent();
		MiDebug.println(obj);
		dumpObjectContents(obj);
		MiDebug.setMicaPrefixEnabled(true);
		}

	private		void		dumpObjectContents(MiPart object)
		{
		MiDebug.println(indent + "{");

		if (object instanceof MiEditor)
			{
			MiEditor editor = (MiEditor )object;
			MiDebug.println(indent + " + device = " + editor.getDeviceBounds());
			MiDebug.println(indent + " + world = " + editor.getWorldBounds());
			MiDebug.println(indent + " + universe = " + editor.getUniverseBounds());
			if (editor.hasLayers())
				{
				MiDebug.println(indent + " + HAS LAYERS: (" + editor.getNumberOfLayers() 
					+ "), Current = " + editor.getCurrentLayer());
				}
			MiDebug.println(indent
				+ "keepConnectionsBelowNodes = " + editor.getKeepConnectionsBelowNodes());
			}
		if (object instanceof MiLayer)
			{
			MiDebug.println(indent
				+ "keepConnectionsBelowNodes = " 
				+ ((MiLayer )object).getKeepConnectionsBelowNodes());
			}

		if (printBounds)
			{
			MiDebug.println(indent + "geom bounds:" + object.getBounds());
			MiDebug.println(indent + "draw bounds:" + object.getDrawBounds(tmpBounds));
			}
		if (printPreferredBounds)
			{
			MiSize size = new MiSize();
			object.getPreferredSize(size);
			MiDebug.println(indent + "Preferred Size: " + size 
				+ (object.hasOverriddenPreferredSize() ? "<OVERRIDE>" : ""));
			}
		if (printSpecialBounds)
			{
			MiSize size = new MiSize();
			object.getMinimumSize(size);
			MiDebug.println(indent + "Minimum Size: " + size
				+ (object.hasOverriddenMinimumSize() ? "<OVERRIDE>" : ""));
			MiDebug.println(indent + "Inner Bounds: " + object.getInnerBounds());
			if (object instanceof MiVisibleContainer)
				{
				MiVisibleContainer c = (MiVisibleContainer )object;
				MiDebug.println(indent + "Inset Margins: " + c.getInsetMargins());
				MiDebug.println(indent + "Margins: " + c.getMargins(new MiMargins()));
				}
			}
		if (printState)
			printState(object);
		if (printMajorAttributes)
			printMajorAttributes(object);
		if (printMinorAttributes)
			printMinorAttributes(object);
		if (printResources)
			printResources(object);
		if (printTransforms)
			printTransforms(object);
		if (printEventHandlers)
			printEventHandlers(object);
		if (printActionHandlers)
			printActionHandlers(object);
		if (object.getLayout() != null)
			{
			MiDebug.println(indent + "+ layout: " + object.getLayout());
			if (printLayoutDetails)
				{
				if (object.getLayout().determinesPreferredAndMinimumSizes())
					{
					MiSize size = new MiSize();
					object.getLayout().getPreferredSize(size);
					MiDebug.println(indent + "    + Preferred Size: " + size);
					object.getLayout().getMinimumSize(size);
					MiDebug.println(indent + "    + Min Size: " + size);
					}
				else
					{
					MiDebug.println(indent
						+ "      <Does not determine pref or min size of container>");
					}
				}
			}
		if (printValidity)
			{
			MiDebug.println(indent 
				+ " + isOpaqueRectangle: " + object.isOpaqueRectangle());
			MiDebug.println(indent 
				+ " + containsOpaqueRectangles: " + object.containsOpaqueRectangles());
			MiDebug.println(indent 
				+ " + hasValidLayout: " + object.hasValidLayout());
			MiDebug.println(indent 
				+ " + hasValidEventHandlerCache: " + object.hasValidCachedEventHandlerInfo());
			}

		if (printManagedPointManagers)
			{
			printManagedPointManagers(object);
			}
		if (printConnections)
			{
			printConnections(object);
			}

		if (printHelpInfo)
			{
			printHelp(object);
			}

		if ((printAttachments) && (object.getNumberOfAttachments() > 0))
			{
			printAttachments(object);
			}

		if (printObjectContents)
			{
			for (int i = 0; i < object.getNumberOfParts(); ++i)
				{
				MiPart obj = object.getPart(i);
				MiDebug.println(indent + obj);
	
				++depth;
				calcIndent();
				dumpObjectContents(obj);
				--depth;
				calcIndent();
				}
			}
		MiDebug.println(indent + "}");
		}
	private		void		printState(MiPart object)
		{
		if ((object.isVisible()) && (!object.isSelected()) && (object.isSensitive())
			&& (!object.isAcceptingKeyboardFocus()) 
			&& (!object.isAcceptingMouseFocus())
			&& (!object.isAcceptingEnterKeyFocus())
			&& (!object.hasFixedWidth())
			&& (!object.hasFixedHeight())
			&& (!object.hasEnterKeyFocus())
			&& (!object.hasKeyboardFocus())
			&& (!object.hasMouseFocus())
			&& (!object.isHidden())
			&& (!object.getHasBorderHilite())
			&& (object.isPrintable())
			&& (object.isSavable())
			&& (object.isCopyable())
			&& (object.isMovable())
			&& (object.isSnappable())
			&& (!object.isUngroupable())
			&& (object.isSelectable())
			&& (object.isDeletable())
			&& ((!(object instanceof MiText)) || (!((MiText )object).isEditable()))
			&& (object.isPickableWhenTransparent())
			&& ((!(object instanceof MiVisibleContainer)) 
				|| (!(((MiVisibleContainer )object).getDisplaysFocusBorder())))
			&& (object.getContextCursor(object.getBounds()) == MiiTypes.Mi_DEFAULT_CURSOR))
			{
			return;
			}
		MiDebug.print(indent + " + state: ");
		if (object.isAcceptingKeyboardFocus())
			MiDebug.print("ACCEPTS-KEYBOARD-FOCUS ");
		if (object.isAcceptingMouseFocus())
			MiDebug.print("ACCEPTS-MOUSE-FOCUS ");
		if (object.isAcceptingEnterKeyFocus())
			MiDebug.print("ACCEPTS-ENTERKEY-FOCUS ");
		if (!object.isPrintable())
			MiDebug.print("NOT-PRINTABLE ");
		if (!object.isSavable())
			MiDebug.print("NOT-SAVABLE ");
		if (!object.isVisible())
			MiDebug.print("INVISIBLE ");
		if ((object instanceof MiText) && (((MiText )object).isEditable()))
			MiDebug.print("EDITABLE ");
		if (object.isHidden())
			MiDebug.print("HIDDEN ");
		if (object.isSelected())
			MiDebug.print("SELECTED ");
		if (!object.isSensitive())
			MiDebug.print("DE-SENSITIZED ");
		if (object.hasKeyboardFocus())
			MiDebug.print("HAS-KEYBOARD-FOCUS ");
		if (object.hasMouseFocus())
			MiDebug.print("HAS-MOUSE-FOCUS ");
		if (object.hasEnterKeyFocus())
			MiDebug.print("HAS-ENTERKEY-FOCUS ");
		if (object.hasFixedWidth())
			MiDebug.print("HAS-FIXED-WIDTH ");
		if (object.hasFixedHeight())
			MiDebug.print("HAS-FIXED-HEIGHT ");
		if (!object.isCopyable())
			MiDebug.print("NOT-COPYABLE ");
		if (!object.isMovable())
			MiDebug.print("NOT-MOVABLE ");
		if (!object.isSnappable())
			MiDebug.print("NOT-SNAPPABLE ");
		if (object.isUngroupable())
			MiDebug.print("NOT-UN-GROUPABLE ");
		if (!object.isSelectable())
			MiDebug.print("NOT-SELECTABLE ");
		if (!object.isDeletable())
			MiDebug.print("NOT-DELETABLE ");
		if (!object.isPickableWhenTransparent())
			MiDebug.print("NOT-PICKABLE-WHEN_TRANSPARENT ");
		if (object.getHasBorderHilite())
			MiDebug.print("HAS-BORDER-HILITE(width=" + object.getBorderHiliteWidth() + ") ");
		if (!object.getEventHandlingEnabled())
			MiDebug.print("EVENT-HANDLING-DISABLED ");
		if (object.getEventHandlingDisabledByContainer())
			MiDebug.print("EVENT-HANDLING-DISABLED-BY-CONTAINER ");
		if (object.getContextCursor(object.getBounds()) != MiiTypes.Mi_DEFAULT_CURSOR)
			MiDebug.print("CONTEXT-CURSOR=" + object.getContextCursor(object.getBounds()) + " ");
		if (object instanceof MiVisibleContainer)
			{
			if (((MiVisibleContainer )object).getDisplaysFocusBorder())
				MiDebug.print("DISPLAYS-FOCUS-BORDER");
			else
				MiDebug.print("not-DISPLAYS-FOCUS-BORDER");
			}
		MiDebug.print("\n");
		}
	private		void		printMajorAttributes(MiPart object)
		{
		MiDebug.print(indent + " + color: " + MiColorManager.getColorName(object.getColor()));
		MiDebug.print(" + backGroundColor: " + MiColorManager.getColorName(object.getBackgroundColor()));
		MiDebug.print("\n");
		}
	private		void		printMinorAttributes(MiPart object)
		{
		if ((object.getBackgroundRenderer() != null)
			|| (object.getBackgroundImage() != null)
			|| (object.getBackgroundTile() != null)
			|| (object.getSelectionGraphics() != null)
			|| (object.getBorderLook() != MiiTypes.Mi_FLAT_BORDER_LOOK)
			|| (object.getHasShadow())
			|| (object.getLineWidth() != 0)
			|| (object.getLineStartStyle() != MiiTypes.Mi_NONE)
			|| (object.getLineEndStyle() != MiiTypes.Mi_NONE)
			|| (object.getRotation() != 0)
			|| (object.isDragAndDropSource())
			|| (object.isDragAndDropTarget()))
			{
			MiDebug.print(indent);
			}
		else
			{
			return;
			}
		if (object.getBackgroundRenderer() != null)
			MiDebug.println(indent + " + Background Renderer: " + object.getBackgroundRenderer());
		if (object.getBackgroundImage() != null)
			MiDebug.println(indent + " + Background Image: " + object.getBackgroundImage());
		if (object.getBackgroundTile() != null)
			MiDebug.println(indent + " + Background Tile: " + object.getBackgroundTile());
		if (object.getSelectionGraphics() != null)
			MiDebug.println(indent + " + Selection Graphics handler: " + object.getSelectionGraphics());
		if (object.getBorderLook() != MiiTypes.Mi_FLAT_BORDER_LOOK)
			MiDebug.println(indent + " + BorderLook: " + MiiNames.borderLookNames[object.getBorderLook()]);
		if (object.getHasShadow())
			{
			MiDebug.println(indent + " + Shadow: [color=" 
				+ MiColorManager.getColorName(object.getShadowColor())
				+ ", length=" + object.getShadowLength() 
				+ ", direction=" 
				+ MiiNames.locationNames[object.getShadowDirection() 
					- MiiTypes.Mi_MIN_BUILTIN_LOCATION]
				+ "]");
			}
		if (object.getLineStartStyle() != MiiTypes.Mi_NONE)
			{
			MiDebug.println(indent + " + Line start : " + object.getLineStartStyle() 
				+ " (size = " + object.getLineStartSize() + ")");
			}
		if (object.getLineEndStyle() != MiiTypes.Mi_NONE)
			{
			MiDebug.println(indent + " + Line end : " + object.getLineEndStyle() 
				+ " (size = " + object.getLineEndSize() + ")");
			}
		if (object.getLineWidth() != 0)
			MiDebug.println(indent + " + Line width: " + object.getLineWidth());

		if (object.getRotation() != 0)
			MiDebug.println(indent + " + Rotation: " + (object.getRotation() * 180/Math.PI));

		if (object.isDragAndDropSource())
			MiDebug.println(indent + " + DragAndDrop Source");
		if (object.isDragAndDropTarget())
			MiDebug.println(indent + " + DragAndDrop Target");
		MiDebug.print("\n");
		}
	private		void		printResources(MiPart object)
		{
		int num = object.getNumberOfResources();
		if (num > 0)
			MiDebug.print(indent + "Resources:\n");
		for (int i = 0; i < num; ++i)
			{
			String name = object.getResourceName(i);
			Object value = object.getResource(name);
			MiDebug.print(indent + " -> " + name + "=" + value + "\n");
			}
		}
	private		void		printTransforms(MiPart object)
		{
		MiiTransform transform = object.getTransform();
		if (transform != null)
			{
			MiDebug.print(indent + " Transform: " + transform);
			if (transform.getRotation() != 0)
				{
				MiDebug.print("Angle:" + transform.getRotation());
				}
			MiDebug.print("\n");
			}
		}
	private		void		printEventHandlers(MiPart object)
		{
		for (int i = 0; i < object.getNumberOfEventHandlers(); ++i)
			{
			MiiEventHandler h = object.getEventHandler(i);
			String type = MiEventHandler.getNameForType(h.getType());
			MiDebug.println(indent + "event handler: <" + type + "> " + h);
			printEventsHandled(h, indent);
			}
		if (object instanceof MiWindow)
			{
			MiWindow editor = (MiWindow )object;
			for (int i = 0; i < editor.getNumberOfGlobalEventHandlers(); ++i)
				{
				MiiEventHandler h = editor.getGlobalEventHandler(i);
				String type = MiEventHandler.getNameForType(h.getType());
				MiDebug.println(indent + "hotkey handler: <" + type + "> " + h);
				printEventsHandled(h, indent);
				}
			for (int i = 0; i < editor.getNumberOfGrabEventHandlers(); ++i)
				{
				MiiEventHandler h = editor.getGrabEventHandler(i);
				String type = MiEventHandler.getNameForType(h.getType());
				MiDebug.println(indent + "grab event handler: <" + type + "> " + h);
				printEventsHandled(h, indent);
				}
/*
			for (int i = 0; i < editor.getNumberOfAnimators(); ++i)
				{
				MiiEventHandler h = editor.getAnimator(i);
				String type = MiEventHandler.getNameForType(h.getType());
				MiDebug.println(indent + "animator: <" + type + "> " + h);
				}
*/
			}
		}
	private		void		printEventsHandled(MiiEventHandler handler, String indent)
		{
		if (!printEventHandlersEvents)
			return;

		MiEvent[] events = handler.getRequestedEvents();
		String[] cmds = handler.getRequestedCommands();
		for (int i = 0; i < cmds.length; ++i)
			{
			MiDebug.println(
				indent + "   [" + MiEvent.eventToString(events[i]) + "->" + cmds[i] + "]");
			}
		}
	private		void		printActionHandlers(MiPart object)
		{
		Strings strs = new Strings();
		for (int i = 0; i < object.getNumberOfActionHandlers(); ++i)
			{
			MiiAction h = object.getActionHandler(i);
			MiDebug.println(indent + "action handler: <" + h.getActionHandler() + ">");
			strs.removeAllElements();
			strs.appendLineFeedDelimitedLines(h.getValidActionsString());
			for (int j = 0; j < strs.size(); ++j)
				{
				MiDebug.println(indent + "   " + strs.elementAt(j));
				}
/*
			String[] actionsHandled = h.getActionsHandled();
			for (int j = 0; j < actionsHandled.length; ++j)
				{
				MiDebug.println(indent + "   " + actionsHandled[j]);
				}
*/
			}
		}
	private		void		printConnections(MiPart object)
		{
		for (int i = 0; i < object.getNumberOfConnections(); ++i)
			{
			MiDebug.println(indent + " + connection: " + object.getConnection(i));
			}
		}
	private		void		printHelp(MiPart part)
		{

		printTheHelp("Tool-Hint-Help", part.getToolHintHelp(null));
		printTheHelp("Status-Bar-Help", part.getStatusHelp(null));
		printTheHelp("Dialog-Box-Help", part.getDialogHelp(null));
		}
	private		void		printTheHelp(String helpName, MiiHelpInfo helpInfo)
		{
		if (helpInfo != null)
			{
			if (helpInfo == MiHelpInfo.ignoreThis)
				{
				MiDebug.println(indent + " + " + helpName + ": IGNORE THIS");
				}
			else if (helpInfo == MiHelpInfo.noneForThis)
				{
				MiDebug.println(indent + " + " + helpName + ": NONE FOR THIS");
				}
			else
				{
				MiDebug.println(indent + " + " + helpName + ": " + helpInfo.getMessage());
				}
			}
		}
	private		void		printManagedPointManagers(MiPart object)
		{
		if (object.getAnnotationPointManager() != null)
			{
			MiDebug.println(indent + " + Annotation Points: " 
				+ object.getAnnotationPointManager().toString());
			MiDebug.print(indent + " + Connection Points @: [");
			MiDebug.print(""
				+ object.getAnnotationPointManager().getManagedPointsDebugString(object));
				MiDebug.print("]\n");
			}
		if (object.getConnectionPointManager() != null)
			{
			MiDebug.println(indent + " + Connection Points: "
				 + object.getConnectionPointManager().toString());
			MiDebug.print(indent + " + Connection Points @: [");
			MiDebug.print(""
				+ object.getConnectionPointManager().getManagedPointsDebugString(object));
				MiDebug.print("]\n");
			}
		if (object.getControlPointManager() != null)
			{
			MiDebug.println(indent + " + Control Points: "
				 + object.getControlPointManager().toString());
			MiDebug.print(indent + " + Connection Points @: [");
			MiDebug.print(""
				+ object.getControlPointManager().getManagedPointsDebugString(object));
				MiDebug.print("]\n");
			}
		if (object.getSnapPointManager() != null)
			{
			MiDebug.println(indent + " + Snap Points: "
				+ object.getSnapPointManager().toString());
			MiDebug.print(indent + " + Snap Points @: [");
			MiDebug.print(""
				+ object.getSnapPointManager().getManagedPointsDebugString(object));
				MiDebug.print("]\n");
			}
		}

	private		void		printAttachments(MiPart object)
		{
		MiPart container = object.getAttachments();
		if (container != null)
			{
			MiDebug.println(indent + " + attachments: " + container);
			depth += 2;
			calcIndent();
			dumpObjectContents(container);
			depth -= 2;
			calcIndent();
			}
		}

	private		void		printAttributeCache()
		{
		MiDebug.println("Number of MiParts created (ignoring those deleted): " 
			+ MiPart.getTotalNumberOfPartsCreated() + "\n");
		MiDebug.println("Total number of cached attributes: " 
			+ MiPart.getDefaultAttributes().getCache().getNumberOfCachedAttributes());
		MiDebug.println("Formatting attribute objects for printing.... one moment please ...\n");
		MiDebug.println(MiPart.getDefaultAttributes().cacheToString());
		}

	private		void		printProperties()
		{
		boolean micaPrefixEnabled = MiDebug.getMicaPrefixEnabled();
		MiDebug.setMicaPrefixEnabled(false);

		MiDebug.println("# -----------------------------------------------");
		MiDebug.println("# Default properties for Mica");
		MiDebug.println("# -----------------------------------------------");

		Properties properties = MiSystem.getMicaDefaultProperties();
		Enumeration e = properties.keys();
		while (e.hasMoreElements())
			{
			String key = (String)e.nextElement();
			String value = properties.getProperty(key);
			value = Utility.replaceAll(value, "\n", "\\n");
			MiDebug.println(key + "\t= " + value);
			}

		MiDebug.println("# -----------------------------------------------");
		MiDebug.println("# Default properties for current Applications");
		MiDebug.println("# -----------------------------------------------");

		properties = MiSystem.getApplicationDefaultProperties();
		e = properties.keys();
		while (e.hasMoreElements())
			{
			String key = (String)e.nextElement();
			String value = properties.getProperty(key);
			value = Utility.replaceAll(value, "\n", "\\n");
			MiDebug.println(key + "\t= " + value);
			}

		MiDebug.println("# -----------------------------------------------");
		MiDebug.println("# User specified properties");
		MiDebug.println("# -----------------------------------------------");

		properties = MiSystem.getProperties();
		e = properties.keys();
		while (e.hasMoreElements())
			{
			String key = (String)e.nextElement();
			String value = properties.getProperty(key);
			value = Utility.replaceAll(value, "\n", "\\n");
			MiDebug.println(key + "\t= " + value);
			}

		MiDebug.setMicaPrefixEnabled(micaPrefixEnabled);
		}
	private 	void 		calcIndent()
		{
		int num = depth;
		indent = new String("   ");
		while (num > 0)
			{
			indent = indent.concat("   ");
			--num;
			}
		}
	}


