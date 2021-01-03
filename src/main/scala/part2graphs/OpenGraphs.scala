package part2graphs

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Broadcast, Concat, GraphDSL, Sink, Source}
import akka.stream.{Materializer, SinkShape, SourceShape, SystemMaterializer}

object OpenGraphs extends App {

  implicit val system: ActorSystem = ActorSystem("OpenGraphs")
  implicit val materializer: Materializer = SystemMaterializer(system).materializer

  /*
  crate a composite source that concatenates 2 sources
  i.e. emit ALL element of first source and then emit ALL elements of second source
   */

  val firstSource = Source(1 to 20)
  val secondSource = Source(50 to 100)

  val sourceGraph = Source.fromGraph(
    GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>
      import GraphDSL.Implicits._

      val concat = builder.add(Concat[Int](2))

      firstSource ~> concat
      secondSource ~> concat

      SourceShape(concat.out)
    }
  )

  //sourceGraph.to(Sink.foreach(println)).run()

  /*
  complex sink
   */
  val sink1 = Sink.foreach[Int](x => println(s"Meaning of life 1: $x"))
  val sink2 = Sink.foreach[Int](x => println(s"Meaning of life 2: $x"))

  val complexSink = Sink.fromGraph(
    GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>
      import GraphDSL.Implicits._
      val broadcast = builder.add(Broadcast[Int](2))

      broadcast ~> sink1
      broadcast ~> sink2

      SinkShape(broadcast.in)
    }
  )
  firstSource.to(complexSink).run()
}
