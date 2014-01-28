
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

import java.util.Stack; 
import java.util.Vector; 
import com.swfm.mica.util.Utility; 

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiTransactionManager extends MiContainer implements MiiCommandHandler, MiiCommandNames
	{
	public static final	String	Mi_MAX_NUMBER_TRANSACTIONS_TO_KEEP_ON_STACK 
						= "Mi_MAX_NUMBER_TRANSACTIONS_TO_KEEP_ON_STACK";
	public static final	String	Mi_SET_TRANSACTION_STACK = "Mi_SET_TRANSACTION_STACK";
	public static final	String	Mi_START_TRANSACTION = "Mi_START_TRANSACTION";
	public static final	String	Mi_COMMIT_TRANSACTION = "Mi_COMMIT_TRANSACTION";

	private		Vector		transactions 	= new Vector();
	private		int		transactionIndex;
	private		int		maxNumberToKeep	= 100;
	private		boolean		executing;
	private		Stack		openTransactions 	= new Stack();



	public				MiTransactionManager()
		{
		transactionIndex = -1;
		String maxToKeepStr = MiSystem.getProperty(Mi_MAX_NUMBER_TRANSACTIONS_TO_KEEP_ON_STACK);
		if (maxToKeepStr != null)
			{
			maxNumberToKeep = Utility.toInteger(maxToKeepStr);
			}
		if (maxNumberToKeep < 1)
			{
			maxNumberToKeep = 1;
			}
		}

	public		void		setMaxNumberOfTransactionsToKeep(int number)
		{
		maxNumberToKeep = number;
		}
	public		int		getMaxNumberOfTransactionsToKeep()
		{
		return(maxNumberToKeep);
		}
	public		MiNestedTransaction	startTransaction(MiiTransaction transaction)
		{
		if (executing)
			{
			return(null);
			}
		if (getOpenTransaction() == transaction)
			{
			throw new RuntimeException("Opening already open transaction: " + transaction);
			}
		MiNestedTransaction newTransaction = null;
		if (transaction instanceof MiNestedTransaction)
			{
			newTransaction = (MiNestedTransaction )transaction;
			}
		else
			{
			newTransaction = new MiNestedTransaction(transaction.getName(), transaction);
			}
		if (openTransactions.empty())
			{
			// Remove everything off the undo stack uptothe current point...
			for (int i = transactionIndex + 1; i < transactions.size(); ++i)
				{
				transactions.removeElementAt(i);
				}

			transactions.addElement(newTransaction);
			transactionIndex = transactions.size() - 1;
			}
		else
			{
			getOpenTransaction().addElement(newTransaction);
//MiDebug.printStackTrace("Added newTransaction: " + newTransaction);
//MiDebug.println("Added newTransactionto open transacton: " + getOpenTransaction());
			}
		openTransactions.push(newTransaction);
		//dispatchAction(Mi_TRANSACTION_MANAGER_NEW_TRANSACTION_ACTION, newTransaction); // MiCADTransactionFilter needs this... sowhy is it commented out?
		dispatchAction(Mi_TRANSACTION_MANAGER_CHANGED_ACTION, Mi_START_TRANSACTION);
		return(newTransaction);
		}

	public		void		commitTransaction(MiiTransaction transaction)
		{
		if (executing)
			{
			return;
			}
		if (transaction != getOpenTransaction())
			{
			throw new RuntimeException("Closing unopened transaction: " + transaction);
			}
		openTransactions.pop();
		dispatchAction(Mi_TRANSACTION_MANAGER_CHANGED_ACTION, Mi_COMMIT_TRANSACTION);
		}
					// Canceled in middle
	public		void		rollbackTransaction()
		{
		MiiTransaction openTransaction = (MiiTransaction )openTransactions.pop();
		openTransaction.undo();
		if (openTransactions.empty())
			{
			transactions.removeElementAt(transactions.size() - 1);
			--transactionIndex;
			}
		}
	public		void		appendTransaction(MiiTransaction transaction)
		{
//MiDebug.printStackTrace("appendTransaction: " + transaction);
		if (executing)
			{
			return;
			}

		// Remove everything off the undo stack uptothe current point...
		for (int i = transactionIndex + 1; i < transactions.size(); ++i)
			{
			transactions.removeElementAt(i);
			}
		
		if (!(transaction instanceof MiNestedTransaction))
			{
			transaction = new MiNestedTransaction(transaction.getName(), transaction);
//MiDebug.println("appendTransaction: now is wrapped by nested " + transaction);
			}
//MiDebug.printStackTrace("appendTransaction: " + transaction);

		if (getOpenTransaction() != null)
			{
			((MiNestedTransaction )getOpenTransaction()).addElement(transaction);
//MiDebug.println("appendTransaction: getOpenTransaction = " + getOpenTransaction());
			}
		else
			{
			transactions.addElement(transaction);
//MiDebug.println("appendTransaction: transactions = " + transactions);
			}
		transactionIndex = transactions.size() - 1;
		constrainNumberOfTransactions();
		//dispatchAction(Mi_TRANSACTION_MANAGER_NEW_TRANSACTION_ACTION, transactions.lastElement());
		dispatchAction(Mi_TRANSACTION_MANAGER_NEW_TRANSACTION_ACTION, transaction);
		dispatchAction(Mi_TRANSACTION_MANAGER_CHANGED_ACTION);
		}
	public		void		appendTransaction(String name,
						MiiTransaction transaction1,
						MiiTransaction transaction2)
		{
		appendTransaction(new MiNestedTransaction(name, transaction1, transaction2));
		}

	public		void		appendTransaction(String name,
						MiiTransaction transaction1, 
						MiiTransaction transaction2,
						MiiTransaction transaction3)
		{
		appendTransaction(new MiNestedTransaction(name, transaction1, transaction2, transaction3));
		}
	public		void		undoTransaction()
		{
		if (transactionIndex >= 0)
			{
			MiiTransaction transaction = (MiiTransaction )transactions.elementAt(transactionIndex);
			dispatchAction(Mi_TRANSACTION_MANAGER_EXECUTION_START_UNDO_ACTION, transaction);
			executing = true;
//MiDebug.printStackTrace("MASTER UNDO:" + transaction);
			transaction.undo();
			executing = false;
			dispatchAction(Mi_TRANSACTION_MANAGER_EXECUTION_END_UNDO_ACTION, transaction);
			--transactionIndex;
			dispatchAction(Mi_TRANSACTION_MANAGER_CHANGED_ACTION);
			}
		}
	public		void		redoTransaction()
		{
		if (transactionIndex < transactions.size() - 1)
			{
			MiiTransaction transaction = (MiiTransaction )transactions.elementAt(transactionIndex + 1);
			dispatchAction(Mi_TRANSACTION_MANAGER_EXECUTION_START_REDO_ACTION, transaction);
			executing = true;
			transaction.redo();
			executing = false;
			dispatchAction(Mi_TRANSACTION_MANAGER_EXECUTION_END_REDO_ACTION, transaction);
			++transactionIndex;
			dispatchAction(Mi_TRANSACTION_MANAGER_CHANGED_ACTION);
			}
		}
	public		void		repeatTransaction()
		{
		if (transactionIndex >= 0)
			{
			MiiTransaction transaction = (MiiTransaction )transactions.elementAt(transactionIndex);
			dispatchAction(Mi_TRANSACTION_MANAGER_EXECUTION_START_REPEAT_ACTION, transaction);
			executing = true;
			transaction.repeat();
			executing = false;
			dispatchAction(Mi_TRANSACTION_MANAGER_EXECUTION_END_REPEAT_ACTION, transaction);
			}
		}

					// Usefull when a new 'document' is opened
	public		void		removeAllTransactions()
		{
		if (getOpenTransaction() != null)
			{
			throw new RuntimeException("Not expecting a currently open transaction: " + getOpenTransaction());
			}
		transactions.removeAllElements();
		transactionIndex = -1;
		dispatchAction(Mi_TRANSACTION_MANAGER_CHANGED_ACTION);
		}
					// Usefull when a different 'view' is switched to
	public		void		setTransactionStack(Vector stack, int indexOfNextTransactionToUndo)
		{
		if (getOpenTransaction() != null)
			{
			throw new RuntimeException("Not expecting a currently open transaction: " + getOpenTransaction());
			}
		transactions.removeAllElements();
		transactionIndex = -1;
		for (int i = 0; i < stack.size(); ++i)
			{
			MiiTransaction transaction = (MiiTransaction )stack.elementAt(i);
			if (!(transaction instanceof MiNestedTransaction))
				{
				transaction = new MiNestedTransaction(transaction.getName(), transaction);
				}
			transactions.addElement(transaction);
			//? dispatchAction(Mi_TRANSACTION_MANAGER_NEW_TRANSACTION_ACTION, transactions.lastElement());
			}
		transactionIndex = indexOfNextTransactionToUndo; // transactions.size() - 1;
		constrainNumberOfTransactions();
		dispatchAction(Mi_TRANSACTION_MANAGER_CHANGED_ACTION, Mi_SET_TRANSACTION_STACK);
		}
	public		int		getNumberOfTransactions()
		{
		return(transactions.size());
		}
	public		MiNestedTransaction	getTransaction(int index)
		{
		return((MiNestedTransaction )transactions.elementAt(index));
		}
	public		void		removeTransaction(int index)
		{
		transactions.removeElementAt(index);
		--transactionIndex;
		}

	public		void		removeTransaction(MiiTransaction t)
		{
		if (transactions.contains(t))
			{
			transactions.removeElement(t);
			return;
			}
		for (int i = 0; i < openTransactions.size(); ++i)
			{
			MiNestedTransaction nestedTransaction = (MiNestedTransaction )openTransactions.elementAt(i);
			if (nestedTransaction.contains(t))
				{
				nestedTransaction.removeElement(t);
				return;
				}
			}
		}

	public 		boolean		hasTransactionsToUndo()
		{
		return(transactionIndex >= 0);
		}
	public 		boolean		hasTransactionsToRedo()
		{
		return(transactionIndex < transactions.size() - 1);
		}
	public 		boolean		hasTransactionsToRepeat()
		{
		return(hasTransactionsToUndo() && (!hasTransactionsToRedo())
			&& (getNextTransactionToRepeat() != null));
		}

	public		boolean		isExecuting()
		{
		return(executing);
		}

	public		MiNestedTransaction	getOpenTransaction()
		{
		return(openTransactions.empty() ? null : (MiNestedTransaction )openTransactions.peek());
		}

	public		MiiTransaction	getNextTransactionToUndo()
		{
		if (transactionIndex >= 0)
			{
			MiiTransaction transaction = (MiiTransaction )transactions.elementAt(transactionIndex);
			return(transaction.isUndoable() ? transaction : null);
			}
		return(null);
		}
	public		MiiTransaction	getNextTransactionToRedo()
		{
		if (transactionIndex < transactions.size() - 1)
			{
			return((MiiTransaction )transactions.elementAt(transactionIndex + 1));
			}
		return(null);
		}
	public		MiiTransaction	getNextTransactionToRepeat()
		{
		if (transactionIndex >= 0)
			{
			MiiTransaction transaction = (MiiTransaction )transactions.elementAt(transactionIndex);
			return(transaction.isRepeatable() ? transaction : null);
			}
		return(null);
		}
					// i.e. indexOfNextTransactionToUndo
	public		int		getIndexOfLastCompletedTransaction()
		{
		// FIX: this needs to be chanegd if there is a max number of undos/transactions
		return(transactionIndex);
		}
	public		void		processCommand(String cmd)
		{
		if (cmd.equals(Mi_UNDO_COMMAND_NAME))
			{
			undoTransaction();
			}
		else if (cmd.equals(Mi_REDO_COMMAND_NAME))
			{
			redoTransaction();
			}
		else if (cmd.equals(Mi_REPEAT_COMMAND_NAME))
			{
			repeatTransaction();
			}
		}
	protected	void		constrainNumberOfTransactions()
		{
		while (transactions.size() > maxNumberToKeep)
			{
			transactions.removeElementAt(0);
			--transactionIndex;
			}
		}
	public		String		toString()
		{
		String str = "Number of OPEN transactions (nested levels) = " + openTransactions.size() + "\n";
		if (openTransactions.size() > 0)
			{
			str += "        {\n";
			}
		for (int i = openTransactions.size() - 1; i >= 0; --i)
			{
			str += "        " + i + "." + openTransactions.elementAt(i) + "\n";
			}
		if (openTransactions.size() > 0)
			{
			str += "        }\n";
			}
		str += "Number if UNDOable transactions = " + transactions.size() + "\n";
		for (int i = transactions.size() - 1; i >= 0; --i)
			{
			str += "        " + i + "." + transactions.elementAt(i) + "\n";
			}
		return(str);
		}
	}

