It is a best practice to write test code for your classes.  There is a lot of variations in testing
and here I will only show a single class being tested using a common tool named 'JUnit'.  The
Eclipse IDE has some nice support for creating a test suite (something to run a bunch of tests across
a bunch of classes) and the individual class tests.

I will note that it can take a lot of effort to maintain tests so create tests wisely.  It is also easy
to write broken tests which report the wrong results.  It is also true that you get out of testing
what you put into it.  So who tests the test code??  Be careful writing tests.

Something to think about is how classes can be tested.  You should consider how a class can be tested
when considering a design.  Ease of testability is considered a good attribute.  Many times testability
is improved by designing classes so that they receive instances of various data instead of using 'new'
internal to themselves or directly calling factory methods.  Many changes in technology have occurred
over the years and one of them has been an increase in the use of "dependency injection".  Google for
that term and maybe "Spring" with it.  You may also want to read up on "CDI" - contexts and dependency
injection.  All these have a lot of merit.

There are other unit testing libs/frameworks which can be used with / instead of or with Junit. Examples
include Mockito and TestNG.  It is always worth doing some research to see what testing frameworks
have sprung up that might be helpful.  Not everything will be but helpful sometimes there is a jewel in the
midst of all the coal.


