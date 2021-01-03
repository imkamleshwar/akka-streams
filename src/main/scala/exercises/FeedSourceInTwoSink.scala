package exercises

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.{ClosedShape, SystemMaterializer}
import akka.stream.scaladsl.{Broadcast, GraphDSL, RunnableGraph, Sink, Source}

object FeedSourceInTwoSink extends App {

  implicit val system = ActorSystem("FeedSourceInTwoSink")
  implicit val materializer = SystemMaterializer(system).materializer

  val input = Source(1 to 100)
  val sink1 = Sink.foreach(println)
  val sink2 = Sink.foreach(println)

  val graph = RunnableGraph.fromGraph(
    GraphDSL.create() {implicit builder: GraphDSL.Builder[NotUsed] =>
      import GraphDSL.Implicits._
      val broadcast = builder.add(Broadcast[Int](2))

      input ~> broadcast ~> sink1
      broadcast ~> sink2
//      broadcast.out(0) ~> sink1
//      broadcast.out(1) ~> sink2

      ClosedShape
    }
  )

  graph.run()

}
