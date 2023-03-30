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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * This is a simple implementation that reads game data from a file and
 * stuffs it into a game instance. As part of that load, it also has
 * the responsibility to manage an optimization (tracking used/available
 * vals in cells).
 *
 * Talk points:
 *  Handling the used val tracking here is the type of decision you may find
 *  made in this type of logic but I do think it would be cleaner to move that
 *  to a separate location; maybe a "Strategy" of its own or if all potential
 *  strategies would still need it then maybe part of the Game implementation
 *  itself.  My point being that load logic probably shouldn't know about
 *  this optimization - the optimization would need implementation again
 *  if a different loader were implemented.
 *
 *  We pass in the raw array of data cells that the loader updates. This causes
 *  the loader api to be tightly coupled to storage as an actual 2D array. It
 *  might be better to have the loader access the data through the Game class
 *  by using row/col/val parameters.  That would cause the loader to depend on
 *  the Game API but not a specific data structure; the API dependency is
 *  maybe a better choice for maintainability/flexibility.
 */
class FileLoader implements LoaderIntfc
{
	/**
	 * Constructor.
	 *
	 * Talk points:
	 *  Note that constructors are not defined in an "interface", only in a "class".
	 *  Regarding have "Path" as a constructor arg.  It is "ok" but may be better if it
	 *  was passed to the load(...) method with the other info (and with no instance var).
	 *  A side benefit of that is that a single instance of this class (which would have
	 *  no instance data) could be used across multiple Game loads concurrently if desired
	 *  or across multiple Game loads serially without a need/method to reinitialize
	 *  the path info.
	 *
	 * @param filePath Path object to a data file for a game instance.
	 */
	public FileLoader(final Path filePath)
	{
		this.filePath = filePath;
	}

	/**
	 * Method responsible for loading data. See notes above regarding
	 * possible better design choices.  Not that the Coordinator is only
	 * used for the optimization need.
	 *
	 * @param data The full game array data; we are going to assign values to the contained cells
	 * @param coordinator An object that manages various view/cross-view needs.
	 *
	 * @return true; Could use this instead of an exception to indicate success/failure.  Need to determine what makes the most sense.
	 */
	@Override
	public boolean load(final Cell data[][], final Coordinator coordinator)
	{
		System.out.println("Loading " + filePath.toString());

		/*
		 * Lines read from the file will be formed from
		 *   valid values: 0-9, x, ' ' i.e. space
		 *
		 *  A line is formed by 9 iterations of
		 *  	char being one of 1-9 OR X
		 *  	' '
		 *  a CRLF terminates the line.
		 *
		 *  A file consist of 9 lines as defined above.
		 *  Example:
		 *  	7 x x x x 8 x x x
		 *		x x x x 4 x 7 9 1
		 *		x 9 3 x x x 5 x x
		 *		x x x x 1 x 8 2 x
		 *		x x x 7 x 2 x x x
		 *		x 8 6 x 3 x x x x
		 *		x x 2 x x x 4 6 x
		 *		4 3 7 x 5 x x x x
		 *		x x x 9 x x x x 5
		 */

		/*
		 * mutable references/variables are not allowed to be referenced in
		 * the lambda code.  We get around the particular need by using
		 * arrays - the array reference is final but we *are* allowed to
		 * update the content of the array.  There may be other ways the
		 * underly need could be solved but I didn't look very hard.
		 */
		final int row[] =  {-1};
		final int col[] = {0};

		try
		{
			Files.lines(filePath)  				// handle file content - produce lines
		     .map(s -> s.trim())  				// remove leading/trailing white space if found
		     .filter(s -> !s.isEmpty())  		// get rid of empty lines now
		     .forEach(							// We are passing an instance of the Java Consumer<T> interface which is created
		    		 							//		through the syntax below. The block (i.e. {..}) after "l ->" ends up as the "accept" method impl.
		    	l -> {							// take each line and break it up into the #'s or X's
	    			Arrays.stream(l.split(" ")).forEach(
	    			 c ->
	    			 	{
	    			 		col[0] %= 9;
	    			 		final int cTmp = col[0]++;  // Note cTmp gives me a stable # before the increment of the array value

	    			 		if (cTmp == 0) // any time we are at the 0 column, we should increment our row number.
	    			 		{
	    			 			row[0]++;  // note initial -1 above
	    			 		}
	    			 		final int rTmp = row[0];  // Here we get the incremented val

	    			 		try
	    			 		{
	    			 			final int val = Integer.parseInt(c);
	    			 			data[rTmp][cTmp].setAcceptedVal(val);
	    			 			data[rTmp][cTmp].validVals.clear();  // remove remaining vals
	    			 			coordinator.removedUsedVal(val, rTmp, cTmp);  // remove val from views
	    			 		}
	    			 		catch(final NumberFormatException e1)
	    			 		{
	    			 			/*
	    			 			 *  instead of checking char for # vs 'X' we simply allow exception but don't care.
	    			 			 *  Maybe better to not produce exception but this is a common practice.
	    			 			 *  Exceptions as flow control are not a good idea; it complicates understanding
	    			 			 *  and can cause performance issues.  In this case, it may be ok due to program
	    			 			 *  usage/target.
	    			 			 */
	    			 		}
	    			 		catch(final Exception e)
	    			 		{
	    			 			// If something happens we *don't* expect then print something out and propagate.
	    			 			// the exception back up the chain since there must be a problem of some sort
	    			 			// and we can't guarantee that a solution is possible.
	    			 			System.out.println("c=" + c + " Exception: " + e.toString());
	    			 			e.printStackTrace();
	    			 			throw e;
	    			 		}
	    			 	});
		     		});
		}
		catch(final Exception e)
		{
			throw new RuntimeException(e);
		}
		return true;
	}

	Path filePath;
}
