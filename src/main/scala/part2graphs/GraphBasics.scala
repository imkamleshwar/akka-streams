package part2graphs

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, RunnableGraph, Sink, Source, Zip}
import akka.stream.{ClosedShape, Materializer, SystemMaterializer}

object GraphBasics extends App {

  implicit val system: ActorSystem = ActorSystem("GraphBasics")
  implicit val materializer: Materializer = SystemMaterializer(system).materializer

  val input = Source(1 to 100)
  val doubler = Flow[Int].map(x => x * 2)
  val multiplier = Flow[Int].map(x => x * 10)
  val output = Sink.foreach[(Int, Int)](println)

  val graph = RunnableGraph.fromGraph(
    GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] => // builder -> It is a mutable data structure
      import GraphDSL.Implicits._
      val broadcast = builder.add(Broadcast[Int](2)) // fan-out operator
      val zip = builder.add(Zip[Int, Int]) // fan-in operator

      input ~> broadcast
      broadcast.out(0) ~> doubler ~> zip.in0
      broadcast.out(1) ~> multiplier ~> zip.in1
      zip.out ~> output

      ClosedShape // Freeze the builder shaper

    } // This should be a graph

  )
  // create shape => create graph => create runnable graph => run graph and materializer

  graph.run()

}
