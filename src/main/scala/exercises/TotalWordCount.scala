package exercises

import akka.actor.ActorSystem
import akka.stream.SystemMaterializer
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}

import scala.util.{Failure, Success}

object TotalWordCount extends App {

  val system = ActorSystem("WordCount")

  implicit val materializer = SystemMaterializer(system).materializer

  import system.dispatcher

  val listOfString = List("I am learning Akka", "I am also learning akka streams", "getting the meaning of life" )

  val source = Source(listOfString)
  val flow = Flow[String].map(str => str.split(" ").length)
  val sink = Sink.reduce[Int](_ + _)

  val graph = source.viaMat(flow)(Keep.right).toMat(sink)(Keep.right)

  graph.run().onComplete {
    case Success(value) =>
      println(s"Total word count: $value")
    case Failure(exception) =>
      println(s"There was a problem calculating the total word count: $exception")
  }

}
