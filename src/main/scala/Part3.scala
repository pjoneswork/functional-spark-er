import java.time.Duration

import zio.{App, Cause, ExitCode, IO, Task, UIO, URIO}
import zio.duration._

trait ZIOAppDefault extends App {
  def run: IO[Any, Any]
  final override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = run.exitCode
}

object Console {
  def printLine(s: String): UIO[Unit] = IO.succeed(println(s))
  val readLine: UIO[String] = IO.succeed(scala.io.StdIn.readLine())
}

object SuccessEffect extends ZIOAppDefault {

  /**
   * EXERCISE
   *
   * Using `ZIO.succeed`, create an effect that succeeds with the string
   * "Hello World".
   */
  override val run =
    IO.succeed("Hello World")
}

object HelloWorld extends ZIOAppDefault {

  /**
   * EXERCISE
   *
   * Implement a simple "Hello World!" program by invoking `Console.printLine`
   * to create an effect that, when executed, will print out "Hello World!" to
   * the console.
   */
  val run =
    ???
}

object SimpleMap extends ZIOAppDefault {
  import Console.readLine

  /**
   * EXERCISE
   *
   * Using `ZIO#map`, map the string success value of `Console.readLine` into an
   * integer (the length of the string)`.
   */
  val run =
    ???
}

object PrintSequenceZip extends ZIOAppDefault {

  /**
   * EXERCISE
   *
   * Using `zip`, compose a sequence of `Console.printLine` effects to produce an effect
   * that prints three lines of text to the console.
   */
  val run =
    ???
}

object PrintSequence extends ZIOAppDefault {

  /**
   * EXERCISE
   *
   * Using `*>` (`zipRight`), compose a sequence of `Console.printLine` effects to
   * produce an effect that prints three lines of text to the console.
   */
  val run =
    ???
}

object PrintReadSequence extends ZIOAppDefault {

  /**
   * EXERCISE
   *
   * Using `*>` (`zipRight`), sequentially compose a `Console.printLine` effect, which
   * models printing out "Hit Enter to exit...", together with a `Console.readLine`
   * effect, which models reading a line of text from the console.
   */
  val run =
    ???
}

object SimpleDuplication extends ZIOAppDefault {

  /**
   * EXERCISE
   *
   * In the following program, the expression `Console.printLine("Hello again")`
   * appears three times. Factor out this duplication by introducing a new
   * value that stores the expression, and then referencing that variable
   * three times.
   */
  val run = {
    Console.printLine("Hello") *>
      Console.printLine("Hello again") *>
      Console.printLine("Hello again") *>
      Console.printLine("Hello again")
  }
}

object FlatMap extends ZIOAppDefault {

  /**
   * EXERCISE
   *
   * The following program is intended to ask the user for their name, then
   * read their name, then print their name back out to the user. However,
   * the `zipRight` (`*>`) operator is not powerful enough to solve this
   * problem, because it does not allow a _subsequent_ effect to depend
   * on the success value produced by a _preceding_ effect.
   *
   * Solve this problem by using the `ZIO#flatMap` operator, which composes
   * a first effect together with a "callback", which can return a second
   * effect that depends on the success value produced by the first effect.
   */
  val run =
    Console.printLine("What is your name?") *>
      Console.readLine *> // Use .flatMap(...) here
      Console.printLine("Your name is: ")
}

object PromptName extends ZIOAppDefault {

  /**
   * EXERCISE
   *
   * The following program uses a combination of `zipRight` (`*>`), and
   * `flatMap`. However, this makes the structure of the program harder
   * to understand. Replace all `zipRight` by `flatMap`, by ignoring the
   * success value of the left hand effect.
   */
  val run =
    Console.printLine("What is your name?") *>
      Console.readLine.flatMap(name => Console.printLine(s"Your name is: ${name}"))

  /**
   * EXERCISE
   *
   * Implement a generic "zipRight" that sequentially composes the two effects
   * using `flatMap`, but which succeeds with the success value of the effect
   * on the right-hand side.
   */
  def myZipRight[E, A, B](
    left: IO[E, A],
    right: IO[E, B]
  ): IO[E, B] =
    ???
}

object ForComprehension extends ZIOAppDefault {

  /**
   * EXERCISE
   *
   * Rewrite the following program to use a `for` comprehension.
   */
  val run =
    Console
      .printLine("What is your name?")
      .flatMap(
        _ => Console.readLine.flatMap(name => Console.printLine(s"Your name is: ${name}"))
      )

}

object ForComprehensionBackward extends ZIOAppDefault {

  val readInt = Console.readLine.flatMap(string => IO.effect(string.toInt)).orDie

  /**
   * EXERCISE
   *
   * Rewrite the following program, which uses a `for` comprehension, to use
   * explicit `flatMap` and `map` methods. Note: each line of the `for`
   * comprehension will translate to a `flatMap`, except the final line,
   * which will translate to a `map`.
   */
  val run = {
    for {
      _   <- Console.printLine("How old are you?")
      age <- readInt
      _ <- if (age < 18) Console.printLine("You are a kid!")
          else Console.printLine("You are all grown up!")
    } yield ()
  }
}

object NumberGuesser extends ZIOAppDefault {
  def analyzeAnswer(random: Int, guess: String) =
    if (random.toString == guess.trim) Console.printLine("You guessed correctly!")
    else Console.printLine(s"You did not guess correctly. The answer was ${random}")

  /**
   * EXERCISE
   *
   * Choose a random number (using `Random.nextInt`), and then ask the user to guess
   * the number (using `Console.readLine`), feeding their response to `analyzeAnswer`,
   * above.
   */
  val run =
    ???
}

object SingleSyncInterop extends ZIOAppDefault {

  /**
   * EXERCISE
   *
   * Using ZIO.effect, convert `println` into a ZIO function.
   */
  def myPrintLn(line: String): Task[Unit] = ???

  val run =
    myPrintLn("Hello World!")
}

object MultipleSyncInterop extends ZIOAppDefault {

  /**
   * Using `ZIO.effect`, wrap Scala's `println` method to lazily convert it
   * into a functional effect, which describes the action of printing a line
   * of text to the console, but which does not actually perform the print.
   */
  def printLine(line: String): Task[Unit] = ???

  /**
   * Using `ZIO.effect`, wrap Scala's `scala.io.StdIn.readLine()` method to
   * lazily convert it into a ZIO effect, which describes the action of
   * printing a line of text to the console, but which does not actually
   * perform the print.
   */
  val readLine: Task[String] = ???

  val run = {
    for {
      _    <- printLine("Hello, what is your name?")
      name <- readLine
      _    <- printLine(s"Good to meet you, ${name}!")
    } yield ()
  }
}

object AsyncExample extends ZIOAppDefault {
  import scala.concurrent.ExecutionContext.global

  def loadBodyAsync(onSuccess: String => Unit, onFailure: Throwable => Unit): Unit =
    global.execute { () =>
      if (scala.util.Random.nextDouble() < 0.01) onFailure(new java.io.IOException("Could not load body!"))
      else onSuccess("Body of request")
    }

  /**
   * EXERCISE
   *
   * Using `ZIO.async`, convert the above callback-based API into a
   * nice clean ZIO effect.
   */
  lazy val loadBodyAsyncZIO: IO[Throwable, String] =
    ???

  val run =
    for {
      body <- loadBodyAsyncZIO
      _    <- Console.printLine(body)
    } yield ()
}

/*
 * INTRODUCTION
 *
 * ZIO effects model failure, in a way similar to the Scala data types `Try`
 * and `Either`. Unlike exceptions, ZIO effects are statically-typed, allowing
 * you to determine if and how effects fail by looking at type signatures.
 *
 * ZIO effects have a large number of error-related operators to transform
 * and combine effects. Some of these "catch" errors, while others transform
 * them, and still others combine potentially failing effects with fallback
 * effects.
 *
 * In this section, you will learn about all these operators, as well as the
 * rich underlying model of errors that ZIO uses internally.
 */

object ErrorConstructor extends ZIOAppDefault {

  /**
   * EXERCISE
   *
   * Using `ZIO.fail`, construct an effect that models failure with any
   * string value, such as "Uh oh!". Explain the type signature of the
   * effect.
   */
  val failed: IO[String, Nothing] = ???

  val run =
    failed.foldM(Console.printLine(_), Console.printLine(_))
}

object ErrorRecoveryOrElse extends ZIOAppDefault {

  val failed = IO.fail("Uh oh!")

  /**
   * EXERCISE
   *
   * Using `ZIO#orElse` have the `run` function compose the preceding `failed`
   * effect with another effect.
   */
  val run =
    ???
}

object ErrorShortCircuit extends ZIOAppDefault {
  val failed: IO[Any, Unit] =
    for {
      _ <- Console.printLine("About to fail...")
      _ <- IO.fail("Uh oh!")
      _ <- Console.printLine("This will NEVER be printed!")
    } yield ()

  /**
   * EXERCISE
   *
   * Using `ZIO#orElse`, compose the `failed` effect with another effect that
   * succeeds with an exit code.
   */
  val run =
    ???
}

object ErrorRecoveryFold extends ZIOAppDefault {

  val failed = IO.fail("Uh oh!")

  /**
   * EXERCISE
   *
   * Using `ZIO#fold`, map both failure and success values of `failed` into
   * the unit value.
   */
  val run =
    ???
}

object ErrorRecoveryCatchAll extends ZIOAppDefault {

  val failed: IO[String, Nothing] = IO.fail("Uh oh!")

  /**
   * EXERCISE
   *
   * Using `ZIO#catchAll`, catch all errors in `failed` and print them out to
   * the console using `Console.printLine`.
   */
  val run =
    ???
}

object ErrorRecoveryFoldZIO extends ZIOAppDefault {

  val failed: IO[String, String] = IO.fail("Uh oh!")

  /**
   * EXERCISE
   *
   * Using `ZIO#foldZIO`, print out the success or failure value of `failed`
   * by using `Console.printLine`.
   */
  val run =
    ???
}

object ErrorRecoveryEither extends ZIOAppDefault {

  val failed: IO[String, Int] = IO.fail("Uh oh!")

  /**
   * EXERCISE
   *
   * Using `ZIO#either`, surface the error of `failed` into the success
   * channel, and then map the `Either[String, Int]` into an exit code.
   */
  val run =
    ???
}

object ErrorRecoveryIgnore extends ZIOAppDefault {

  val failed: IO[String, Int] = IO.fail("Uh oh!")

  /**
   * EXERCISE
   *
   * Using `ZIO#ignore`, simply ignore the failure of `failed`.
   */
  val run =
    ???
}

object ErrorRefinement1 extends ZIOAppDefault {
  import java.io.IOException
  import scala.io.StdIn.readLine

  val broadReadLine: IO[Throwable, String] = IO.effect(scala.io.StdIn.readLine())

  /**
   * EXERCISE
   *
   * Using `ZIO#refineToOrDie`, narrow the error type of `broadReadLine` into
   * an `IOException`:
   */
  val myReadLine: IO[IOException, String] = ???

  def myPrintLn(line: String): UIO[Unit] = IO.succeed(println(line))

  val run =
    (for {
      _    <- myPrintLn("What is your name?")
      name <- myReadLine
      _    <- myPrintLn(s"Good to meet you, ${name}!")
    } yield ())
}

object ErrorRefinement2 extends ZIOAppDefault {

  import java.io.IOException
  import java.util.concurrent.TimeUnit

  /**
   * EXERCISE
   *
   * Create an effect that will get a `Duration` from the user, by prompting
   * the user to enter a decimal number of seconds. Use `refineToOrDie` to
   * narrow the error type as necessary.
   */
  lazy val getAlarmDuration: IO[IOException, Duration] = {
    def parseDuration(input: String): IO[NumberFormatException, Duration] =
      ???

    def fallback(input: String): IO[IOException, Duration] =
      Console.printLine(s"The input ${input} is not valid.") *> getAlarmDuration

    for {
      _        <- Console.printLine("Please enter the number of seconds to sleep: ")
      input    <- Console.readLine
      duration <- parseDuration(input) orElse fallback(input)
    } yield duration
  }

  /**
   * EXERCISE
   *
   * Create a program that asks the user for a number of seconds to sleep,
   * sleeps the specified number of seconds using `ZIO.sleep(d)`, and then
   * prints out a wakeup alarm message, like "Time to wakeup!!!".
   */
  val run =
    ???
}

object SequentialCause extends ZIOAppDefault {

  val failed1 = Cause.fail("Uh oh 1")
  val failed2 = Cause.fail("Uh oh 2")

  /**
   * EXERCISE
   *
   * Using `Cause.++`, form a sequential cause by composing `failed1`
   * and `failed2`.
   */
  lazy val composed = ???

  /**
   * EXERCISE
   *
   * Using `Cause.prettyPrint`, dump out `composed` to the console.
   */
  val run =
    ???
}

object ParallelCause extends ZIOAppDefault {

  val failed1 = Cause.fail("Uh oh 1")
  val failed2 = Cause.fail("Uh oh 2")

  /**
   * EXERCISE
   *
   * Using `Cause.&&`, form a parallel cause by composing `failed1`
   * and `failed2`.
   */
  lazy val composed = ???

  /**
   * EXERCISE
   *
   * Using `Cause.prettyPrint`, dump out `composed` to the console.
   */
  val run =
    ???
}

object Sandbox extends ZIOAppDefault {

  val failed1    = IO.fail("Uh oh 1")
  val failed2    = IO.fail("Uh oh 2")
  val finalizer1 = IO.fail(new Exception("Finalizing 1!")).orDie
  val finalizer2 = IO.fail(new Exception("Finalizing 2!")).orDie

  val composed = IO.uninterruptible {
    (failed1 ensuring finalizer1) zipPar (failed2 ensuring finalizer2)
  }

  /**
   * EXERCISE
   *
   * Using `ZIO#sandbox`, sandbox the `composed` effect and print out the
   * resulting `Cause` value to the console using `Console.printLine`.
   */
  val run =
    ???
}
