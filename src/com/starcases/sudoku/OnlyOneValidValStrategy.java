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
 * This is a strategy that is sort of an optimization.  It looks for cells
 * that only have 1 possible value based upon the state just after initial load.
 *
 * If it finds cells like that; they are set to their required value just like
 * data read from the input source.. ie. it can't be reset/changed.
 */
class OnlyOneValidValStrategy implements StrategyIntfc
{
	/**
	 * The entry point to the strategy logic.
	 *
	 * @param fullSrc The full game src data.
	 * @param coordinator The view coordinator instance.
	 *
	 * @return true if we updated any cells value; false otherwise.
	 */
	@Override
	public boolean process(final Cell fullSrc[][], final Coordinator coordinator)
	{
		boolean retVal = false;
		for (Cell row[] : fullSrc)
		{
			for (Cell col : row)
			{
				if (col.getValidVals().size() == 1)  // Only 1 possible value remaining
				{
					col.setAcceptedVal(col.getValidVals().iterator().next());
					col.getValidVals().clear();  // clear the valid values data once we used that value.
					retVal = true;
				}
			}
		}
		return retVal;
	}
}
