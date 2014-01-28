
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


/**
 * This class represents an operation on a MiConnection and is used
 * by such methods as: MiPart#isValidConnectionSource and 
 * MiPart#isValidConnectionDestination to validate that the connection
 * between the source (at the source connection point) and the destination
 * (at the destination connection point) is valid for the given 
 * connection (of perhaps a specific type).
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiConnectionOperation
	{
	private		MiConnection	prototypeConnection;
	private		MiConnection	connection;
	private		MiPart		source;
	private		MiPart		destination;
	private		int		sourceConnPt;
	private		int		destConnPt;



	public				MiConnectionOperation(MiConnection prototypeConnection, MiPart source, MiPart destination)
		{
		this.prototypeConnection = prototypeConnection;
		this.source = source;
		this.destination = destination;
		}

	/**
	 * Operation will/did use a copy of this prototype to
	 * make the connection...
	 */
/*
	public		void		setPrototypeConnection(MiConnection prototypeConnection)
		{
		this.prototypeConnection = prototypeConnection;
		}
*/
	public		MiConnection	getPrototypeConnection()
		{
		return(prototypeConnection);
		}

	/**
	 *  Operation will/did use a this connection to
	 *  make the connection if not null...
	 */
	public		void		setConnection(MiConnection connection)
		{
		this.connection = connection;
		}
	public		MiConnection	getConnection()
		{
		return(connection);
		}
	public		void		setSource(MiPart source)
		{
		this.source = source;
		}
	public		MiPart	getSource()
		{
		return(source);
		}
	public		void		setDestination(MiPart destination)
		{
		this.destination = destination;
		}
	public		MiPart	getDestination()
		{
		return(destination);
		}
	public		void		setSourceConnPt(int connectionPt)
		{
		sourceConnPt = connectionPt;
		}
	public		int		getSourceConnPt()
		{
		return(sourceConnPt);
		}
	public		void		setDestinationConnPt(int connectionPt)
		{
		destConnPt = connectionPt;
		}
	public		int		getDestinationConnPt()
		{
		return(destConnPt);
		}

	}

