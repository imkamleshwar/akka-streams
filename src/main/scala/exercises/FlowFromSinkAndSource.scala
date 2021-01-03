package exercises

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.{ClosedShape, FlowShape, SystemMaterializer}
import akka.stream.scaladsl.{Flow, GraphDSL, RunnableGraph, Sink, Source}

object FlowFromSinkAndSource extends App {

  implicit val system = ActorSystem("FlowFromSinkAndSource")
  implicit val materializer = SystemMaterializer(system).materializer

  /*
  +----------------------------------------------+
  | Resulting Flow[I, O, NotUsed]                |
  |                                              |
  |  +---------+                  +-----------+  |
  |  |         |                  |           |  |
I ~~>| Sink[I] | [no-connection!] | Source[O] | ~~> O
  |  |         |                  |           |  |
  |  +---------+                  +-----------+  |
  +----------------------------------------------+
   */

  val sink = Sink.foreach[Int](x => println(s"values in sink: $x"))
  val source = Source(50 to 70)

  val flowFromSinkAndSourceGraph = Flow.fromGraph(
    GraphDSL.create(){implicit builder: GraphDSL.Builder[NotUsed] =>

      val sinkShape = builder.add(sink)
      val sourceShape = builder.add(source)

      FlowShape(sinkShape.in, sourceShape.out)
    }
  )

  //Source(1 to 10).via(flowFromSinkAndSourceGraph).runForeach(println)
  Source(1 to 10).via(flowFromSinkAndSourceGraph).runWith(Sink.foreach(println))

  /*def fromSinkAndSource[A, B](sink: Sink[A, _], source: Source[B, _]): Flow[A, B, _] =
    Flow.fromGraph(
      GraphDSL.create(){implicit builder: GraphDSL.Builder[NotUsed] =>

        val sinkShape = builder.add(sink)
        val sourceShape = builder.add(source)

        FlowShape(sinkShape.in, sourceShape.out)
      }
    )*/

  //val flow = Flow.fromSinkAndSource(Sink.foreach[String](println), Source(1 to 10))
  //Source(List("a", "b", "c")).via(flow).runForeach(println)

  //val flow = Flow.fromSinkAndSource(Sink.ignore, Source(1 to 10))
  //Source(1 to 10).via(flow).runForeach(println)

}
