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
 * Basic enumeration to represent status of various objects or operations/tests (ACCEPT, REJECT, UNKNOWN).
 *
 */
enum Status
{
	/**
	 * Represent a value whis is fixed (initial game value) or one that must be
	 * correct as determined by tests which always maintain correctness (no guesses).
	 */
	ACCEPT,

	/**
	 * A value/test which results in an invalid game configuration.
	 */
	REJECT,

	/**
	 * Used to identify initial blank game locations and guesses when used.
	 */
	UNKNOWN;
}
