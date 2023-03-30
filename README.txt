The intent of this project is, to a large degree, to give my kids something
to reference as they learn about programming. I can't teach them everything
I know but I can give them things which I hope provide a useful range of
information from adequate & appropriate documentation, different coding
styles, analysis thoughts, design/implementation choices, tradeoffs and
hopefully the urge to always question assumptions, explore different solutions
and learn new things.

All the code is heavily commented and I tried to include "Talk point" comments
for things I wanted to make sure my kids think about. Much of this implementation
is still well past their skills but it hopefully will give us a long term
conversation item.

NOTE: This was not written to be "THE ULTIMATE" Sudoku solver. It tries to
demonstrate a lot of material and in most cases it is done reasonably and
in a few places I comment on how things might be done better (design, etc).

I did this initial implementation before doing any real research on Sudoku
algorithms.  I was doing some recent looking around and ran across this link:

	http://stackoverflow.com/questions/1518346/optimizing-the-backtracking-algorithm-solving-sudoku

It was quite interesting and may provide some ideas to enhance the performance for extra challenge.

To summarize this Sudoku solver; it would be classified as a backtracking implementation
with some optimizations.  It is written to allow making customizations - different
game data sources by using interfaces, multiple "strategies" to solve things, again,
via interfaces.  I am hoping that my kids will change it to their hearts content
and see if they can make it better somehow.

It requires Java 8+.

If all the required setup is there and correct; you should see some output in the "Console" window. Each of the 3 included Sudoku puzzles
were loaded one at a time and solved.  Each produces a bit of output.
1) File name loaded
2) metrics (timings)
3) Puzzle result
4) The initial board info showing the potential valid values for each cell as of entry to the "SerialStrategy".

I also hope that others out there may find this useful and/or interesting in some way.

Hoping you enjoy this and that Jesus blesses your day!
Scott Case

