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
 * This Interface is for interacting with the logic which solves the puzzle.  There may be
 * multiple classes implementing this interface and the idea is to allow multiple different
 * implementation classes to be used within the process to solve a single puzzle.
 *
 * @author scase
 *
 */
interface StrategyIntfc
{
	/**
	 * This is the entry point for any "solution" logic in an implementor.
	 *
	 * Talk points:
	 * This isn't a big interface by any means which is good.  There are other overall designs which wouldn't
	 * let the strategy know the games fundamental implementation is arrays though. In larger programs
	 * that would be more important - hiding implementation details between classes is a best practice.
	 *
	 * @param fullSrc This is the complete source cell game array.
	 * @param coordinator A coordinator object which is responsible mainly for managing views over the data and similar stuff.
	 * @return True if the process didn't fail utterly (hopefully successful); false if something went way wrong.
	 *
	 * @throws Exception General exception if we identify a condition that can't be handled.
	 */
	boolean process(final Cell fullSrc[][], final Coordinator coordinator) throws Exception;
}
