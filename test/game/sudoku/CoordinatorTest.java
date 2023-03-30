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

package game.sudoku;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.starcases.sudoku.BlockView;
import com.starcases.sudoku.Cell;
import com.starcases.sudoku.Coordinator;
import com.starcases.sudoku.Status;

/**
 * Note that you should be liberal in documenting test cases; if you can't understand later what you
 * were testing then it isn't a very useful test.
 *
 * I've not written a test method for every possible test case/method but did enough to provide some understanding of how it should work.
 *
 * @author scase
 *
 */
public class CoordinatorTest
{

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		// this would be called once (before any tests).  We don't have any need here but I left it
		// for informational purposes.
	}

	/*
	 * This does some setup before EACH test method.
	 */
	@Before
	public void setUp() throws Exception
	{
		coordinator = new Coordinator();
	}

	@Test
	public void testSetupBlockViews()
	{
		// Talk point:
		// From a practical perspective, it sometimes makes sense to combine some testing.  If you do, make sure you document it well.
		// Note that I have the actual test method here but unimplemented.  This means that anyone looking for the test can easily find
		// it and see the note about how the testing is combined with other stuff.  That prevents someone from thinking it was
		// just accidentally left out.


		// Not testing this directly; using testRowColToBlock() to test indirectly.
	}

	@Test
	public void testSetupRowViews()
	{
		// Test that a particular row in the source data with a unique set of values
		// ends up with a row view for the correct row which is verified by
		// checking the row values which only exist in that row.
		final Cell [][] rowcells = getRow4Val6();
		coordinator.setupRowViews(rowcells);
		for ( Cell cell : coordinator.rowViews[4].items)
		{
								// expected val,   actual val
			Assert.assertEquals(new Integer(6), cell.content.getFirst());
		}
	}

	@Test
	public void testSetupColViews()
	{
		// Test that a particular column in the source data with a unique set of values
		// ends up with a col view for the correct column which is verified by
		// checking the col values which only exist in that column.
		final Cell [][] colcells = geCol3Val4();
		coordinator.setupColViews(colcells);
		for ( Cell cell : coordinator.colViews[3].items)
		{
			Assert.assertEquals(new Integer(4), cell.content.getFirst());
		}
	}

	@Test
	public void testRowColToBlock()
	{
		/*
		 * Talk point:
		 * This isn't currently a really good test.  Why?  Because it is going
		 * through a a few other untested methods so we risk a false
		 * positive.  To make this a reasonable test we should try to show
		 * that data in the non-success blocks does in fact not return success.
		 * If you do that for all the other 8 blocks then you have a higher level
		 * of confidence that this method and the other methods are correct.
		 *
		 * On a failure, it would simply mean that something between here and
		 * other successfully tested code probably has an issue (highest likelihood).
		 * It could be that other successfully tested code had a false positive/success but
		 * if you have tests that generally cover most situations then it is less likely.
		 */
		final Cell cells[][] = getBlockRow3Col3ValValid();
		coordinator.setupBlockViews(cells);

		final BlockView bv = coordinator.rowColToBlock(3, 3);
		final Status status = bv.validate();
		Assert.assertEquals(Status.ACCEPT, status);
		//fail("Not yet implemented");
	}

	@Test
	public void testFiltStatus()
	{
		/**
		* Talk point: the main improvement required here would be some more variation in the order of test values to make sure that order didn't cause a wrong return. Also document a bit better.
		 */
		Assert.assertEquals("3 accepts should result in accept", Status.ACCEPT, coordinator.filtStatus(Status.ACCEPT, Status.ACCEPT, Status.ACCEPT));

		Assert.assertEquals("unknowns without reject should result in unknown", Status.UNKNOWN, coordinator.filtStatus(Status.UNKNOWN, Status.UNKNOWN, Status.UNKNOWN));
		Assert.assertEquals("unknowns without reject should result in unknown", Status.UNKNOWN, coordinator.filtStatus(Status.UNKNOWN, Status.UNKNOWN, Status.ACCEPT));

		Assert.assertEquals("Any reject should result in reject", Status.REJECT, coordinator.filtStatus(Status.REJECT, Status.REJECT, Status.REJECT));
		Assert.assertEquals("Any reject should result in reject", Status.REJECT, coordinator.filtStatus(Status.REJECT, Status.ACCEPT, Status.ACCEPT));
		Assert.assertEquals("Any reject should result in reject", Status.REJECT, coordinator.filtStatus(Status.UNKNOWN, Status.REJECT, Status.ACCEPT));
	}

	/**
	 * Talk point:
	 * Note the method naming convention below.  I try to name the methods in a way that I know what the data configuration is.  Method documentation helps as well..
	 */


	/**
	 * Test data utility - fill col 3 with the value of 4, everything else is default
	 * @return
	 */
	Cell[][] geCol3Val4()
	{
		final Cell testCells[][] = new Cell[9][9];
		for (Cell [] row : testCells)
		{
			for (int colIdx=0; colIdx < row.length; colIdx++)
			{
				if (colIdx == 3)
				{
					row[colIdx] = new Cell(coordinator);
					row[colIdx].setAcceptedVal(4);
				}
				else
				{
					row[colIdx] = new Cell(coordinator);
					// leave content as default 0
				}
			}
		}
		return testCells;
	}

	/**
	 * Test data utility - fill row 4 with a value of 6, everything else is default
	 * @return
	 */
	Cell[][] getRow4Val6()
	{
		final Cell testCells[][] = new Cell[9][9];
		for (int rowIdx=0; rowIdx < testCells.length; rowIdx++)
		{
			for (int colIdx=0; colIdx < testCells[rowIdx].length; colIdx++)
			{
				if (rowIdx == 4)
				{
					testCells[rowIdx][colIdx] = new Cell(coordinator);
					testCells[rowIdx][colIdx].setAcceptedVal(6);
				}
				else
				{
					testCells[rowIdx][colIdx] = new Cell(coordinator);
					// leave content as default 0
				}
			}
		}
		return testCells;
	}

	/**
	 * Test data utility - fill the block for row 3 / col 3 (center block) with 1-9 so it is "valid" i.e. tests as  Accepted.
	 * All other cells have default val.
	 * @return
	 */
	Cell[][] getBlockRow3Col3ValValid()
	{
		int val = 1;
		Cell testCells[][] = new Cell[9][9];
		for (int rowIdx=0; rowIdx < testCells.length; rowIdx++)
		{
			for (int colIdx=0; colIdx < testCells[rowIdx].length; colIdx++)
			{
				if (rowIdx >= 3 && rowIdx <= 5 && colIdx >= 3 && colIdx <= 5)
				{
					testCells[rowIdx][colIdx] = new Cell(coordinator);
					testCells[rowIdx][colIdx].setAcceptedVal(val++);
				}
				else
				{
					testCells[rowIdx][colIdx] = new Cell(coordinator);
					// leave content as default 0
				}
			}
		}
		return testCells;
	}

	/*
	 * This is part of test data; the Junit "setup" method sets this to a new instance prior to calling each test method.
	 */
	Coordinator coordinator;
}
