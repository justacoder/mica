
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
import com.swfm.mica.util.Strings; 
import com.swfm.mica.util.Utility; 
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.*;


/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiHelpViewer extends MiWidget implements MiiActionHandler, MiiActionTypes, MiiCommandNames
	{
	public static final String	Mi_BOLD_HTML_NAME		= "<B>";
	public static final String	Mi_END_BOLD_HTML_NAME		= "</B>";
	public static final String	Mi_ITALIC_HTML_NAME		= "<I>";
	public static final String	Mi_END_ITALIC_HTML_NAME		= "</I>";
	public static final String	Mi_UNDERLINE_HTML_NAME		= "<U>";
	public static final String	Mi_END_UNDERLINE_HTML_NAME	= "</U>";
	public static final String	Mi_SIZE_HTML_NAME		= "<Size ";
	public static final String	Mi_END_SIZE_HTML_NAME		= "</Size>";
	public static final String	Mi_TAB_REPLACEMENT_NAME		= "        ";



	public static final String	Mi_HELP_VIEW_CHANGED_ACTION_NAME= "helpViewChanged";
	public static final int		Mi_HELP_VIEW_CHANGED_ACTION	= MiActionManager.registerAction(
									Mi_HELP_VIEW_CHANGED_ACTION_NAME);
	public static String		Mi_SECTION_HEADER_ONLY	= "SectionHeaderOnly";

	private		Strings 	contents;
	private		Strings 	viewedSections;
	private		IntVector	viewedLineNumbers;
	private		IntVector	viewedHighlightStarts;
 	private		IntVector	viewedHighlightLengths;
	private		int 		currentViewIndex;
 	private		MiText		currentHighlightedLine	= new MiText();
	private		int 		currentSectionIndex	= -1;
	private		int 		currentHighlightOffset	= -1;
	private		String 		streamName;
	private		Hashtable 	contentsTable;
	private		Hashtable 	graphicsContentTable;
	private		MiTreeList 	contentsTreeList;
	private		MiList 		helpTextViewer;
	private		MiScrolledBox	helpTextViewerScrolledBox;
	private		boolean		includeSectionNumbersInTableOfContents	= true;
	private		boolean		fastButSimpleDisplay	= false;
	private		boolean		includeSectionHeadersInContent	= true;
	private		String		includedSectionHeadersAttributes= "<B><U><Size 24>";



	public				MiHelpViewer()
		{
		this(Mi_HORIZONTAL);
		}
	public				MiHelpViewer(int orientation)
		{
		if (orientation == Mi_HORIZONTAL)
			{
			MiRowLayout layout = new MiRowLayout();
			layout.setElementVSizing(Mi_EXPAND_TO_FILL);
			layout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
			layout.setUniqueElementIndex(1);
			setLayout(layout);
			}
		else 
			{
			MiColumnLayout layout = new MiColumnLayout();
			layout.setElementHSizing(Mi_EXPAND_TO_FILL);
			layout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
			layout.setUniqueElementIndex(1);
			setLayout(layout);
			}
		contentsTreeList = new MiTreeList(30, false);
		contentsTreeList.setFontBold(true);
		contentsTreeList.appendActionHandler(this, Mi_ITEM_SELECTED_ACTION);
		MiScrolledBox scrolledBox = new MiScrolledBox(contentsTreeList);
		appendPart(scrolledBox);

		helpTextViewer = new MiList();
		helpTextViewer.setFont(new MiFont("TimesRoman", MiFont.PLAIN, 14));
		helpTextViewer.getSortManager().setEnabled(false);
		helpTextViewer.setPreferredSize(new MiSize(500, 400));
		helpTextViewer.getSelectionManager().setBrowsable(false);
		helpTextViewer.getSelectionManager().setMaximumNumberSelected(0);
		helpTextViewerScrolledBox = new MiScrolledBox(helpTextViewer);
		appendPart(helpTextViewerScrolledBox);
		}

	public		Strings		getContentsList()
		{
		return(contents);
		}
	public		MiTreeList	getContentsListViewer()
		{
		return(contentsTreeList);
		}
	public		void		setHelpTextViewer(MiList list)
		{
		helpTextViewer = list;
		helpTextViewerScrolledBox.setVisible(false);
		((MiRowColBaseLayout )getLayout()).setUniqueElementIndex(0);
		}
	public		MiList		getHelpTextViewer()
		{
		return(helpTextViewer);
		}
	public		int		getNumberOfSectionIndexes()
		{
		return(contents.size());
		}
	public		int		getCurrentSectionIndex()
		{
		return(currentSectionIndex);
		}
	public		void		setCurrentSectionIndex(int index)
		{
		setCurrentSection(contents.elementAt(index), -1, -1);
		}
	public		String		getHelpText(int index)
		{
		return((String )contentsTable.get(contents.elementAt(index)));
		}
	public		void		setHelpText(String content, int index)
		{
		graphicsContentTable.remove(contents.elementAt(index));
		contentsTable.put(contents.elementAt(index), content);
		if (currentSectionIndex == index)
			{
			currentSectionIndex = -1;
			setCurrentSectionIndex(index);
			}
		}
	public		String		getHelpText(String sectionName)
		{
		return((String )contentsTable.get(sectionName));
		}
	public		void		setHelpText(String content, String sectionName)
		{
		contentsTable.put(sectionName, content);
		}
	public		void		moveCurrentlyViewedSection(boolean forward)
		{
		if (forward)
			++currentViewIndex;
		else
			--currentViewIndex;

		String sectionName = viewedSections.elementAt(currentViewIndex);
		String helpText = (String )contentsTable.get(sectionName);
		if (helpText.equals(Mi_SECTION_HEADER_ONLY))
			return;

		currentHighlightedLine.setSelection(0, 0);

		if (currentSectionIndex != contents.indexOf(sectionName))
			displayContentsForSection(sectionName);

		currentSectionIndex = contents.indexOf(sectionName);
		if (!sectionName.equals(contentsTreeList.getValue()))
			contentsTreeList.setValue(sectionName);


		int lineNumber = viewedLineNumbers.elementAt(currentViewIndex);
		if (lineNumber + helpTextViewer.getNumberOfVisibleRows() <= helpTextViewer.getNumberOfRows())
			{
			helpTextViewer.setTopVisibleRow(lineNumber);
			}

		currentHighlightedLine = (MiText )helpTextViewer.getPartItem(lineNumber);
		int currentHighlightStart = viewedHighlightStarts.elementAt(currentViewIndex);
		currentHighlightedLine.setSelection(currentHighlightStart, 
			currentHighlightStart + viewedHighlightLengths.elementAt(currentViewIndex));


		dispatchAction(Mi_HELP_VIEW_CHANGED_ACTION);
		}
	public		String		getPreviouslyViewedSection()
		{
		if (currentViewIndex > 0)
			return(viewedSections.elementAt(currentViewIndex - 1));
		return(null);
		}
	public		String		getNextViewedSection()
		{
		if (currentViewIndex < viewedSections.size() - 1)
			return(viewedSections.elementAt(currentViewIndex + 1));
		return(null);
		}
	public		void		deHighlightAll()
		{
		currentHighlightedLine.setSelection(0, 0);
		}
			
	public		int		getCurrentSectionOffset()
		{
		return(currentHighlightOffset);
		}
 	public		void		setCurrentSection(	String sectionName,
								int highlightOffset, int highlightLength)
		{
		if ((currentSectionIndex == contents.indexOf(sectionName))
			&& (currentHighlightOffset == highlightOffset))
			{
			return;
			}

		String helpText = (String )contentsTable.get(sectionName);
		if (helpText.equals(Mi_SECTION_HEADER_ONLY))
			return;

		getRootWindow().setMouseAppearance(MiiTypes.Mi_WAIT_CURSOR);

		currentHighlightedLine.setSelection(0, 0);

		if (currentSectionIndex != contents.indexOf(sectionName))
			displayContentsForSection(sectionName);


		currentSectionIndex = contents.indexOf(sectionName);
		int lineNumber = 0;
		int highlightStart = 0;
		if (highlightOffset > 0)
			{
			lineNumber = Utility.numOccurancesOf(helpText, '\n', 0, highlightOffset);
			highlightStart = highlightOffset - getOffsetOfCurrentSectionLineNumber(lineNumber);
			}
		currentHighlightOffset = highlightOffset;

		if (lineNumber + helpTextViewer.getNumberOfVisibleRows() <= helpTextViewer.getNumberOfRows())
			helpTextViewer.setTopVisibleRow(lineNumber);

		currentHighlightedLine = (MiText )helpTextViewer.getPartItem(lineNumber);
		currentHighlightedLine.setSelection(highlightStart, highlightStart + highlightLength);

		if (!sectionName.equals(contentsTreeList.getValue()))
			contentsTreeList.setValue(sectionName);

		while (viewedSections.size() > currentViewIndex + 1)
			{
			viewedSections.removeLastElement();
			viewedLineNumbers.removeLastElement();
			viewedHighlightStarts.removeLastElement();
			viewedHighlightLengths.removeLastElement();
			}
			
		viewedSections.addElement(sectionName);
		viewedLineNumbers.addElement(lineNumber);
		viewedHighlightStarts.addElement(highlightStart);
		viewedHighlightLengths.addElement(highlightLength);

		++currentViewIndex;

		dispatchAction(Mi_HELP_VIEW_CHANGED_ACTION);

		getRootWindow().setMouseAppearance(MiiTypes.Mi_DEFAULT_CURSOR);
		}
	public		int		getOffsetOfCurrentSectionLineNumber(int lineNumber)
		{
		String helpText = (String )contentsTable.get(contents.elementAt(currentSectionIndex));
		Strings list = new Strings(helpText);
		int index = 0;
		for (int i = 0; i < lineNumber; ++i)
			{
			index += list.elementAt(i).length() + 1;
			}
		return(index);
		}

	public		void		load(InputStream inputStream, String streamName) throws IOException
		{
		String line;
		this.streamName 	= streamName;
		currentViewIndex 	= -1;
		currentSectionIndex 	= -1;
		currentHighlightOffset 	= -1;
		viewedSections	 	= new Strings();
		viewedLineNumbers	= new IntVector();
		viewedHighlightStarts	= new IntVector();
		viewedHighlightLengths	= new IntVector();
		contents 		= new Strings();
		contentsTable 		= new Hashtable();
		graphicsContentTable 	= new Hashtable();
		BufferedReader in 	= new BufferedReader(new InputStreamReader(inputStream));
		String sectionHeader	= new String();
		String sectionText	= new String();
		while (true)
			{
			line = in.readLine();
			if ((line == null) || ((line.trim().length() > 0) && (line.charAt(0) == '.')))
				{
				if ((sectionText.length() != 0) && (sectionHeader.length() != 0))
					contentsTable.put(sectionHeader, sectionText);
				else if (sectionHeader.length() != 0)
					contentsTable.put(sectionHeader, Mi_SECTION_HEADER_ONLY);

				if (sectionHeader.length() != 0)
					contents.addElement(sectionHeader);

				sectionHeader = new String();
				sectionText = new String();
				if (line == null)
					break;

				sectionHeader = line.substring(1);
				if (includeSectionHeadersInContent)
					sectionText += includedSectionHeadersAttributes + sectionHeader + "\n";
				}
			else
				{
				line = Utility.replaceAllTabsWithSpaces(line, 8);
				sectionText += line + "\n";
				}
			}
		for (int i = 0; i < contents.size(); ++i)
			{
			String parentSection = null;
			String key = contents.elementAt(i);
			String sectionName = key;

			int j = 0;
			for (j = 0; (j < sectionName.length()) && ((sectionName.charAt(j) == '.') 
				|| (Character.isDigit(sectionName.charAt(j)))); ++j)
				{
				if (parentSection == null)
					parentSection = new String();
				parentSection += sectionName.charAt(j);
				}
			
			if (parentSection != null)
				{
				int index = parentSection.lastIndexOf('.');
				if (index != -1)
					parentSection = parentSection.substring(0, index).trim();
				}
			String sectionNumber = sectionName.substring(0, j);
			if (!includeSectionNumbersInTableOfContents)
				{
				sectionName = sectionName.substring(j);
				contents.setElementAt(sectionName, i);
				}

//System.out.println("sectionName = " + sectionName);
//System.out.println("sectionNumber = " + sectionNumber);
//System.out.println("parentSection = " + parentSection);
			boolean hasChildren = contentsTable.get(key).equals(Mi_SECTION_HEADER_ONLY);
			contentsTreeList.addItem(
				sectionName, null, sectionNumber, parentSection, hasChildren);
			}
		}

	protected	void		displayContentsForSection(String sectionName)
		{
		String content = (String )contentsTable.get(sectionName);
		if (fastButSimpleDisplay)
			{
			helpTextViewer.setItem(content, 0);
			return;
			}
		MiParts parts;
		if ((parts = (MiParts )graphicsContentTable.get(sectionName)) == null)
			{
			Strings strings = new Strings(content);
			parts = formatGraphicsContent(strings);
			graphicsContentTable.put(sectionName, parts);
			contentsTable.put(sectionName, strings.getLineFeedDelimitedLines());
			}
		helpTextViewer.removeAllItems();
		helpTextViewer.appendItems(parts);
		}
					// Modifies strings to remove all format commands
	protected	MiParts		formatGraphicsContent(Strings content)
		{
		MiParts parts = new MiParts();
		for (int i = 0; i < content.size(); ++i)
			{
			String line = content.elementAt(i);
			boolean isBold = false;
			boolean isItalic = false;
			boolean isUnderlined = false;
			boolean isSized = false;
			int size = 0;
			int index;
			if (line.indexOf(Mi_BOLD_HTML_NAME) != -1)
				{
				isBold = true;
				line = Utility.replaceAll(line, Mi_BOLD_HTML_NAME, "");
				line = Utility.replaceAll(line, Mi_END_BOLD_HTML_NAME, "");
				}
			if (line.indexOf(Mi_ITALIC_HTML_NAME) != -1)
				{
				isItalic = true;
				line = Utility.replaceAll(line, Mi_ITALIC_HTML_NAME, "");
				line = Utility.replaceAll(line, Mi_END_ITALIC_HTML_NAME, "");
				}
			if (line.indexOf(Mi_UNDERLINE_HTML_NAME) != -1)
				{
				isUnderlined = true;
				line = Utility.replaceAll(line, Mi_UNDERLINE_HTML_NAME, "");
				line = Utility.replaceAll(line, Mi_END_UNDERLINE_HTML_NAME, "");
				}
			if ((index = line.indexOf(Mi_SIZE_HTML_NAME)) != -1)
				{
				isSized = true;
				String spec = Mi_SIZE_HTML_NAME;
				String sizeName = new String();
				char ch;

				index += Mi_SIZE_HTML_NAME.length();
				while ((ch = line.charAt(index)) != '>')
					{
					if (Character.isDigit(ch))
						sizeName += ch;
					spec += ch;
					++index;
					}
				size = Utility.toInteger(sizeName);
				line = Utility.replaceAll(line, spec + ">", "");
				line = Utility.replaceAll(line, Mi_END_SIZE_HTML_NAME, "");
				}
			content.setElementAt(line, i);

			MiPart part = helpTextViewer.makeMiText(line);
			if (isBold)
				part.setFontBold(true);
			if (isItalic)
				part.setFontItalic(true);
			if (isUnderlined)
				part.setFontUnderlined(true);
			if (isSized)
				part.setFontPointSize(size);
			parts.addElement(part);
			}
		return(parts);
		}

	public		boolean		processAction(MiiAction action)
		{
		if (action.hasActionType(Mi_ITEM_SELECTED_ACTION))
			{
			// ---------------------------------------------------------------
			// Chose a different section?
			// ---------------------------------------------------------------
			String sectionKey = contentsTreeList.getSelectedItem();
			if (currentSectionIndex != contents.indexOf(sectionKey))
				setCurrentSection(sectionKey, -1, -1);
			}
		return(true);
		}
	}
