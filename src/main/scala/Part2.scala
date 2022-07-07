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
