package part1primer

import akka.actor.ActorSystem
import akka.stream.{Materializer, SystemMaterializer}
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}

import scala.util.{Failure, Success}

object MaterializingStreams extends App {

  val system = ActorSystem("MaterializingStreams")
  implicit val materializer: Materializer = SystemMaterializer(system).materializer

  import system.dispatcher

  val graph = Source(1 to 10).to(Sink.foreach(println))
  //val simpleMaterializedValue = simpleGraph.run() // run method returns a materialized value(NotUsed in this case)

  val source = Source(1 to 10)
  //val sink = Sink.reduce[Int]((a, b) => a + b)
  //val sumFuture = source.runWith(sink) // returns a Future[Int]
  //  sumFuture.onComplete{
  //    case Success(value) =>
  //      println(s"The sum of all elements is : $value")
  //    case Failure(exception) =>
  //      println(s"Sum not calculated as exception occurred: $exception")
  //  }

  // choosing the materialized value
  // By default the left most is taken
  val simpleSource = Source(1 to 10)
  val simpleFlow = Flow[Int].map(x => x * x)
  val simpleSink = Sink.foreach(println)
  //val simpleGraph = simpleSource.viaMat(simpleFlow)((sourceMat, flowMat) => flowMat).toMat(simpleSink)((sourceMat, flowMat) => flowMat)
  /*val simpleGraph = simpleSource.viaMat(simpleFlow)(Keep.right).toMat(simpleSink)(Keep.right)
  simpleGraph.run().onComplete {
    case Success(_) =>
      println("stream processing completed successfully")
    case Failure(exception) =>
      println(s"stream processing failed: $exception")
  }*/

  // sugars
  Source(1 to 10).runWith(Sink.reduce[Int](_ + _)) //Source(1 to 10).toMat(Sink.reduce[Int](_ + _))(Keep.right).run()
  Source(1 to 10).runReduce(_ + _)

  // backwards
  Sink.foreach[Int](println).runWith(Source.single(43))
  Source.single(43).runWith(Sink.foreach(println))
  
  // Both ways
  Flow[Int].map(x => x * 2).runWith(simpleSource, simpleSink)

}
