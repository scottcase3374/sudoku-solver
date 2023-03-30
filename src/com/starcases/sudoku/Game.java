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
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

//      col 0 1 2  3 4 5  6 7 8
//   cblock 0 0 0  1 1 1  2 2 2
//row rblock
//0    0    #|#|#  #|#|#  #|#|#
//  -----  -----  -----
//1    0    #|#|#  #|#|#  #|#|#
//  -----  -----  -----
//2    0    #|#|#  #|#|#  #|#|#
//
//3    1    #|#|#  #|#|#  #|#|#
//  -----  -----  -----
//4    1    #|#|#  #|#|#  #|#|#
//  -----  -----  -----
//5    1    #|#|#  #|#|#  #|#|#
//
//6    2    #|#|#  #|#|#  #|#|#
//  -----  -----  -----
//7    2    #|#|#  #|#|#  #|#|#
//  -----  -----  -----
//8    2    #|#|#  #|#|#  #|#|#

/**
 *
 * This is the main application class.  The fun starts here.
 * This class will contain the data representing the puzzle
 * data & references to the utility objects required to
 * solve and validate the puzzle.
 *
 */
public class Game
{
	/**
	 * Constructor.  Note it is private which is ok since
	 * main is contained in this class.
	 *
	 * We do our setups here.
	 */
	private Game()
	{
		/*
		 *  A somewhat general and common practice is to put most constructor logic in some
		 *  sort of initialization method which is then called from the constructor.  A reason
		 *  for that is that you can have multiple constructors and if there is a need to
		 *  create additional ones then there is less need to duplicate logic or take the
		 *  time at that point to factor it out into a new method.. just do it from the
		 *  beginning since it isn't much overhead from a time perspective.
		 */

		init();
	}

	private void init()
	{
		// init some info for metric tracking
		curGameStartInstant = Instant.now();

		/**
		 * Our array exists but has no objects at any location.
		 */
		for (int row=0; row< data.length; row++)
		{
			for (int col=0; col < data[row].length; col++)
			{
				data[row][col] = new Cell(coordinator);
			}
		}

		coordinator.init(data);
	}

	/**
	 * This is a utility method used to get data from some
	 * source and push it into our application "model".
	 *
	 * Talk points
	 * 1) Returning a reference to oneself (or application type objects)
	 * produces what is generally called a "fluent" API. This results in
	 * the ability to chain method calls together as seen in main().
	 *
	 * @param loader Object implementing Loader interface.
	 * @return Reference to "game" which is just itself.
	 *
	 * @throws IOException Just passing back any unhandled IO exceptions.
	 */
	Game load(final LoaderIntfc loader)
	{
		loader.load(data, coordinator);
		return this;
	}

	/**
	 * The method responsible for initiating the solving of the puzzle.
	 *
	 * Talk point: Note that we are using Java vararg support for the 'solutionstragies'
	 * param.  What that means is that if there were multiple parameters passes - they
	 * would result in multiple items in an array where solutionstrategies is a reference
	 * to that array.
	 *
	 * @param solutionStrategies
	 *
	 * @return Game instance - itself.
	 */
	Game process(final StrategyIntfc ... solutionStrategies)
	{
		curGameProcStartInstant = Instant.now();

		/**
		 * strategy(ies)
		 * list of incomplete views
		 * loop over incomplete views
		 *      find next proposed cell
		 *      pick next proposed value
		 * 		check validity of proposed val @ proposed cell
		 * 		if valid; set value as valid @ cell
		 *
		 * how are proposed values acquired?
		 */
		try
		{
			do
			{
				for (StrategyIntfc strategy : solutionStrategies)
				{
					strategy.process(data, coordinator);
				}
			}
			while(false);
		}
		catch(final Exception e)
		{
			System.out.println(e.toString());
		}

		Instant tmpNow = Instant.now();
		curGameTotalDur = Duration.between(curGameStartInstant, tmpNow);
		curGameTotalProcDur = Duration.between(curGameProcStartInstant, tmpNow);
		return this;
	}

	/**
	 * Method to display, to stdout, the puzzle data in a user friendly fashion.
	 *
	 * @param includeRemainingValidVals Intended to control display/hiding of the "valid vals" data but unused for now.
	 */
	void displayBoard(boolean includeRemainingValidVals)
	{
		final int MILLI_CONV = 1000000;
		System.out.println("Total Time (game_start -> load/setup -> solved): [milliseconds] " + curGameTotalDur.getNano()/MILLI_CONV);
		System.out.println("Total Time (solve): [milliseconds] " + curGameTotalProcDur.getNano()/MILLI_CONV);

		/**
		 * Talk points
		 * 1) Note the style of for-loop here; we didn't need to know
		 * a specific index so this style is the cleanest in that case.
		 * 2) This is a "procedural" or "imperative" style of processing by
		 * using nested explicit loops. We could use a more functional style
		 * but I wanted to use a mix of styles in places to demonstrate
		 * them.
		 *
		 * I didn't put hardly any time into this; there are surely much better ways of doing this. Some ways
		 * come to mind (templating of various forms) but would incur a dependency and a 3rd party
		 * library you would have to understand..  simple was better here.  I should have cleaned
		 * it up more than it is though. This is an example of "less than ideal".
		 */
		final StringBuilder boardText = new StringBuilder();
		final StringBuilder extraOutput = new StringBuilder();
		final StringBuilder boardWithExtra = new StringBuilder();
		final StringBuilder boardWithExtra1 = new StringBuilder();
		final StringBuilder tmpStr = new StringBuilder();

		for (Cell [] row : data)
		{
			for (Cell col : row)
			{
				final String tmp = col.peekProposal().getFirst() + " ";  // Remember this declaration is IN the for loop so each iteration
																		 //  is effectively entering a new scope so "final" is valid to use.
				boardText.append(tmp);

				formatValidVal(tmpStr, col.getValidVals());

				final String fmtString = "%1$19s";
				boardWithExtra.append(String.format(fmtString, tmp)); // board - formated
				boardWithExtra1.append(String.format(fmtString, tmpStr)); // valid vals - formatted
				tmpStr.setLength(0);
			}
			boardText.append("\n");

			extraOutput.append(boardWithExtra);
			extraOutput.append("\n");
			extraOutput.append(boardWithExtra1);
			extraOutput.append("\n");

			boardWithExtra1.setLength(0);
			boardWithExtra.setLength(0);
		}

		System.out.println(boardText);
		System.out.println("\n\n");

		if (includeRemainingValidVals)
			System.out.println(extraOutput);
	}

	/**
	 * Utility method for forming part of the output for the display of the "valid values".  We take the set of values and simply generate
	 * them into  the form "[x,x,x..]". If the set contains all #'s (1-9) then we shorten the display form to [*].
	 *
	 * @param outStr This is a non-null instance; data is appended to this.
	 * @param validVals A set of potentially valid vals; this is formatted into a nicely formatted text format
	 *
	 * @return length of text appended to outStr
	 */
	int formatValidVal(final StringBuilder outStr, final Set<Integer> validVals)
	{
		final String valStr = validVals.size() == 9 ? "[*]" : validVals.stream().map(String::valueOf).collect(Collectors.joining(",", "[", "]"));
		outStr.append(valStr);
		return valStr.length();
	}

	// This is a coordinator which manages the views.
	Coordinator coordinator = new Coordinator();

	/**
	 * The primary game data
	 */
	Cell data[][] = new Cell[9][9];

	/**
	 * Timing data - from the creation of the Game instance to completion of solution (not display).
	 */
	Instant curGameStartInstant;
	Duration curGameTotalDur;

	/**
	 * Timing data - for the processing of the solution only; no setup
	 */
	Instant curGameProcStartInstant;
	Duration curGameTotalProcDur;

	/**
	 * The main method.  This is the entry point into the application.
	 *
	 * We construct a Game instance, load data, solve it and display the result.
	 *
	 * @param args Intended to pass a directory path.
	 *
	 * @throws Exception Allow any exceptions to propagate out and terminate the program.
	 */
	public static void main(final String args[]) throws Exception
	{
		/**
		 * talk points
		 * 1) Note we construct a Game instance and immediately start
		 * calling methods on it with other methods chained to the
		 * method return value of the prior call. This is a "fluent"
		 * style API usage.
		 *
		 * 2) With this design, it is pretty easy to desin new Strategies which can either be added below or replace
		 * the initial strategies.  I felt that was a good goal to allow experimenting with new ideas.  What kind
		 * of other strategies might be interesting?
		 *
		 * 2a) The one that may be promising would determine the views with
		 * the fewest missing data items and start the solution there; continue with views with next fewest missing
		 * cells and repeat.  This may reduce the amount (depth & overall quantity) of backtracking attempts.
		 *
		 * 2b) other ideas are using a stack based instead of recursive solution.
		 */
		Consumer<Path> consumer = (p) ->	 new Game().
	  				load(new FileLoader(p)).
	  				process(	new OnlyOneValidValStrategy(),  // we can pass any # of StrategyIntfc based instances
	  							new SerialStrategy()).			// since process uses a vararg parameter.
	  				displayBoard(true);

		if (args.length == 1)
		{
			// Take command line arg as a directory, read the file names as paths and pass each path to a new Game instance.
			Files.list(Paths.get(args[0])).forEach(consumer);
		}
	}
}
