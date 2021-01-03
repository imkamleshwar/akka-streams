package part1primer

import akka.actor.ActorSystem
import akka.stream.SystemMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}

object OperatorFusion extends App {

  implicit val system = ActorSystem("OperatorFusion")
  implicit val materializer = SystemMaterializer(system).materializer

  /// By default akka stram components are fused i.e. they are run on same Actor
  val source = Source(1 to 1000)

  val complexFlow1 = Flow[Int].map { num =>
    Thread.sleep(1000)
    println("complexFlow1: " + java.time.Instant.now())
    num * 10
  }

  val complexFlow2 = Flow[Int].map {
    Thread.sleep(1000)
    println("complexFlow2: " + java.time.Instant.now())
    num => num + 1
  }

  val sink = Sink.foreach[Int](println)

  /// Running akka streams with Async boundaries provide better throughput as it runs on different Actors

  //source.via(complexFlow1).via(complexFlow2).to(sink).run()
  /*source.via(complexFlow1).async
    .via(complexFlow2).async
    .to(sink)
    .run()*/

  // Ordering guarantees

  Source(1 to 3)
    .map(num => {println(s"Flow1: $num"); num}).async
    .map(num => {println(s"Flow2: $num"); num}).async
    .map(num => {println(s"Flow3: $num"); num}).async
    .runWith(Sink.ignore)

}
