
object ReferentialTransparency {
  // Referential Transparency is the most important part of functional programming.
  // Allows refactoring with ease. Allows building program out of blocks

  def f(x: Int) = 2 * x
  def g(x: Int) = x * x
  def h(x: Int, y: Int) = x + y

  def messyFunction(x: Int) = {
    h(h(g(f(x)), f(x)), g(f(x)))
  }

  def cleanerFunction(x: Int) = {
    val fx = f(x)
    val gfx = g(fx)
    h(h(gfx, fx), gfx)
  }

  def anotherFunction(x: Int) = {
    val fx = f(x)

    def inner(y: Int) = {
      val gy = g(y)
      h(h(gy, y), gy)
    }

    inner(fx)
  }
}

object functions {
  // 1. Total - every input returns a value
  // 2. Deterministic - The same input produces the same output
  // 3. Pure - no side effects.

  type ??? = Nothing

  //
  // EXERCISE 1
  //
  // Convert the following non-function into a function.
  //
  def parseInt1(s: String): Int = s.toInt
  def parseInt2(s: String): Option[Int] = scala.util.Try(s.toInt).toOption

  //
  // EXERCISE 2
  //
  // Convert the following non-function into a function.
  //
  def arrayUpdate1[A](arr: Array[A], i: Int, f: A => A): Unit =
    arr.update(i, f(arr(i)))
  def arrayUpdate2[A](arr: Array[A], i: Int, f: A => A): Array[A] = {
    val newArr = arr.clone()
    newArr.update(i, f(arr(i)))
    newArr
  }


  //
  // EXERCISE 3
  //
  // Convert the following non-function into a function.
  //
  def divide1(a: Int, b: Int): Int = a / b
  def divide2(a: Int, b: Int): ??? = ???

  //
  // EXERCISE 4
  //
  // Convert the following non-function into a function.
  //
  var id = 0
  def freshId1(): Int = {
    val newId = id
    id += 1
    newId
  }
  def freshId2( /* ??? */ ): (Int, Int) = ???

  //
  // EXERCISE 5
  //
  // Convert the following non-function into a function.
  //
  import java.time.LocalDateTime
  def afterOneHour1: LocalDateTime              = LocalDateTime.now.plusHours(1)
  def afterOneHour2( /* ??? */ ): LocalDateTime = ???

  //
  // EXERCISE 6
  //
  // Convert the following non-function into function.
  //
  def head1[A](as: List[A]): A = {
    if (as.length == 0) println("Oh no, it's impossible!!!")
    as.head
  }
  def head2[A](as: List[A]): ??? = ???

  //
  // EXERCISE 7
  //
  // Convert the following non-function into a function.
  //
  trait Account
  trait Processor {
    def charge(account: Account, amount: Double): Unit
  }
  final case class Coffee() {
    final val price = 3.14
  }
  def buyCoffee1(processor: Processor, account: Account): Coffee = {
    val coffee = Coffee()
    processor.charge(account, coffee.price)
    coffee
  }
  final case class Charge[+A](account: Account, amount: Double, value: A)
  def buyCoffee2(account: Account): ??? = ???

  //
  // EXERCISE 8
  //
  // Implement the following function under the Scalazzi subset of Scala.
  //
  def printLine(line: String): Unit = ???

  //
  // EXERCISE 9
  //
  // Implement the following function under the Scalazzi subset of Scala.
  //
  def readLine: String = ???

  //
  // EXERCISE 10
  //
  // Implement the following function under the Scalazzi subset of Scala.
  //
  def systemExit(code: Int): Unit = ???

  //
  // EXERCISE 11
  //
  // Rewrite the following non-function `printer1` into a pure function, which
  // could be used by pure or impure code.
  //
  def printer1(): Unit = {
    println("Welcome to the help page!")
    println("To list commands, type `commands`.")
    println("For help on a command, type `help <command>`")
    println("To exit the help page, type `exit`.")
  }
  def printer2[A](println: String => A, combine: (A, A) => A): A = ???

  //
  // EXERCISE 12
  //
  // Create a purely-functional drawing library that is equivalent in
  // expressive power to the following procedural library.
  //
  trait Draw {
    def goLeft(): Unit
    def goRight(): Unit
    def goUp(): Unit
    def goDown(): Unit
    def draw(): Unit
    def finish(): List[List[Boolean]]
  }
  def draw1(size: Int): Draw = new Draw {
    val canvas = Array.fill(size, size)(false)
    var x      = 0
    var y      = 0

    def goLeft(): Unit  = x -= 1
    def goRight(): Unit = x += 1
    def goUp(): Unit    = y += 1
    def goDown(): Unit  = y -= 1
    def draw(): Unit = {
      def wrap(x: Int): Int =
        if (x < 0) (size - 1) + ((x + 1) % size) else x % size

      val x2 = wrap(x)
      val y2 = wrap(y)

      canvas.updated(x2, canvas(x2).updated(y2, true))
    }
    def finish(): List[List[Boolean]] =
      canvas.map(_.toList).toList
  }
  def draw2(size: Int /* ... */ ): ??? = ???
}

object combiningEffects {
  import scala.util.Try
  type ??? = Nothing

  // 1. Read a double from a user
  // 2. Subtract 10
  // 3. Take the log of the result
  // 4. Then take a square root
  // 5. Times two.

  def readDouble1(): Double = scala.io.StdIn.readLine().toDouble
  def log1(x: Double): Double = scala.math.log(x)
  def sqrt1(x: Double): Double = scala.math.sqrt(x)

  def func1(): Double = {
    val a = readDouble1
    val b = a - 10
    val c = log1(b)
    val d = sqrt1(c)
    d * 2
  }

  def readDouble2(): Option[Double] = Try(scala.io.StdIn.readLine().toDouble).toOption
  def log2(x: Double): Option[Double] = Try(scala.math.log(x)).toOption
  def sqrt2(x: Double): Option[Double] = Try(scala.math.sqrt(x)).toOption

  // sealed trait Option[+A] { self =>
  //   def map[B](f: A => B): Option[B] = ???
  //   def flatMap[B](f: A => Option[B]): Option[B] = self match {
  //     case Some(a) => f(a)
  //     case None => None
  //   }
  // }
  // final case class Some[A](a: A) extends Option[A]
  // final case object None extends Option[Nothing]

  def func2(): Option[Double] =
    readDouble2().flatMap(a => {
      val b = a - 10
      log2(b).flatMap(c => sqrt2(c).map(d => d *2))
    })

  def func3(): Option[Double] =
    readDouble2() match {
      case Some(a) =>
        val b = a - 10
        log2(b) match {
          case Some(c) =>
            sqrt2(c) match {
              case Some(d) =>
                Some(d * 2)
              case None => None
            }
          case None => None
        }
      case None => None
    }

  def func4(): Option[Double] =
    for {
      a <- readDouble2()
      b = a - 10
      c <- log2(b)
      d <- sqrt2(c)
    } yield {
      val x = a * 3
      d * 2 + x
    }

  def readDouble3(): Either[String, Double] =
    Try(scala.io.StdIn.readLine().toDouble).toOption.toRight("Could not convert user input to Double")
  def log3(x: Double): Either[String, Double] =
    Try(scala.math.log(x)).toOption.toRight(s"Input, $x, was non-positive")
  def sqrt3(x: Double): Either[String, Double] =
    Try(scala.math.sqrt(x)).toOption.toRight(s"Input, $x, was negative")

  def func5(): Either[String, Double] =
    for {
      a <- readDouble3()
      b = a - 10
      c <- log3(b)
      d <- sqrt3(c)
    } yield d * 2

  def readDouble4(): Try[Double] =
    Try(scala.io.StdIn.readLine().toDouble)
  def log4(x: Double): Try[Double] =
    Try(scala.math.log(x))
  def sqrt4(x: Double): Try[Double] =
    Try(scala.math.sqrt(x))

  readDouble4().flatMap(a => log4(a))

  def func6(): Try[Double] =
    for {
      a <- readDouble4()
      b = a - 10
      c <- log4(b)
      d <- sqrt4(c)
    } yield d * 2

    def head(x: Option[Int]): Int = ???
}
