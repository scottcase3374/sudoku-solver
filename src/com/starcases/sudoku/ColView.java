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

import java.util.Arrays;
import java.util.function.Consumer;

/**
 *  Represents a view over a 9-cell column in the game.
 *  Provides ability to validate whether the data in the
 *  view is in a valid configuration.
 *
 *  This concrete class implements an interface which
 *  provides the validation API.
 */
class ColView implements ViewIntfc
{
	/**
	 * Constructor; walk the source data for the specified
	 * column and generate a structure that will reference
	 * those cell instances.
	 *
	 * @param fullSrc The full game dataset
	 * @param col The column (1-9) we want the subset of from the full dataset.
	 */
	ColView(final Cell fullSrc[][], final int col)
	{
		for (int i=0; i < fullSrc.length; i++)
		{
			items[i] = fullSrc[i][col];
		}
	}

	/**
	 * Determine if the cells represented by the view are in a valid state.
	 *
	 * see {@link  ViewIntfc.validate() for high-level details.}.
	 *
	 * @return Accept if all view cells meet game rules; Reject if any dupes; Unknown otherwise
	*/
	@Override
	public Status validate()
	{
		/*
		 * Talk point:
		 * I really hate duplicating code (and documentation) and the code here is *close* to the implementations
		 * in BlockView and RowView.  It might be nice to find a way to share
		 * the bulk of the implementation - either using a superclass or maybe conveying
		 * some implementation details in through either the constructor or some setter. It
		 * might also require us to extract some of this into one or more separate methods.
		 */

		/*
		 * represent used values - each index location represents count of
		 * the associated "value" in the view. e.g if all 9 cells had no
		 * current value; there would be a 9 at index 0 since 0 is used as
		 * the "no-value" value.  Of the 9 cells, if only the numbers 1,2,3 existed
		 * uniquely in the cells then values at indexes 1,2,3 would be 1 and the
		 * remaining indexes would have a value of 0.
		 * If only the number 5 existed 3 times in the 9 cells then the value at
		 * index 5 would be 3 with the remaining index containing 0.
		 */
		final int USED_VALS[] = {0,0,0,0,0,0,0,0,0,0};

		// Func to count cell values 0-9.
		final Consumer<Cell> c = (x) -> USED_VALS[x.peekProposal().getFirst()]++;

		// Walk the list of cells; track usage of each val from the cells
		Arrays.asList(items).stream().forEach(c);

		Status retStatus;
		if (Arrays.stream(USED_VALS, 1,10).allMatch((i) -> i == 1)) // find whether ALL idx 1-9 have distinct val (no dupe, all vals used)
		{
			retStatus = Status.ACCEPT;
		}
		else if (Arrays.stream(USED_VALS, 1,10).max().getAsInt() > 1)  // determine if any dupes @ idx 1-9
		{
			retStatus = Status.REJECT;
		}
		else // count @ idx 0 must be > 1 which means there are "unknown" values
		{
			retStatus = Status.UNKNOWN;
		}
		return retStatus;
	}

	/**
	 * Remove used values.  See {@link ViewIntfc.updateUsedValues(int) }
	 *
	 * Talk point:
	 * Once again; a superclass could implement this to prevent duplication.
	 *
	 * @param val A value (1-9) we want to maintain knowledge that we have now used/consumed.
	 */
	@Override
	public void updateUsedValues(final int val)
	{
		for (int idx=0; idx<items.length; idx++)
		{
			items[idx].removeUsedVal(val);
		}
	}

	// Data structure representing view data. We never replace the array; we only update the contents.
	final Cell items[] = new Cell[9];
}
