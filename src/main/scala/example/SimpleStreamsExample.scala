package example

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, Supervision}
import akka.stream.scaladsl._

import scala.concurrent.{Await, Future, ExecutionContext}

object SimpleStreamExample extends App {

  val decider: Supervision.Decider = {
    case _: IllegalArgumentException => Supervision.Stop
    case x: Throwable                => println(s"oops ${x}") ; Supervision.Resume
  }

  def main: Unit = {
    implicit val system = ActorSystem("example")
    implicit val executionContext = system.dispatcher
    implicit val materializer = ActorMaterializer(ActorMaterializerSettings(system).withSupervisionStrategy(decider))

    val numbers = 1 to 10

    // We create a Source that will iterate over the number sequence
    val numberSource: Source[Int, NotUsed] = Source.fromIterator(() => numbers.iterator)

    // Only let pass even numbers through the Flow
    val isEvenFlow: Flow[Int, Int, NotUsed] = Flow[Int].filter((num) => num % 2 == 0)

    // Create a Source of even random numbers by combining the random number Source with the even number filter Flow
    val evenNumbersSource: Source[Int, NotUsed] = numberSource.via(isEvenFlow)

    // A Sink that will write its input onto the console
    val consoleSink: Sink[Int, Future[Done]] = Sink.foreach[Int](println)

    // A flow to test exceptions and supervision strategy
    def flow1: Flow[Int, Int, NotUsed] =
      Flow[Int].
        mapAsync(2)(input =>
          Future {
            val ret =
            if (input != 4)
              - input
            else
              throw new Exception("Oops")
            Thread.sleep(1000)
            ret
          })

    // Connect the Source with the Sink and run it using the materializer
    evenNumbersSource.
      via(flow1).
      runWith(consoleSink).
      onComplete {
        case x =>
          println(x)
          println("done")
          system.terminate
      }
  }

  main
}
