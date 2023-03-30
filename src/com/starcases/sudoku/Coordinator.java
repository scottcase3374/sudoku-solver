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

/**
 *
 * Talk points
 * Most of this originated in "Game.java" and was refactored (moved) to here.
 * A best practice is to separate different responsibilities into different
 * classes (and methods as well).
 *
 */
class Coordinator
{
	/**
	 * Constructor.
	 *
	 * Talk points:
	 * As is, it's very important that the initialization isn't done here
	 * because the src cells would not yet be initialized. There must be
	 * coordination between the creation and initialization processes
	 * within the Game instance and this so the cells are available.
	 *
	 * Also note that Game is coded to create an instance of this directly.
	 * Note that means we don't have the ability as is to replace the
	 * implementation with a different one.  A more flexible solution
	 * would be to use an Coordinator interface and create/pass it into
	 * the Game instance at an appropriate point (maybe the Game constructor).
	 * By doing that, we could try out different implementations of the
	 * coordination logic.  Whether that would be useful would take some
	 * analysis to see if the existing code had too many cross dependencies
	 * which would prevent trying out any useful different implementations.
	 *
	 */
	public Coordinator()
	{}

	/**
	 * Initialization performed after the construction of the object since we need the
	 * src data arrays initialized prior to initializing the views themselves.
	 *
	 * @param data Represents the full array of game values.
	 */
	public void init(final Cell[][] data)
	{
		setupBlockViews(data);
		setupRowViews(data);
		setupColViews(data);
	}

	/**
	 * Utility method for setting up views over 3x3 blocks of cells.
	 *
	 * Talk points
	 * 1) Note the trailing comma (,) after the last entry in the
	 * initialization of the array; it is legal to have it. The value of
	 * it here is minimal but if you regularly have to maintain an array
	 * by adding new values it is a tiny nicety that sort of "pays it forward".
	 * 2) Notice that I pass "data" and starting points into the Block
	 * constructors.  I could (and originally did) have the logic to extract
	 * the 3x3 cell array here and pass it directly to the Block but
	 * "refactored" the code by moving the extraction logic to the Block class.
	 * That seems like a better design - the 3x3 array itself resulted in
	 * Block implementation details bleeding into this class.  It is better
	 * that internal implementation details of Block stay as invisible to
	 * other classes as possible.  This is a fundamental programming principle.
	 * 3) This method has package visibility only because of the test code access,
	 * normally this would be private.
	 *
	 * @param data Represents the full array of game data.
	 */
	void setupBlockViews(final Cell[][] data)
	{
		blockViews = new BlockView[][]
		{
			{new BlockView(data,0,0), new BlockView(data,0,3), new BlockView(data,0,6)},
			{new BlockView(data,3,0), new BlockView(data,3,3), new BlockView(data,3,6)},
			{new BlockView(data,6,0), new BlockView(data,6,3), new BlockView(data,6,6)},
		};
	}

	/**
	 * Utility method for setting up views representing a row of data.
	 *
	 * Talk point:
	 * This method has package visibility only because of the test code access,
	 * normally this would be private.
	 *
	 * @param Represents the full array of game data.
	 */
	void setupRowViews(final Cell[][] data)
	{
		rowViews = new RowView[]
		{
			new RowView(data, 0),
			new RowView(data, 1),
			new RowView(data, 2),
			new RowView(data, 3),
			new RowView(data, 4),
			new RowView(data, 5),
			new RowView(data, 6),
			new RowView(data, 7),
			new RowView(data, 8)
		};
	}

	/**
	 * Utility method for setting up views representing a col of data.
	 *
	 * Talk point: This method has package visibility only because of the test code access,
	 * normally this would be private.
	 *
	 * @param Represents the full array of game data.
	 */
	void setupColViews(final Cell[][] data)
	{
		colViews = new ColView[]
		{
			new ColView(data, 0),
			new ColView(data, 1),
			new ColView(data, 2),
			new ColView(data, 3),
			new ColView(data, 4),
			new ColView(data, 5),
			new ColView(data, 6),
			new ColView(data, 7),
			new ColView(data, 8)
		};
	}

	/**
	 * Method used to remove used value from the cells in the appropriate row/col/block. This
	 * is an optimization to allow reducing the number of invalid paths which are tried.
	 *
	 * Row/Col combo determines block to remove value from.
	 *
	 * @param val Value (1-9) to remove if found
	 * @param row Row (one of 1-9) [all cells] to remove value from
	 * @param col Col (one of 1-9) [all cells] to remove value from
	 */
	void removedUsedVal(final int val, final int row, final int col)
	{
		colViews[col].updateUsedValues(val);
		rowViews[row].updateUsedValues(val);
		rowColToBlock(row, col).updateUsedValues(val);
	}

	/**
	 * Determine if a particular row/col/block combination is valid.
	 *
	 *  row/col combo determines block to check.
	 *
	 * @param row Row (1-9) to check
	 * @param col Col (1-9) to check
	 * @return ACCEPT if game rules met; UNKNOWN if there are missing vals; REJECT if any dupe vals
	 */
	Status test(final int row, final int col)
	{
		return filtStatus(colViews[col].validate(),
				rowViews[row].validate(),
				rowColToBlock(row, col).validate());
	}

	/**
	 * Map a row/col to the specific block view for it.
	 *
	 * @param row Some row (1-9) of interest
	 * @param col Some col (1-9) of interest
	 * @return BlockView reference for row/col
	 */
	BlockView rowColToBlock(final int row, final int col)
	{
		final int tmpRow = row / 3;
		final int tmpCol = col / 3;
		return blockViews[tmpRow][tmpCol];
	}

	/**
	 * Reduce the statuses from related row/col/block down to a single
	 * overall status.
	 *
	 * @param stats A vararg parameter; this is an array which the Java runtime creates
	 *
	 * @return Reject if any rejects passed; Accept if all parms are Accept; Unknown otherwise.
	 */
	Status filtStatus(final Status ... stats)
	{
		Status retStatus = Status.ACCEPT;

		for (Status stat : stats)
		{
			if (Status.REJECT == stat)
			{
				//  no need to check further.
				retStatus = Status.REJECT;
				break;
			}
			else if (Status.UNKNOWN == stat)
			{
				// If unknown then we should continue
				// to make sure it isn't a reject
				retStatus = Status.UNKNOWN;
			}
			// Fall through (default if not changes) is ACCEPT .
		}
		return retStatus;
	}

	/**
	 * The views over the data.
	 *
	 * Talk point:
	 * Even though BlockView is using a "natural" mapping by using a 2D array, if we changed
	 * it to a 1D array then there would be higher commonality in various methods and it
	 * would provide a higher chance of reducing code duplication (probably via superclass).
	 * The downside is a slightly more complex mapping from the original cell data to block.
	 * It would take a small amount of analysis to determine if the change is warranted.
	 */
	BlockView blockViews[][];
	ColView [] colViews;
	RowView [] rowViews;
}
