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
 * Class that allows keeping both the cell integer value and
 * status of the cell/value together via generics. This is an artifact
 * of an early implementation.
 *
 * Talk points:
 * This is an implementation detail. Note what classes need to know about this
 * class?  Is the way this class is used "good" or should it be better
 * hidden from classes that don't really need to know about it?
 *
 * Hint:  Users of "Cell" probably shouldn't know about this class.  Instead of having
 * any methods returning a Tuple2<>, the methods should only return the individual
 * attributes.
 *
 * Note that this class uses "generics" - see the <X,Y> as part of the initial
 * declaration.  Users of this class specify real types instead of X & Y when
 * they create a Tuple2 instance.
 *
 * @param <X> One of the type parameters.
 * @param <Y> Another type parameter.
 */
class Tuple2<X,Y>
{
	/**
	 * constructor
	 *
	 * @param first
	 * @param second
	 */
	public Tuple2(final X first, final Y second)
	{
		this.first = first;
		this.second = second;
	}

	// return whatever is in the "first" member.
	public X getFirst()
	{
		return first;
	}

	// Set a value to the "first" member
	public void setFirst(final X first)
	{
		this.first = first;
	}

	// get the value of the "second" member.
	public Y getSecond()
	{
		return second;
	}

	// set the value of the "second" member
	public void setSecond(final Y second)
	{
		this.second = second;
	}

	private X first;
	private Y second;
}
