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
import java.util.HashSet;
import java.util.Set;

/**
 * This class represents the valid or proposed data at a
 * particular location in the game.
 *
 */
class Cell
{
	/**
	 * Constructor;
	 *
	 * @param coordinator Responsible for operations across the views.
	 */
	Cell(final Coordinator coordinator)
	{
		this.coordinator = coordinator;
	}

	/**
	 * Set a value that will not change.
	 *
	 * @param val The value (1-9) that is part of the current input or was determined via tests that maintain correctness.
	 */
	public void setAcceptedVal(final int val)
	{
		content = new Tuple2<Integer,Status>(val, Status.ACCEPT);
	}

	/**
	 * Set a value which can be removed or changed.
	 * Don't replace any "accepted" value.
	 *
	 * Talk point: Instead of just ignoring any attempt to replace an
	 * "accepted" value; we might throw an exception.  This is dependent
	 * upon usage - if we have no good way to determine "accepted" values
	 * before setting the value or don't want to check then this current code is likely ok.
	 * You must be careful to understand that this could mean you miss
	 * a potential fundamental error in logic which you might not easily
	 * see since no error is thrown though.
	 *
	 * @param val A "guessed" value of 1-9.
	 */
	public void setProposedVal(final int val)
	{
		if (Status.ACCEPT != content.getSecond())
		{
			content = new Tuple2<Integer,Status>(val, Status.UNKNOWN);
		}
	}

	/**
	 * Determine if the cur cell is set to the default (unset) value.
	 *
	 * @return true if the value is the default val (0)
	 */
	public boolean isUnset()
	{
		return DEFAULT_VAL == content.getFirst();
	}

	/**
	 * Backs out a cell value; used when a cell itself or a dependent cell
	 * is determined to have an invalid value.  Doesn't affect already
	 * Accepted values. The result of this is a default cell value with an "unknown" status.
	 */
	public void reject()
	{
		if (Status.ACCEPT != getStatus())
			content = new Tuple2<>(DEFAULT_VAL, Status.UNKNOWN);
	}

	/**
	 * Get the status inherent in the current cell value
	 *
	 * @return Accepted for initial loaded values; Unknown for guesses; Reject is not assigned in this context.
	 */
	public Status getStatus()
	{
		return content.getSecond();
	}

	/**
	 * Return the internal value of the cell.
	 *
	 * Talk points:
	 *  Note that the method naming convention here reflects an early implementation
	 * thought where I was going to use a stack. It might be better to not bleed information out even in naming
	 * conventions so someone doesn't make an assumption (which may or may not cause a problem).  The bigger
	 * issue here is the blatant return of an internal structure from a public API call.  Best practice is
	 * to hide internal details. It we were returning interfaces instead of concrete classes it would be better.
	 * I think that using specific getter methods for the individual values would be best though. There is already
	 * a 'getStatus' which returns the Status returned here so the only method missing is a getValue() or similar.
	 * If that was implemented then this method could be removed.
	 *
	 * @return Returns the reference to internally stored reference.
	 */
	public Tuple2<Integer, Status> peekProposal()
	{
		return content;
	}

	/**
	 * Remove a value from a cells set of potential valid values.
	 *
	 * @param val Value (1-9) to remove from collection representing potential valid values.
	 */
	public void removeUsedVal(final int val)
	{
		validVals.remove(val);
	}

	/**
	 * Return the set of potential valid values for this cell. Y
	 *
	 * Talk points:
	 * Yes, our declaration says "Set" as well which at first thought seems like we are violating the
	 * "don't return implementation details" type rule but if you look closely you will find that Set
	 * is an interface and doesn't really share the concrete type.  Could we share even less info? Maybe..
	 * If some cases, it might be possible to pass back an abstract base class like "AbstractCollection"
	 * or one of the higher level interfaces like "Collection". You just need to understand how the
	 * data will be used to determine what is best. If you are writing something generic where you don't
	 * know what future clients of the code will do or how they will use it then you are probably better
	 * off returning one of the more specific *interfaces* - not the concrete type though.
	 *
	 * @return set of potentially valid values.
	 */
	public Set<Integer> getValidVals()
	{
		return validVals;
	}

	/*
	 * The value of cells with no current guess or loaded value - i.e default value.
	 */
	final static Integer DEFAULT_VAL = 0;

	/*
	 * Cell content; The initial cell state - 0 as the value and "unknown" as status.
	 */
	Tuple2<Integer, Status> content = new Tuple2<>(DEFAULT_VAL, Status.UNKNOWN);


	final static Integer [] data = {1,2,3,4,5,6,7,8,9};
	final Set<Integer> validVals = new HashSet<Integer>(Arrays.asList(data));

	// The coordinator reference - manage some cross view interactions and such.
	Coordinator coordinator;
}
