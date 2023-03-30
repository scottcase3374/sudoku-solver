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

import java.io.IOException;

/**
 * This interface represents the API to load a game from some data source (file, database, etc).
 *
 * Talk points:
 * The way this is defined, it is mainly intended to load *1* game.
 * In other words, it isn't directly intended to support loading
 * and running something like 1000 games in a row.
 *
 * With this general interface, running multiple games would best be accomplished
 * by adding a path parameter in to "load" below and probably adding a loop to
 * "main" that either takes command line parms and passes them or maybe reads
 * input file or directory info from a configuration file (or cmd line) which
 * is passed to this interface.
 */
interface LoaderIntfc
{
	/**
	 * Method responsible for loading data into a game instance.
	 *
	 * @param data The full 2D array of games cells
	 * @param coordinator A coordinator instance.
	 *
	 * @return Always returns true for now; left room for some sort of non-exception error handling
	 *
	 * @throws IOException
	 */
	boolean load(final Cell data[][], final Coordinator coordinator);
}
