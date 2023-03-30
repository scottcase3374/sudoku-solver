/* The MIT License (MIT)

Copyright (c) 2016 Scott Case

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package com.starcases.sudoku;

import java.util.Iterator;

/**
 * This is the core strategy for solving the puzzle - it does the bulk of the work.
 *
 * The general strategy is to:
 *  1) start at the row 0 / col 0 (treat as upper left corner); increased values move left to right/top to bottom in a typewriter style.
 *  2) Call a process method for the current cell;
 *      a) if row # is 9 (i.e. past last real row 0-8) then return Accept
 *      b) if cell has default val; set to first possible val
 *      c) If cell is already accepted then call process on next cell
 *
 *
 *  So this is a recursive method and uses back-tracking.  There are 2 minor optimizations which reduce the "brute force" aspect - fill in
 *  statically determinable locations and setup an initial set of valid remaining values for each cell.
 *
 * @author scase
 *
 */
class SerialStrategy implements StrategyIntfc
{
	/**
	 * This is the main entry point from the StrategyIntfc
	 *
	 * @param fullSrc The full game dataset
	 * @param coordinator View coordinator instance
	 *
	 * @return false if final status is Reject; true otherwise
	 */
	@Override
	public boolean process(final Cell[][] fullSrc, final Coordinator coordinator) throws Exception
	{
		return process(fullSrc, coordinator, 0, -1) != Status.REJECT;
	}

	/**
	 * This is a recursive method which implements the strategy logic.  The class description describes the general logic.
	 *
	 * @param fullSrc The full game dataset.
	 * @param coordinator The coordinator instance.
	 * @param row Row (1-9) being processed.
	 * @param col Col (1-9) being processed.
	 *
	 * @return Accept if recursed call returns Accept; reject if no valid cell value found for views; unknown otherwise
	 * @throws Exception
	 */
	private Status process(final Cell[][] fullSrc, final Coordinator coordinator, int row, int col) throws Exception
	{
		//  *******************
		//  Row/col # handling
		// ********************
		// prep cur row/col info
		int tmpCol = col;
		col = tmpCol + 1;
		col %= 9;

		if (col < tmpCol)
		{
			row++;
		}

		//*******************************
		// Terminate deeper traversal and start unwinding the recursive calls
		//*******************************
		if (row == 9)
			return Status.ACCEPT;

		// *************************************
		// Traversal prep - this data is manipulated on non-Accept cells/returns
		// *************************************
		// Get current row/col data - prepare before depth first traversal
		final Cell cell = fullSrc[row][col];
		final Iterator<Integer> it = cell.getValidVals().iterator();
		Status curStatus = cell.getStatus();

		// ***********************************
		// CUR CELL DATA SETUP (if needed)
		// ***********************************
		// If we don't have a current value in the cell then we should set one from the currently known potential values.
		if (Status.UNKNOWN == curStatus)
		{
			if (cell.isUnset())
			{
				if (it.hasNext())
				{
					final int tmpVal = it.next();
					cell.setProposedVal(tmpVal);
					curStatus = coordinator.test(row, col);
				}
				else
					throw new Exception("No viable value; cell["+ row+","+ col + "]");
			}
		}

		try // The "try" of it itself is part of the back-tracking logic. No matter how we leave this logic; if the result was reject then we should
				// undo the current cells value and set it back to the default value.
		{
			do // re-enter loop only if the current value of the cell isn't valid; goal of loop is to try remaining potential valid values
			{
				if (Status.ACCEPT == curStatus)
				{
					/*
					 * Value was set at game load time or our recent view update puts us in a valid state.
					 *
					 * If cur cell is accepted; skip to next cell
					 * No processing to do after this; simply return next status
					 * to caller. Caller responsible for determining
					 * what is needed.
					 */
					curStatus = process(fullSrc, coordinator, row, col);

					// A status of Accept at this point means we are done.
				}
				else if (Status.UNKNOWN == curStatus)
				{
					// Means we don't have enough data points to determine anything yet so
					// we must continue processing more cells to fill in values that are
					// then truly testable for Accept/Reject once entire views have data.
					curStatus = process(fullSrc, coordinator, row, col);

					// A reject returned means that a downstream cell failed - we don't need to
					// test then. if downstream failed then either we must try our next
					// valid value or return to our caller (reject)
					if (Status.REJECT != curStatus)
					{
						// If we are not rejected then test the set of views covering this cell.
						curStatus = coordinator.test(row, col);
					}
				}
				else if (Status.REJECT == curStatus)
				{
					// current cell has tested out and been rejected.
					// If we have more potentially valid vals then try them.
					if (it.hasNext())
					{
						/*
						 * If we are not correct for current row and there are more
						 * values to test then test them.
						 */
						final int tmpVal = it.next();
						cell.setProposedVal(tmpVal);
						curStatus = coordinator.test(row, col);
					}
					else
					{
						// we are rejected and have not valid values to pass the reject back..
						// caller with try their next values or back-track themselves.
						return curStatus;
					}
				}

				// Not accept means allow us to try any remaining potentially valid val (and start recursion again)
				// otherwise we start to unwind our calls because we are accepted and solution was found.
			}
			while (Status.ACCEPT != curStatus);
		}
		finally
		{
			if (Status.REJECT == curStatus) // reset our state to default/unknown so next time we recurse into this cell we acquire the next possible valid value
				cell.reject();
		}
		return curStatus;
	}

	/*
	 * The below commented out method was used for some simple debugging and removed. In real applications, logging may be a very important and very integral
	 * component of things.  If you have a very complex and/or large application it may be VERY hard to determine what happens when something goes
	 * wrong (note WHEN and not IF something goes wrong).  There are a number of logging libraries/frameworks out there - log4j, slf4j and others;
	 * evaluate them, pick one that works for what you need and use it.  If you are writing something that isn't a toy then use a logging framework.
	 * There are best practices and such for how to use them.. do some research.
	 */
	//void log(String cmt, int row, int col, int val, Status status)
	//{
	//	System.out.println(String.format("%s [%d,%d]=%d status: %s", cmt, row, col, val, status));
	//}
}
