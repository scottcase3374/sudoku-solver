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
 * A view is a subset of game cells meeting a specific game rule criteria (col/row/block).
 */
interface ViewIntfc
{
	/**
	 * Talk point:
	 * It is real easy to make something that is ambiguous.  Without reading other comments
	 * take a guess at what this method might be doing..
	 *
	 * Is indicating that values exist in *all* cells in the view?
	 * Is it indicating that there are no duplicate values in the cells?
	 * Is it indicating that all cells have appropriate value with no duplicates?
	 * What else could it mean?
	 * Some combination?
	 *
	 *   --- Try to make documentation clear and unambiguous  ---
	 * In this case; the intent is to determine if there is a violation of the game rule. This means
	 * it determines if there are duplicates (return status Reject), if all cells have set
	 * values and no duplicates (return Accept) or if there are no duplicates and some 0+ subset
	 * with values then return (Unknown).
	 *
	 *  --- Note that you should make sure that implementations of this method also
	 *  clearly document themselves - that can be done by pointing users here instead of
	 *  duplicating documentation - maybe use javadoc links to reference this. By just referencing
	 *  existing documentation you don't have to worry about maintaining it in multiple places
	 *  if there is a need for change. Documentation is important and incorrect documentation
	 *  is sometimes worse than no documentation.
	 *
	 * @return Status indicating the state of the view.
	 */
	Status validate();

	/**
	 * This method is an optimization; by tracking the remaining valid values for each cell
	 * we can quickly eliminate invalid paths.
	 *
	 * This method is only called when a valid value is found and assigned within the view.
	 * At that point, we walk all the associated cells in this view and remove the specified
	 * val as a value for the remaining cells in the view.
	 *
	 * Talk point:
	 * Note that "final val" isn't affecting anything but is a "documentation" clue
	 * that I don't want/intend to write to ("mutate") it.
	 *
	 * @param val The value (1-9) to remove
	 *
	 */
	void updateUsedValues(final int val);
}
