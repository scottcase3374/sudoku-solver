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
 * Talk points:
 * 1) I generally prefer to specify each import instead of using "*"
 * to pickup everything in a package. It is more typing but I find
 * it easier to understand/maintain when working with large projects.
 * Having it explicit helps make it clear just how many dependencies
 * a file has. In projects with many 3rd party dependencies, you are
 * also less likely to run into a name conflict between them.
 */
import java.util.Arrays;
import java.util.function.Consumer;

/**
 *  Represents a view over a 3x3 block in the game
 *  Provides ability to validate whether the data in the
 *  view is in a valid configuration.
 *
 *  This concrete class implements an interface which
 *  provides the validation API.
 */
class BlockView implements ViewIntfc
{
	/**
	 * This is the constructor for the class. The
	 * parameters are used to extract the set of
	 * cells for a specific.
	 *
	 * Notes/Talk points:
	 * 1) Here, "final" is more about providing "code as documentation" of
	 * intent than needing to prevent any real changes to parms.
	 * 2) Remember that parameters are passed by value - for both
	 * primitives and references.
	 * 3) Here we can't change the value of the primitives or
	 * reference itself but you could change the content of the object
	 *  that a reference points to (array content here).
	 * 4) Imagine if I didn't specify that the row/col parms are 0 based indexes - how would you know???
	 *         My point is to document these things - not allow assumptions which could be wrong..
	 *
	 * @param fullSrc The entire 9x9 set of cells.
	 * @param blockRowStart The 0 based start row in fullSrc.
	 * @param blockColStart The 0 based start col in fullSrc
	 */
	BlockView(final Cell [][] fullSrc, final int blockRowStart, final int blockColStart)
	{
		cellBlock = extractCellBlock(fullSrc, blockRowStart, blockColStart);
	}

	// Note use of javadoc link below to reference documentation for the method we override.
	// In many IDE's or if we produce the documentation from the code, it will help
	// future users/maintainers of the code.

	/**
	 * Determine if the cells represented by the view are in a valid state.
	 *
	 * see {@link  ViewIntfc.validate() for high-level details.}.
	 *
	 * Talk point:
	 * '@ Override' annotation lets the compiler know that
	 * this method declaration is from a parent; which in this case is the ViewIntfc interface.
	 *
	 * @return Accept if all view cells meet game rules; Reject if any dupes; Unknown otherwise
	 */
	@Override
	public Status validate()
	{
		/*
		 * represent used values - each index location represents count of
		 * the associated "value" in the view. e.g if all 9 cells had no
		 * current value; there would be a 9 at index 0 since 0 is used as
		 * the "no-value" value.  Of the 9 cells, if only the numbers 1,2,3 existed
		 * uniquely in the cells then values at indexes 1,2,3 would be 1 and the
		 * remaining indexes would have a value of 0.
		 * If only the number 5 existed 3 times in the 9 cells then the value at
		 * index 5 would be 3 withe the remaining index containing 0.
		 */
		final int USED_VALS[] = {0,0,0,0,0,0,0,0,0,0};

		// Func to count cell values 0-9.
		final Consumer<Cell> c = (x) -> USED_VALS[x.peekProposal().getFirst()]++;

		/* Walk the list of cells; track usage of each val from the cells
		 * Updates USED_VALS as indicated above.
		 */
		Arrays.asList(cellBlock).stream().flatMap(cba -> Arrays.asList(cba).stream()).forEach(c);

		/*
		 * Talk point:
		 * regarding "retStatus"; since uninitialized, all code paths must set a val. It
		 * would be better to initialize it to UNKNOWN in this case - then the
		 * final "else" can just go away since it would occur by default with the initial
		 * value if not changed.
		 */
		Status retStatus;
		if (Arrays.stream(USED_VALS, 1,10).allMatch((i) -> i == 1)) // count @ idx 1-9 = 1
		{
			retStatus = Status.ACCEPT;
		}
		else if (Arrays.stream(USED_VALS, 1,10).max().getAsInt() > 1)  // count for any idx 1-9 > 1
		{
			retStatus = Status.REJECT;
		}
		else // count @ idx 0 must be > 1
		{
			retStatus = Status.UNKNOWN;
		}
		return retStatus;
	}

	/**
	 * This is a utility method used during construction to extract
	 * the set of cells representing a particular 3x3 block. The specific
	 * cells extracted are from [blockRowStart, blockColStart] to
	 * [blockRowStart+2, blockColStart+2].
	 *
	 *  Notes/Talk points:
	 *  1) I am not using final on the blockRowStart integer parm since
	 *  I make changes to it in the method.  Those changes are not visible
	 *  outside of the method though since Java uses "pass by value".
	 *
	 * @param fullSrc The entire 9x9 set of cells.
	 * @param blockRowStart The 0 based start row in fullSrc.
	 * @param blockColStart The 0 based start col in fullSrc
	 * @return 2d array (3x3) containing the src Cell objects which represent the block covered by the specified row/col.
	 */
	private Cell[][] extractCellBlock(final Cell[][] fullSrc, int blockRowStart, final int blockColStart)
	{
		/**
		 * Talk points
		 * 1) Note that I am not using "new" here directly when creating the array.
		 *   That is only possible because it is part of a variable declaration.
		 *   If I wanted to perform the array creation in the return statement
		 *   I would have to use "new Cell[][] {..}".  There isn't any real
		 *   difference although by declaring the variable we can see it in
		 *   a debugger here.
		 *  2) Remember that the {} is defining an array; the method calls
		 *  are embedding another array at each of the 3 locations in the
		 *  explicitly initialized array - so the result in an array of arrays
		 *  of Cells (which is what the [][] means).
		 */
		final Cell[][] tmpItems =
		{
			extractRowCells(fullSrc, blockRowStart++, blockColStart),
			extractRowCells(fullSrc, blockRowStart++, blockColStart),
			extractRowCells(fullSrc, blockRowStart, blockColStart)
		};

		return tmpItems;
	}

	/**
	 * construction utility method; this method extracts a group of 3
	 * consecutive cells from a src row in the full src array.
	 *
	 * @param fullSrc The entire 9x9 set of cells.
	 * @param rowStart The 0 based start row in fullSrc.
	 * @param colStart The 0 based start col in fullSrc
	 * @return An array [3] of the extracted cells that represent the partial rows/cols of the block associated with the row/col specified.
	 */
	private Cell [] extractRowCells(final Cell[][] fullSrc, final int rowStart, final int colStart)
	{
		// The number of cells we extract at a time from a row; this could be moved to a class dedicated to
		// constants or made a private static final int class member. Moving to a constants class though creates
		// a small dependency though. Since this value isn't really useful outside of this class, it is
		// probably ok either as is or as a static final member.
		final int BLOCK_WIDTH = 3;

		/**
		 * Talk points
		 * Note that "Arrays" is from our import of java.util.Arrays
		 * The "fullSrc[rowStart]" provides a reference to the contained
		 * array.
		 *
		 *         top-level
		 *          array
		 *            |
		 *           \./       contained array col #'s within row
		 *                   0 1 2 3 4 5 6 7 8
		 *  rowStart  0 ->   C C C C C C C C C
		 * (i.e.row#) 1 ->   C C C C C C C C C
		 *            2 ->   C C C C C C C C C
		 *            3 ->   C C C C C C C C C
		 *            4 ->   C C C C C C C C C
		 *            5 ->   C C C C C C C C C
		 *            6 ->   C C C C C C C C C
		 *            7 ->   C C C C C C C C C
		 *            8 ->   C C C C C C C C C
		 *
		 *  Also take note that the "to", meaning colStart+blockWidth, is not inclusive.
		 *  So a colStart of 0 with a resulting to of "0+3" means that
		 *  cols 0, 1, 2 will be copied and not 0,1,2,3.. Make sure you
		 *  read the documentation which makes this clear.
		 */
		return Arrays.copyOfRange(fullSrc[rowStart], colStart, colStart + BLOCK_WIDTH);
	}

	/**
	 * This method is an optimization; by tracking the remaining valid values for each cell
	 * we can quickly eliminate invalid paths.
	 *
	 * This method is only called when a valid value is found and assigned within the block.
	 * At that point, we walk all the associated cells in this block and remove the specified
	 * val as a value for the remaining cells in the block.
	 *
	 * @param val The value to remove; valid vals 1-9
	 *
	 */
	@Override
	public void updateUsedValues(final int val)
	{
		for (int r = 0; r < cellBlock.length; r++)
		{
			for (int c = 0; c < cellBlock[r].length; c++)
			{
				cellBlock[r][c].removeUsedVal(val);
			}
		}
	}

	/**
	 * This is the array of references to the underlying
	 * data.  Each Cell object originates in the Game
	 * class and we are only maintaining a copy of the
	 * reference here in the local array.
	 */
	private Cell [][] cellBlock;
}
