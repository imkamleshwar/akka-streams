package exercises

import akka.actor.ActorSystem
import akka.stream.SystemMaterializer
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}

import scala.util.{Failure, Success}

object WordCount extends App {

  val system = ActorSystem("WordCount")
  implicit val materializer = SystemMaterializer(system).materializer

  import system.dispatcher

  val listOfString = List("I am learning Akka", "I am also learning akka streams", "getting the meaning of life")

  val source = Source[String](listOfString)
  //val flow = Flow[String].map(str => str.split(" ").mkString("|"))
  //val flow = Flow[String].map(str => str.split(" ").length)
  //val sink = Sink.foreach(println)

  //val graph = source.viaMat(flow)(Keep.right).toMat(sink)(Keep.right)
  val graph = source.runFold(0)((accumulator, sentence) => accumulator + sentence.split(" ").length)

  graph.onComplete {
    case Success(sum:Int) =>
      println(s"Word count: $sum")
    case Failure(exception) =>
      println(s"graph did not worked properly: $exception")
  }

}
