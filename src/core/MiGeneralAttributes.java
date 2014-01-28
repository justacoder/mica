
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

/**----------------------------------------------------------------------------------------------
 * This class allocates and maintains an arbitrary group of
 * attribute values. The values can be either object-valued,
 * integer-valued, double-valued, or boolean-valued. Each
 * attribute can also be specified as being inherited.
 * <p>
 * Instances of this class can be immutable (the default)
 * which means modifications wil return a new instance, leaving
 * the original unchanged.
 * <p>
 * Instances of this class are cached (see MiAttributeCache)
 * to reduce memory requirements.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public abstract class MiGeneralAttributes implements MiiCopyable, Cloneable
	{
	private 	int		id;
	private 	int		hash		= -1;
	private		int		objHash;
	private		int		intHash;
	private		int		doubleHash;
	private		int		booleanHash;
	private		long		assignedHash;

	private		int		startObjectIndex;
	private		int		endObjectIndex;
	private		int		startIntegerIndex;
	private		int		endIntegerIndex;
	private		int		startDoubleIndex;
	private		int		endDoubleIndex;
	private		int		startBooleanIndex;
	private		int		endBooleanIndex;

	private		int		numberOfAttributes;

	private static final int	OBJECT_TYPE		= 1;
	private static final int	INTEGER_TYPE		= 2;
	private static final int	DOUBLE_TYPE		= 4;
	private static final int	BOOLEAN_TYPE		= 8;
	private static final int	INHERITED_TYPE		= 16;
	private static final int	ALL_TYPES		= OBJECT_TYPE
									| INTEGER_TYPE
									| DOUBLE_TYPE
									| BOOLEAN_TYPE
									| INHERITED_TYPE;
	private		int		makeNewArray		= ALL_TYPES;

	private		int		numUsing;

	protected	Object[]	objectAttributes;
	protected	int[]		intAttributes;
	protected	boolean[]	booleanAttributes;
	protected	double[]	doubleAttributes;
	protected	boolean[]	assignedAttributes;

	private		boolean		immutable	= true;



					/**------------------------------------------------------
	 				 * Constructs a new MiGeneralAttributes.
					 * @param immutable	true if this cannot be changed.
					 * @param numberOfAttributes
					 *			the total number of attributes
					 *			(which is just the sum of the numbers
					 *			below)
					 * @param startObjIndex	the first index for object attributes
					 * @param numObjAttributes
					 *			the number of object attributes
					 * @param startIntIndex	the first index for int attributes
					 * @param numIntAttributes
					 *			the number of int attributes
					 * @param startDblIndex	the first index for double attributes
					 * @param numDblAttributes
					 *			the number of double attributes
					 * @param startBoolIndex the first index for boolean attributes
					 * @param numBoolAttributes
					 *			the number of boolean attributes
					 *------------------------------------------------------*/
	public				MiGeneralAttributes(boolean immutable,
						int numberOfAttributes,
						int startObjIndex, int numObjAttributes,
						int startIntIndex, int numIntAttributes,
						int startDblIndex, int numDblAttributes,
						int startBoolIndex, int numBoolAttributes)
		{
		this(	numberOfAttributes,
			startObjIndex, numObjAttributes,
			startIntIndex, numIntAttributes,
			startDblIndex, numDblAttributes,
			startBoolIndex, numBoolAttributes);
		this.immutable = immutable;
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiGeneralAttributes.
					 * @param numberOfAttributes
					 *			the total number of attributes
					 *			(which is just the sum of the numbers
					 *			below)
					 * @param startObjIndex	the first index for object attributes
					 * @param numObjAttributes
					 *			the number of object attributes
					 * @param startIntIndex	the first index for int attributes
					 * @param numIntAttributes
					 *			the number of int attributes
					 * @param startDblIndex	the first index for double attributes
					 * @param numDblAttributes
					 *			the number of double attributes
					 * @param startBoolIndex the first index for boolean attributes
					 * @param numBoolAttributes
					 *			the number of boolean attributes
					 *------------------------------------------------------*/
	public				MiGeneralAttributes(int numberOfAttributes,
						int startObjIndex, int numObjAttributes,
						int startIntIndex, int numIntAttributes,
						int startDblIndex, int numDblAttributes,
						int startBoolIndex, int numBoolAttributes)
		{
/****
		if (numberOfAttributes > 64)
			throw new IllegalArgumentException("Too many attributes(" + numberOfAttributes + ">64)");
		if (numBoolAttributes > 32)
			throw new IllegalArgumentException("Too many boolean attributes(" + numBoolAttributes + ">32)");
****/

		id = getAndIncNumCreated();
		objectAttributes = new Object[numObjAttributes];
		intAttributes = new int[numIntAttributes];
		doubleAttributes = new double[numDblAttributes];
		booleanAttributes = new boolean[numBoolAttributes];
		assignedAttributes = new boolean[numberOfAttributes];

		this.numberOfAttributes = numberOfAttributes;

		startObjectIndex = startObjIndex;
		endObjectIndex = startObjIndex + numObjAttributes - 1;
		startIntegerIndex = startIntIndex;
		endIntegerIndex = startIntIndex + numIntAttributes - 1;
		startDoubleIndex = startDblIndex;
		endDoubleIndex = startDblIndex + numDblAttributes - 1;
		startBooleanIndex = startBoolIndex;
		endBooleanIndex = startBoolIndex + numBoolAttributes - 1;
		}
					/**------------------------------------------------------
	 				 * Gets the number of MiGeneralAttributes created and 
					 * increases the number at the same time.
					 * @return 		the number of MiGeneralAttributes 
					 *			created
					 * @overrides 		MiGeneralAttributes
					 *------------------------------------------------------*/
	protected abstract int		getAndIncNumCreated();
					/**------------------------------------------------------
	 				 * Gets the cache that stores reusable, immutable 
					 * MiGeneralAttributes.
					 * @return 		the attribute cache
					 * @overrides 		MiGeneralAttributes
					 *------------------------------------------------------*/
	protected abstract MiAttributeCache getCache();
					/**------------------------------------------------------
	 				 * For debugging, return the number and a description of
					 * all of the attributes created so far.
					 * @return 		a textual description of the
					 *			attribute cache
					 *------------------------------------------------------*/
	public abstract	String		cacheToString();
					/**------------------------------------------------------
	 				 * Gets the name of the attribute specified by the given
					 * index.
					 * @param index 	the index of an attribute
					 * @return 		the name of the attribute
					 *------------------------------------------------------*/
	public abstract	String		getNameOfAttribute(int index);

					/**------------------------------------------------------
	 				 * Gets whether this MiGeneralAttributes can be changed.
					 * @return 		true if immutable
					 *------------------------------------------------------*/
	public		boolean		isImmutable()
		{
		return(immutable);
		}
					/**------------------------------------------------------
	 				 * Sets whether this MiGeneralAttributes can be changed.
					 * @param flag 		true if immutable
					 *------------------------------------------------------*/
	protected	void		setIsImmutable(boolean flag)
		{
		immutable = flag;
		}
					/**------------------------------------------------------
	 				 * Copies the values of the given MiGeneralAttributes to 
					 * this MiGeneralAttributes. Mutability is not checked for.
					 * @param from 		the MiGeneralAttributes to copy
					 *------------------------------------------------------*/
	protected	void		directCopy(MiGeneralAttributes from)
		{
		System.arraycopy(from.objectAttributes, 0, objectAttributes, 0, objectAttributes.length);
		System.arraycopy(from.intAttributes, 0, intAttributes, 0, intAttributes.length);
		System.arraycopy(from.doubleAttributes, 0, doubleAttributes, 0, doubleAttributes.length);
		System.arraycopy(from.booleanAttributes, 0, booleanAttributes, 0, booleanAttributes.length);
		System.arraycopy(
			from.assignedAttributes, 0, assignedAttributes, 0, assignedAttributes.length);
		hash = from.hashCode();
		}
					/**------------------------------------------------------
	 				 * Gets whether this MiGeneralAttributes and the given 
					 * MiGeneralAttributes
					 * are equal.
					 * @param obj 		the MiGeneralAttributes to compare to
					 * @return  		true if they are equal
					 * @overrides		java.lang.Object#equals
					 *------------------------------------------------------*/
	public		boolean		equals(Object obj)
		{
		if (obj == this)
			{
			return(true);
			}
		if (obj.hashCode() != hash)
			return(false);

		MiGeneralAttributes other = (MiGeneralAttributes )obj;
		if ((booleanHash != other.booleanHash) || (assignedHash != other.assignedHash)
			|| (intHash != other.intHash) || (doubleHash != other.doubleHash)
			|| (objHash != other.objHash))
			{
			return(false);
			}

		for (int i = 0; i < objectAttributes.length; ++i)
			{
			if ((objectAttributes[i] != other.objectAttributes[i])
				// ... same color, different objects, should fix this by caching...
				&& ((objectAttributes[i] == null)
					|| (!objectAttributes[i].equals(other.objectAttributes[i]))))
				{
				return(false);
				}
			}
		for (int i = 0; i < intAttributes.length; ++i)
			{
			if (intAttributes[i] != other.intAttributes[i])
				return(false);
			}
		for (int i = 0; i < doubleAttributes.length; ++i)
			{
			if (doubleAttributes[i] != other.doubleAttributes[i])
				return(false);
			}
		for (int i = 0; i < assignedAttributes.length - 64; ++i)
			{
			if (assignedAttributes[i] != other.assignedAttributes[i])
				return(false);
			}
		return(true);
		}
					/**------------------------------------------------------
	 				 * Gets the hopefully unique value identifying this 
					 * MiGeneralAttributes based on the values of the attributes.
					 * This is used by the attribute cache manager to store
					 * and retrieve MiGeneralAttributes.
					 * @return  		the hash code for this object
					 * @overrides		java.lang.Object#hashCode
					 *------------------------------------------------------*/
	public 		int 		hashCode()
		{
		if (hash == -1)
			makeHashCode();
		return(hash);
		}
					/**------------------------------------------------------
	 				 * Calculates, sets and the returns the hash code for this
					 * class.
					 * @return  		the hash code for this object
					 *------------------------------------------------------*/
	protected	int 		makeHashCode()
		{
		if ((makeNewArray & OBJECT_TYPE) != 0)
			{
			objHash = 0;
			// getting the Hashcode for Strings is a length process, just do every other 4th
			for (int i = 0; i < objectAttributes.length; i += 1)
				{
				if (objectAttributes[i] != null)
					objHash = (objHash << 1) + objectAttributes[i].hashCode();
				}
			}
		if ((makeNewArray & INTEGER_TYPE) != 0)
			{
			intHash = 0;
			for (int i = 0; i < intAttributes.length; i += 1)
				{
				if (intAttributes[i] != 0)
					intHash = (intHash << 1) + intAttributes[i];
				}
			}
		if ((makeNewArray & DOUBLE_TYPE) != 0)
			{
			doubleHash = 0;
			for (int i = 0; i < doubleAttributes.length; i += 1)
				{
				if (doubleAttributes[i] != 0)
					doubleHash = (doubleHash << 1) + (int )doubleAttributes[i];
				}
			}
		if ((makeNewArray & BOOLEAN_TYPE) != 0)
			{
			booleanHash = 0;
			for (int i = 0; i < booleanAttributes.length; ++i)
				{
				booleanHash = booleanHash << 1;
				if (booleanAttributes[i])
					booleanHash += 1;
				}
			}
		if ((makeNewArray & INHERITED_TYPE) != 0)
			{
			assignedHash = 0;
			for (int i = 0; i < assignedAttributes.length; ++i)
				{
				assignedHash = assignedHash << 1;
				if (assignedAttributes[i])
					assignedHash += 1;
				}
			}
		hash = objHash 
			+ (((intHash + 0x1234) * 1234)) 
			+ (((doubleHash + 0x2345) * 2345)) 
			+ ((booleanHash + 0x3241) * 3241)
			+ (int )(((assignedHash + 0x4567) & 0xffffffff) * 4567)
			+ (int )((assignedHash >>> 32) + 0x5678) * 5678;

		return(hash);
		}
					/**------------------------------------------------------
	 				 * Makes and returns a copy of this class.
					 * @return  		the copy of this class
					 * @implements		MiiCopyable
					 *------------------------------------------------------*/
	public		Object		makeCopy()
		{
		MiGeneralAttributes atts;
		try	{
			atts = (MiGeneralAttributes )clone();

			atts.numUsing = 0;

			atts.id = getAndIncNumCreated();
			if ((makeNewArray & OBJECT_TYPE) != 0)
				atts.objectAttributes = (Object[] )objectAttributes.clone();
			if ((makeNewArray & INTEGER_TYPE) != 0)
				atts.intAttributes = (int[] )intAttributes.clone();
			if ((makeNewArray & DOUBLE_TYPE) != 0)
				atts.doubleAttributes = (double[] )doubleAttributes.clone();
			if ((makeNewArray & BOOLEAN_TYPE) != 0)
				atts.booleanAttributes = (boolean[] )booleanAttributes.clone();
			if ((makeNewArray & INHERITED_TYPE) != 0)
				atts.assignedAttributes = (boolean[] )assignedAttributes.clone();

			atts.makeNewArray = ALL_TYPES;
			}
        	catch (CloneNotSupportedException e) 
			{ 
			throw new RuntimeException(); 
			}
		return(atts);
		}
					/**------------------------------------------------------
	 				 * Sets the value of the attribute with the given index
					 * to the given value. Note: mutability is not checked.
					 * @param which		the index of the attribute
					 * @param obj		the new value of the attribute
					 *------------------------------------------------------*/
	public		void		setStaticAttribute(int which, Object obj)
		{
		objectAttributes[which - startObjectIndex] = obj;
		assignedAttributes[which] = true;
		}
					/**------------------------------------------------------
	 				 * Sets the value of the attribute with the given index
					 * to the given value. Note: mutability is not checked.
					 * @param which		the index of the attribute
					 * @param value		the new value of the attribute
					 *------------------------------------------------------*/
	public		void		setStaticAttribute(int which, int value)
		{
		intAttributes[which - startIntegerIndex] = value;
		assignedAttributes[which] = true;
		}
					/**------------------------------------------------------
	 				 * Sets the value of the attribute with the given index
					 * to the given value. Note: mutability is not checked.
					 * @param which		the index of the attribute
					 * @param value		the new value of the attribute
					 *------------------------------------------------------*/
	public		void		setStaticAttribute(int which, double value)
		{
		doubleAttributes[which - startDoubleIndex] = value;
		assignedAttributes[which] = true;
		}
					/**------------------------------------------------------
	 				 * Sets the value of the attribute with the given index
					 * to the given value. Note: mutability is not checked.
					 * @param which		the index of the attribute
					 * @param value		the new value of the attribute
					 *------------------------------------------------------*/
	public		void		setStaticAttribute(int which, boolean value)
		{
		booleanAttributes[which - startBooleanIndex] = value;
		assignedAttributes[which] = true;
		}
					/**------------------------------------------------------
	 				 * Gets whether the value of the attribute with the given
					 * index is to be inherited. Whenever a value is assigned
					 * to an attribute, it is set to be NOT inherited.
					 * @param which		the index of the attribute
					 * @return 		true if the attribute is inherited
					 *------------------------------------------------------*/
	public		boolean		isInheritedAttribute(int which)
		{
		return(!assignedAttributes[which]);
		}
					/**------------------------------------------------------
	 				 * Sets whether the value of the attribute with the given
					 * index is to be inherited. Whenever a value is assigned
					 * to an attribute, it is set to be NOT inherited. 
					 * @param which		the index of the attribute
					 * @param flag		true if the attribute is inherited
					 *------------------------------------------------------*/
	public		MiGeneralAttributes setIsInheritedAttribute(int which, boolean flag)
		{
		assignedAttributes[which] = !flag;
		if (!immutable)
			{
			assignedAttributes[which] = !flag;
			return(this);
			}
		int whichObject = which - startObjectIndex;
		if (assignedAttributes[which] != flag)
			{
			return(this);
			}
		int wasHash = hash;
		long wasAssignedHash = assignedHash;

		makeNewArray = INHERITED_TYPE;
		assignedAttributes[which] = !flag;

		makeHashCode();
		MiGeneralAttributes att = (MiGeneralAttributes )getCache().getCopyFromUniqueObjectsPool(this);
		makeNewArray = ALL_TYPES;

		hash = wasHash;
		assignedHash = wasAssignedHash;
		assignedAttributes[which] = flag;
		return(att);
		}
					/**------------------------------------------------------
	 				 * Sets the values of all inherited attributes from the
					 * given attributes. The 'inherited flag' is not modified 
					 * for any of the attributes.
					 * @param from		the attributes from which we will
					 *			get the values to assign to this
					 *			attributes inherited attribute values
					 * @return 		the resultant attributes
					 *------------------------------------------------------*/
	protected	MiGeneralAttributes	inheritFromAll(MiGeneralAttributes from)
		{
		// ---------------------------------------------------------------
		// Assume that the user has not assigned any values to this attributes
		// and therefore we copy all of the 'from' attributes into the new resultant attributes
		// except for the array which specifies which values have been assiged to this one...
		// ---------------------------------------------------------------
		from.makeNewArray = ALL_TYPES & ~INHERITED_TYPE;
		MiGeneralAttributes tmp = (MiGeneralAttributes )from.makeCopy();
		tmp.assignedAttributes = (boolean[] )assignedAttributes.clone();
		from.makeNewArray = ALL_TYPES;

		// ---------------------------------------------------------------
		// If this attributes has values assigned by the user...
		// ---------------------------------------------------------------
		if (assignedHash != 0)
			{
			for (int i = 0; i < assignedAttributes.length; ++i)
				{
				// ---------------------------------------------------------------
				// ... then, for each value assigned to this attributes, assign it also
				// to the resultant attributes
				// ---------------------------------------------------------------
				if (assignedAttributes[i])
					{
					if (i <= endObjectIndex)
						{
						tmp.objectAttributes[i] = objectAttributes[i];
						}
					else if (i <= endIntegerIndex)
						{
						tmp.intAttributes[i - startIntegerIndex] 
							= intAttributes[i - startIntegerIndex];
						}
					else if (i <= endDoubleIndex)
						{
						tmp.doubleAttributes[i - startDoubleIndex] 
							= doubleAttributes[i - startDoubleIndex];
						}
					else
						{
						tmp.booleanAttributes[i - startBooleanIndex] 
							= booleanAttributes[i - startBooleanIndex];
						}
					}
				}
			}

		tmp.makeHashCode();
		if (!tmp.immutable)
			return(tmp);
		MiGeneralAttributes att = (MiGeneralAttributes )getCache().getCopyFromUniqueObjectsPool(tmp);
		return(att);
		}
					/**------------------------------------------------------
	 				 * Sets the values of any inherited attributes in this
					 * MiGeneralAttributes from the non-inherited attributes
					 * in the given MiGeneralAttributes . The 'inherited flag'
					 * is not modified for any of the attributes.
					 * @param from		the attributes from which we will
					 *			get the non-inherited values to 
					 *			assign to this attributes inherited 
					 *			attribute values
					 * @return 		the resultant attributes
					 *------------------------------------------------------*/
	protected	MiGeneralAttributes	inheritFrom(MiGeneralAttributes from)
		{
		MiGeneralAttributes tmp = !immutable ? this : (MiGeneralAttributes )makeCopy();
		for (int i = 0; i < assignedAttributes.length; ++i)
			{
			if ((!assignedAttributes[i]) && (from.assignedAttributes[i]))
				{
				if (i <= endObjectIndex)
					tmp.objectAttributes[i] = from.objectAttributes[i];
				else if (i <= endIntegerIndex)
					tmp.intAttributes[i - startIntegerIndex] = from.intAttributes[i - startIntegerIndex];
				else if (i <= endDoubleIndex)
					tmp.doubleAttributes[i - startDoubleIndex] = from.doubleAttributes[i - startDoubleIndex];
				else
					tmp.booleanAttributes[i - startBooleanIndex] = from.booleanAttributes[i - startBooleanIndex];
				}
			}
		tmp.makeHashCode();
		if (!immutable)
			return(tmp);
		MiGeneralAttributes att = (MiGeneralAttributes )getCache().getCopyFromUniqueObjectsPool(tmp);
		return(att);
		}
					/**------------------------------------------------------
	 				 * Sets the values of all attributes in this 
					 * MiGeneralAttributes for which the corresponding attribute
					 * in the given MiGeneralAttributes is not inherited.
					 * The 'inherited flag' is not modified for any of the attributes.
					 * @param from		the attributes from which we will
					 *			get the non-inherited values to 
					 *			assign to this attributes attribute
					 *			values
					 * @return 		the resultant attributes
					 *------------------------------------------------------*/
	protected	MiGeneralAttributes	overrideFrom(MiGeneralAttributes from)
		{
		MiGeneralAttributes tmp = !immutable ? this : (MiGeneralAttributes )makeCopy();
		for (int i = 0; i < assignedAttributes.length; ++i)
			{
			if (from.assignedAttributes[i])
				{
				if (i <= endObjectIndex)
					tmp.objectAttributes[i] = from.objectAttributes[i];
				else if (i <= endIntegerIndex)
					tmp.intAttributes[i - startIntegerIndex] = from.intAttributes[i - startIntegerIndex];
				else if (i <= endDoubleIndex)
					tmp.doubleAttributes[i - startDoubleIndex] = from.doubleAttributes[i - startDoubleIndex];
				else
					tmp.booleanAttributes[i - startBooleanIndex] = from.booleanAttributes[i - startBooleanIndex];
				}
			}
		tmp.makeHashCode();
		if (!immutable)
			return(tmp);
		MiGeneralAttributes att = (MiGeneralAttributes )getCache().getCopyFromUniqueObjectsPool(tmp);
		return(att);
		}
					/**------------------------------------------------------
	 				 * Sets the values of all attributes in this 
					 * MiGeneralAttributes for which the corresponding attribute
					 * in the given MiGeneralAttributes is not inherited AND
					 * the for which the corresponding attribute in this
					 * MiGeneralAttributes IS inherited.
					 * The 'inherited flag' IS modified for all of the attributes
					 * overridden.
					 * @param from		the attributes from which we will
					 *			get the non-inherited values to 
					 *			assign to this attributes attribute
					 *			values
					 * @return 		the resultant attributes
					 *------------------------------------------------------*/
	protected	MiGeneralAttributes	overrideFromPermanent(MiGeneralAttributes from)
		{
		MiGeneralAttributes tmp = !immutable ? this : (MiGeneralAttributes )makeCopy();
		for (int i = 0; i < assignedAttributes.length; ++i)
			{
			if ((from.assignedAttributes[i]) && (!assignedAttributes[i]))
				{
				if (i <= endObjectIndex)
					tmp.objectAttributes[i] = from.objectAttributes[i];
				else if (i <= endIntegerIndex)
					tmp.intAttributes[i - startIntegerIndex] = from.intAttributes[i - startIntegerIndex];
				else if (i <= endDoubleIndex)
					tmp.doubleAttributes[i - startDoubleIndex] = from.doubleAttributes[i - startDoubleIndex];
				else
					tmp.booleanAttributes[i - startBooleanIndex] = from.booleanAttributes[i - startBooleanIndex];
				tmp.assignedAttributes[i] = true;
				}
			}
		tmp.makeHashCode();
		if (!immutable)
			return(tmp);
		MiGeneralAttributes att = (MiGeneralAttributes )getCache().getCopyFromUniqueObjectsPool(tmp);
		return(att);
		}
					/**------------------------------------------------------
	 				 * Sets the values of all attributes in this 
					 * MiGeneralAttributes for which the corresponding attribute
					 * in the given MiGeneralAttributes is not inherited.
					 * The 'inherited flag' IS modified for all of the attributes
					 * overridden.
					 * @param from		the attributes from which we will
					 *			get the non-inherited values to 
					 *			assign to this attributes attribute
					 *			values
					 * @return 		the resultant attributes
					 *------------------------------------------------------*/
	protected	MiGeneralAttributes	overrideFromPermanent2(MiGeneralAttributes from)
		{
		MiGeneralAttributes tmp = !immutable ? this : (MiGeneralAttributes )makeCopy();
		for (int i = 0; i < assignedAttributes.length; ++i)
			{
			if (from.assignedAttributes[i])
				{
				if (i <= endObjectIndex)
					tmp.objectAttributes[i] = from.objectAttributes[i];
				else if (i <= endIntegerIndex)
					tmp.intAttributes[i - startIntegerIndex] = from.intAttributes[i - startIntegerIndex];
				else if (i <= endDoubleIndex)
					tmp.doubleAttributes[i - startDoubleIndex] = from.doubleAttributes[i - startDoubleIndex];
				else
					tmp.booleanAttributes[i - startBooleanIndex] = from.booleanAttributes[i - startBooleanIndex];
				tmp.assignedAttributes[i] = true;
				}
			}
		tmp.makeHashCode();
		if (!immutable)
			return(tmp);
		MiGeneralAttributes att = (MiGeneralAttributes )getCache().getCopyFromUniqueObjectsPool(tmp);
		return(att);
		}
					/**------------------------------------------------------
	 				 * Sets all attributes to be 'not inherited'.
					 * @return 		the resultant attributes
					 *------------------------------------------------------*/
	protected	MiGeneralAttributes	initializeAsOverrideAttributes(boolean flag)
		{
		MiGeneralAttributes tmp = !immutable ? this : (MiGeneralAttributes )makeCopy();
		for (int i = 0; i < tmp.assignedAttributes.length; ++i)
			{
			tmp.assignedAttributes[i] = flag;
			}
		tmp.makeHashCode();
		if (!immutable)
			return(tmp);
		MiGeneralAttributes att = (MiGeneralAttributes )getCache().getCopyFromUniqueObjectsPool(tmp);
		return(att);
		}
					/**------------------------------------------------------
	 				 * Sets all attributes to be inherited.
					 * @return 		the resultant attributes
					 *------------------------------------------------------*/
	protected	void		initializeAsInheritedAttributes()
		{
		for (int i = 0; i < assignedAttributes.length; ++i)
			{
			assignedAttributes[i] = false;
			}
		}
	protected	MiGeneralAttributes	getImmutableCopyFromCache()
		{
		return((MiGeneralAttributes )getCache().getCopyFromUniqueObjectsPool(this));
		}
					/**------------------------------------------------------
	 				 * Copies the value of the specific attribute specified
					 * by the given index from the 'from' attributes and
					 * returns the resultant attributes.
					 * @param from		the attributes from which we will
					 *			get new value of an attribute
					 * @param which		the index of the attribute to copy
					 * @return 		the resultant attributes
					 *------------------------------------------------------*/
	protected	MiGeneralAttributes	copyAttribute(MiGeneralAttributes from, int which)
		{
		if (which <= endObjectIndex)
			return(getModifiedAttributes(which, from.objectAttributes[which]));
		else if (which <= endIntegerIndex)
			return(getModifiedAttributes(which, from.intAttributes[which - startIntegerIndex]));
		else if (which <= endDoubleIndex)
			return(getModifiedAttributes(which, from.doubleAttributes[which - startDoubleIndex]));
		else
			return(getModifiedAttributes(which, from.booleanAttributes[which - startBooleanIndex]));
		}
					/**------------------------------------------------------
	 				 * Sets the value of the attribute with the given index
					 * to the given value. If mutable then this MiGeneralAttributes
					 * is returned after modification, otherwise a new 
					 * MiGeneralAttributes is created returned with the new
					 * attribute value.
					 * @param which		the index of the attribute
					 * @param value		the new value of the attribute
					 * @return 		the resultant attributes
					 *------------------------------------------------------*/
	public		MiGeneralAttributes	getModifiedAttributes(int which, Object object)
		{
		if (!immutable)
			{
			setStaticAttribute(which, object);
			return(this);
			}
		int whichObject = which - startObjectIndex;
		if ((objectAttributes[whichObject] == object) && (assignedAttributes[which]))
			{
			return(this);
			}
		Object wasObject = objectAttributes[whichObject];
		int wasHash = hash;
		int wasObjHash = objHash;
		long wasAssignedHash = assignedHash;
		boolean wasAssigned = assignedAttributes[which];

		makeNewArray = OBJECT_TYPE;
		if (!wasAssigned)
			{
			assignedAttributes[which] = true;
			makeNewArray |= INHERITED_TYPE;
			}

		objectAttributes[whichObject] = object;
		makeHashCode();
		MiGeneralAttributes att = (MiGeneralAttributes )getCache().getCopyFromUniqueObjectsPool(this);
		makeNewArray = ALL_TYPES;

		hash = wasHash;
		objHash = wasObjHash;
		assignedHash = wasAssignedHash;
		objectAttributes[whichObject] = wasObject;
		assignedAttributes[which] = wasAssigned;
		return(att);
		}

					/**------------------------------------------------------
	 				 * Sets the value of the attribute with the given index
					 * to the given value. If mutable then this MiGeneralAttributes
					 * is returned after modification, otherwise a new 
					 * MiGeneralAttributes is created returned with the new
					 * attribute value.
					 * @param which		the index of the attribute
					 * @param value		the new value of the attribute
					 * @return 		the resultant attributes
					 *------------------------------------------------------*/
	public		MiGeneralAttributes	getModifiedAttributes(int which, int value)
		{
		if (!immutable)
			{
			setStaticAttribute(which, value);
			return(this);
			}
		int whichInt = which - startIntegerIndex;
		if ((intAttributes[whichInt] == value) && (assignedAttributes[which]))
			{
			return(this);
			}

		int wasInt = intAttributes[whichInt];
		int wasHash = hash;
		int wasIntHash = intHash;
		long wasAssignedHash = assignedHash;
		boolean wasAssigned = assignedAttributes[which];

		makeNewArray = INTEGER_TYPE;
		if (!wasAssigned)
			{
			assignedAttributes[which] = true;
			makeNewArray |= INHERITED_TYPE;
			}

		intAttributes[whichInt] = value;
		makeHashCode();
		MiGeneralAttributes att = (MiGeneralAttributes )getCache().getCopyFromUniqueObjectsPool(this);
		makeNewArray = ALL_TYPES;

		hash = wasHash;
		intHash = wasIntHash;
		assignedHash = wasAssignedHash;
		intAttributes[whichInt] = wasInt;
		assignedAttributes[which] = wasAssigned;
		return(att);
		}
					/**------------------------------------------------------
	 				 * Sets the value of the attribute with the given index
					 * to the given value. If mutable then this MiGeneralAttributes
					 * is returned after modification, otherwise a new 
					 * MiGeneralAttributes is created returned with the new
					 * attribute value.
					 * @param which		the index of the attribute
					 * @param value		the new value of the attribute
					 * @return 		the resultant attributes
					 *------------------------------------------------------*/
	public		MiGeneralAttributes	getModifiedAttributes(int which, double value)
		{
		if (!immutable)
			{
			setStaticAttribute(which, value);
			return(this);
			}
		int whichDbl = which - startDoubleIndex;
		if ((doubleAttributes[whichDbl] == value) && (assignedAttributes[which]))
			{
			return(this);
			}
		double wasDbl = doubleAttributes[whichDbl];
		int wasHash = hash;
		int wasDblHash = doubleHash;
		long wasAssignedHash = assignedHash;
		boolean wasAssigned = assignedAttributes[which];

		makeNewArray = DOUBLE_TYPE;
		if (!wasAssigned)
			{
			assignedAttributes[which] = true;
			makeNewArray |= INHERITED_TYPE;
			}

		doubleAttributes[whichDbl] = value;
		makeHashCode();
		MiGeneralAttributes att = (MiGeneralAttributes )getCache().getCopyFromUniqueObjectsPool(this);
		makeNewArray = ALL_TYPES;

		hash = wasHash;
		doubleHash = wasDblHash;
		assignedHash = wasAssignedHash;
		doubleAttributes[whichDbl] = wasDbl;
		assignedAttributes[which] = wasAssigned;
		return(att);
		}
					/**------------------------------------------------------
	 				 * Sets the value of the attribute with the given index
					 * to the given value. If mutable then this MiGeneralAttributes
					 * is returned after modification, otherwise a new 
					 * MiGeneralAttributes is created returned with the new
					 * attribute value.
					 * @param which		the index of the attribute
					 * @param value		the new value of the attribute
					 * @return 		the resultant attributes
					 *------------------------------------------------------*/
	public		MiGeneralAttributes	getModifiedAttributes(int which, boolean value)
		{
		if (!immutable)
			{
			setStaticAttribute(which, value);
			return(this);
			}
		int whichBoolean = which - startBooleanIndex;
		if ((booleanAttributes[whichBoolean] == value) && (assignedAttributes[which]))
			{
			return(this);
			}
		boolean wasBoolean = booleanAttributes[whichBoolean];
		int wasHash = hash;
		int wasBooleanHash = booleanHash;
		long wasAssignedHash = assignedHash;
		boolean wasAssigned = assignedAttributes[which];

		makeNewArray = BOOLEAN_TYPE;
		if (!wasAssigned)
			{
			assignedAttributes[which] = true;
			makeNewArray |= INHERITED_TYPE;
			}

		booleanAttributes[whichBoolean] = value;
		makeHashCode();
		MiGeneralAttributes att = (MiGeneralAttributes )getCache().getCopyFromUniqueObjectsPool(this);
		makeNewArray = ALL_TYPES;

		hash = wasHash;
		booleanHash = wasBooleanHash;
		assignedHash = wasAssignedHash;
		booleanAttributes[whichBoolean] = wasBoolean;
		assignedAttributes[which] = wasAssigned;
		return(att);
		}
					/**------------------------------------------------------
	 				 * Sets the value of the attribute with the given index
					 * to the given value. If mutable then this MiGeneralAttributes
					 * is returned after modification, otherwise a new 
					 * MiGeneralAttributes is created returned with the new
					 * attribute value.
					 * The value is assigned directly for object-valued
					 * attributes, otherwise it is parsed to get the value
					 * (i.e. Utility.toInteger(value) for integer-valued 
					 * attributes and if !Utility.isInteger(value) then the
					 * value is looked up in 
					 * MiSystem.getValueOfAttributeValueName(value)).
					 *
					 * @param which		the index of the attribute
					 * @param value		the new value of the attribute
					 * @return 		the resultant attributes
					 *------------------------------------------------------*/
	public		MiGeneralAttributes	getModifiedAttributes(String name, String value)
		{
		int i = getIndexOfAttribute(name);
		if (i <= endObjectIndex)
			return(getModifiedAttributes(i, value));
		if (i <= endIntegerIndex)
			{
			if (Utility.isInteger(value))
				return(getModifiedAttributes(i, Utility.toInteger(value)));
			try	{
				int val = MiSystem.getValueOfAttributeValueName(value);
				return(getModifiedAttributes(i, val));
				}
			catch (Exception e)
				{
				MiDebug.println("Attribute value: \"" 
					+ value + "\" in not a valid value of attribute: " + name);
				return(this);
				}
			}
		if (i <= endDoubleIndex)
			return(getModifiedAttributes(i, Utility.toDouble(value)));
		else
			return(getModifiedAttributes(i, Utility.toBoolean(value)));
		}
					/**------------------------------------------------------
	 				 * Gets the value of the object-valued attribute.
					 * @param which		the index of the attribute
					 * @return 		the value
					 *------------------------------------------------------*/
	public		Object		getAttribute(int which)
		{
		return(objectAttributes[which - startObjectIndex]);
		}
					/**------------------------------------------------------
	 				 * Gets the value of the integer-valued attribute.
					 * @param which		the index of the attribute
					 * @return 		the value
					 *------------------------------------------------------*/
	public		int		getIntegerAttribute(int which)
		{
		return(intAttributes[which - startIntegerIndex]);
		}
					/**------------------------------------------------------
	 				 * Gets the value of the double-valued attribute.
					 * @param which		the index of the attribute
					 * @return 		the value
					 *------------------------------------------------------*/
	public		double		getDoubleAttribute(int which)
		{
		return(doubleAttributes[which - startDoubleIndex]);
		}
					/**------------------------------------------------------
	 				 * Gets the value of the boolean-valued attribute.
					 * @param which		the index of the attribute
					 * @return 		the value
					 *------------------------------------------------------*/
	public		boolean		getBooleanAttribute(int which)
		{
		return(booleanAttributes[which - startBooleanIndex]);
		}
					/**------------------------------------------------------
	 				 * Gets the number of attributes values
					 * @return 		the number of attribute values
					 *------------------------------------------------------*/
	public		int		getNumberOfAttributes()
		{
		return(numberOfAttributes);
		}
					/**------------------------------------------------------
	 				 * Gets whether an attribute with the given name exists.
					 * @return 		true if such an attribute exists.
					 *------------------------------------------------------*/
	public		boolean		hasAttribute(String name)
		{
		return(getIndexOfAttributeName(name) != -1);
		}
					/**------------------------------------------------------
	 				 * Gets the index of the attribute with the given name.
					 * @return 		the index
					 * @exception 		IllegalArgumentException if the
					 *			name is not a valid attribute name
					 *------------------------------------------------------*/
	public		int 		getIndexOfAttribute(String name)
		{
		int index = getIndexOfAttributeName(name);
		if (index != -1)
			return(index);
		throw new IllegalArgumentException("Invalid name of attribute: " + name);
		}
					/**------------------------------------------------------
	 				 * Sets the attribute with the given index to the given
					 * value. The value is assigned directly to an object-valued
					 * attributes, otherwise it is parsed to get the value
					 * (i.e. Utility.toInteger(value) for integer-valued 
					 * attributes and if !Utility.isInteger(value) then the
					 * value is looked up in 
					 * MiSystem.getValueOfAttributeValueName(value)).
					 * @param index 	the index of an attribute
					 * @param value		the new value of the attribute
					 * @exception		RuntimeException attempt to modify
					 *			immutable attributes
					 *------------------------------------------------------*/
	public		void		setStaticAttributeValue(int index, String value)
		{
		if (immutable)
			{
			throw new RuntimeException("Attempt to modify immutable attributes");
			}
		if (index <= endObjectIndex)
			objectAttributes[index] = value;
		else if (index <= endIntegerIndex)
			{
			if (Utility.isInteger(value))
				intAttributes[index - startIntegerIndex] = Utility.toInteger(value);
			else
				intAttributes[index - startIntegerIndex] 
					= MiSystem.getValueOfAttributeValueName(value);
			}
		else if (index <= endDoubleIndex)
			doubleAttributes[index - startDoubleIndex] = Utility.toDouble(value);
		else
			booleanAttributes[index - startBooleanIndex] = Utility.toBoolean(value);
		}
					/**------------------------------------------------------
	 				 * Gets the value of the attribute with the given name
					 * as a text string. This will be object.toString() for
					 * object-value attributes, MiiTypes.Mi_NULL_VALUE_NAME
					 * for null valued object-value attributes, otherwise it
					 * will be the value in text format.
					 * @return 		the value as a string
					 *------------------------------------------------------*/
	public		String		getAttributeValue(String name)
		{
		int index = getIndexOfAttribute(name);

		if (index <= endObjectIndex)
			return(objectAttributes[index] == null 
				? MiiTypes.Mi_NULL_VALUE_NAME : objectAttributes[index].toString());
		if (index <= endIntegerIndex)
			return(Utility.toString(intAttributes[index - startIntegerIndex]));
		if (index <= endDoubleIndex)
			return(Utility.toString(doubleAttributes[index - startDoubleIndex]));
		else
			return(Utility.toString(booleanAttributes[index - startBooleanIndex]));
		}
					/**------------------------------------------------------
	 				 * Gets the value of the attribute with the given name
					 * as an object. This will be the object for object-value
					 * attributes, and the corresponding java.lang class
					 * otherwise (i.e. Integer, Double, Boolean).
					 * @return 		the value as an object
					 *------------------------------------------------------*/
	public		Object		getAttributeValueAsObject(int index)
		{
		if (index <= endObjectIndex)
			return(objectAttributes[index]);
		if (index <= endIntegerIndex)
			return(new Integer(intAttributes[index - startIntegerIndex]));
		if (index <= endDoubleIndex)
			return(new Double(doubleAttributes[index - startDoubleIndex]));
		else
			return(new Boolean(booleanAttributes[index - startBooleanIndex]));
		}
					/**------------------------------------------------------
	 				 * For debug. Gets a text description of the names and
					 * values of all the attributes in this MiGeneralAttributes.
					 * @return 		the description
					 *------------------------------------------------------*/
	public		String		dump()
		{
		StringBuffer buf = new StringBuffer(1000);

		for (int i = 0; i < numberOfAttributes; ++i)
			{
			buf.append(getNameOfAttribute(i));
			buf.append("=");
			buf.append(getAttributeValueAsObject(i).toString());
			buf.append("\n");
			}
		return(buf.toString());
		}
					/**------------------------------------------------------
					 * Returns information about this MiPart.
					 * @return		textual information (class name +
					 *			unique numerical id)
					 *------------------------------------------------------*/
	public		String		toString()
		{
		return(getClass().getName() + "." + id);
		}
					/**------------------------------------------------------
	 				 * Gets the index of the attribute with the given name.
					 * @return 		the index or -1, if no such attribute
					 *------------------------------------------------------*/
	protected	int 		getIndexOfAttributeName(String name)
		{
		for (int i = 0; i < numberOfAttributes; ++i)
			{
			if (getNameOfAttribute(i).equalsIgnoreCase(name))
				{
				return(i);
				}
			}
		return(-1);
		}

	protected	void		incNumUsing()
		{
		++numUsing;
		}
	protected	void		decNumUsing()
		{
		--numUsing;
		if (numUsing <= 0)
			{
			getCache().removeCopyFromUniqueObjectsPool(this);
			}
		}
	/* for debug */
	public		int		getNumUsing()
		{
		return(numUsing);
		}
	}

