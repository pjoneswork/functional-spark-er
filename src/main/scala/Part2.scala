object Recap {
  // Referential Transparency
  //  - Total
  //  - Deterministic
  //  - Pure
  //
  // We can refactor functions to satisfy the above criteria
  //
  // You can make functions total by using structures like:
  // Option: Fails in one way
  // Try: Fails with an exception
  // Either: Can handle arbitrary failures
  //
  // The way to combine these values is with a operation called `flatMap`.
  // Scala makes it easy to use this structures with for comprehensions.
}

object valuesVsStatements {
  // Procedural effects are side-effecting,non-deterministic, partial interactions
  // with the real world. They are the soul of procedural and most object-oriented
  // programming

  def main = {
    println("What is your name?")
    val name = scala.io.StdIn.readLine()
    println(s"Hi, $name")
    ()
  }

  // val program = main()
  // val programs = List.fill(10)(program)
  // val retries = program => program.retried

  // Values are abstraction and refactor friendly

  // The goal of functional effects is to bring statements into the world
  // of values.
  //
  // Functional effects are immutable data structures that merely describe
  // sequences of operations.
  // At the end of the world the data structure has to be impurely
  // interpreted to real world effects.

  // procedural print line
  println("Hello World")

  // functional print line
  case class PrintLine(text: String)
}

object io {
  case class IO[+A](unsafeInterpret: () => A) { self =>
    def map[B](f: A => B): IO[B] = self.flatMap(f.andThen(IO.effect(_)))
    def flatMap[B](f: A => IO[B]): IO[B] =
      IO.effect(f(self.unsafeInterpret()).unsafeInterpret())
  }

  object IO {
    def effect[A](eff: => A): IO[A] = IO(() => eff)
  }

  def putStrLn(s: String): IO[Unit] = IO.effect(println(s))
  val getStrLn: IO[String] = IO.effect(scala.io.StdIn.readLine())

  val main =
    for {
      _ <- putStrLn("What is your name?")
      name <- getStrLn
      _ <- putStrLn(s"Hi, $name")
    } yield ()


  // def loadTest(url: String, numWorkers: Int): IO[Unit] = {
  //   val policy = Schedule.recurs(10).jitter
  //   val worker = client.get(url).retry(policy)
  //   val workers = List.fill(10)(worker)

  //   IO.collectAllPar(workers)
  // }
}

object io2 {
  import zio.console.{putStrLn, getStrLn}
  def prog1() = {
    println("What is your name?")
    val name = scala.io.StdIn.readLine()
    println(s"Hi, $name")
    ()
  }

  // Functional effects are immutable data structures that describe procedural
  // effects, and they can be interpreted later into the procedural effects
  // they describe.
  val prog2 =
    for {
      _ <- putStrLn("What is your name?")
      name <- getStrLn
      _ <- putStrLn(s"Hi, $name")
    } yield ()

    // Differences:
    // prog1 performs a series of effect one after the other
    // prog2 returns a value that doesn't do anything. You need to
    // unsafely run it to get the value
    //
    // Syntax differences
    // prog1 is a series of statements
    // prog2 value oriented way of creating a program. Everything is value or
    // an operator for combining values together.
    //
    // Functional effects translate programs into pure values that you can
    // pass into functions or return from functions.
}

/*
 * INTRODUCTION
 *
 * ZIO effects are immutable data values that model a possibly complex series
 * of async, concurrent, resourceful, and contextual computations.
 *
 * The only effect type in ZIO is called ZIO, and has three type parameters,
 * which permit accessing context from an environment (`R`), failing with a
 * value of a certain type (`E`), and succeeding with a value of a certain
 * type (`A`).
 *
 * Unlike Scala's Future, ZIO effects are completely lazy. All methods on ZIO
 * effects return new ZIO effects. No part of the workflow is executed until
 * one of the `unsafeRun*` functions are called.
 *
 * ZIO effects are transformed and combined using methods on the ZIO data type.
 * For example, two effects can be combined into a sequential workflow using
 * an operator called `zip`. Similarly, two effects can be combined into a
 * parallel workflow using an operator called `zipPar`.
 *
 * The operators on the ZIO data type allow very powerful, expressive, and
 * type-safe transformation and composition, while the methods in the ZIO
 * companion object allow building new effects from simple values (which are
 * not themselves effects).
 *
 * In this section, you will explore both the ZIO data model itself, as well
 * as the very basic operators used to transform and combine ZIO effects, as
 * well as a few simple ways to build effects.
 */

object ZIOModel {

  /**
   * EXERCISE
   *
   * Implement all missing methods on the ZIO companion object.
   */
  object IO {
    def succeed[A](a: => A): IO[Nothing, A] = ???

    def fail[E](e: => E): IO[E, Nothing] = ???

    def effect[A](sideEffect: => A): IO[Throwable, A] = ???
  }

  /**
   * EXERCISE
   *
   * Implement all missing methods on the ZIO class.
   */
  final case class IO[+E, +A](run: () => Either[E, A]) { self =>
    def map[B](f: A => B): IO[E, B] = ???

    def flatMap[E1 >: E, B](f: A => IO[E1, B]): IO[E1, B] =
      ???

    def zip[E1 >: E, B](that: IO[E1, B]): IO[E1, (A, B)] =
      ???

    def either: IO[Nothing, Either[E, A]] = ???

    def orDie(implicit ev: E <:< Throwable): IO[Nothing, A] =
      IO(() => self.run().fold(throw _, Right(_)))
  }

  def printLine(line: String): IO[Nothing, Unit] =
    IO.effect(println(line)).orDie

  val readLine: IO[Nothing, String] =
    IO.effect(scala.io.StdIn.readLine()).orDie

  def unsafeRun[A](io: IO[Throwable, A]): A =
    io.run().fold(throw _, a => a)

  /**
   * Run the following main function and compare the results with your
   * expectations.
   */
  def main(args: Array[String]): Unit =
    unsafeRun {
      printLine("Hello, what is your name?").flatMap(
        _ => readLine.flatMap(name => printLine(s"Your name is: ${name}"))
      )
    }
}

object ZIOTypes {
  type ??? = Nothing
  trait ZIO[-R, +E, +A]

  /**
   * EXERCISE
   *
   * Provide definitions for the ZIO type aliases below.
   */
  type Task[+A]     = ???
  type UIO[+A]      = ???
  type RIO[-R, +A]  = ???
  type IO[+E, +A]   = ???
  type URIO[-R, +A] = ???
}
