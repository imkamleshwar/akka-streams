package exercises

import akka.actor.ActorSystem
import akka.stream.SystemMaterializer
import akka.stream.scaladsl.{Keep, Sink, Source}

//import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object LastElement extends App {

  val system = ActorSystem("LastElement")
  implicit val materializer = SystemMaterializer(system).materializer

  import system.dispatcher
  //implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  val source = Source[Int](1 to 100)
  val sink = Sink.last[Int]

  val graph = source.toMat(sink)(Keep.right)

  graph.run().onComplete {
    case Success(value) =>
      println(s"The last value: $value")
    case Failure(exception) =>
      println(s"Failure occurred while getting the last value: $exception")
  }

}
