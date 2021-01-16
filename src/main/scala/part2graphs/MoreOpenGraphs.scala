package part2graphs

import akka.actor.ActorSystem
import akka.stream.scaladsl.{GraphDSL, RunnableGraph, Sink, Source, ZipWith}
import akka.stream.{ClosedShape, Materializer, SystemMaterializer, UniformFanInShape}

object MoreOpenGraphs extends App {

  implicit val system: ActorSystem = ActorSystem("MoreOpenGraphs")
  implicit val materializer: Materializer = SystemMaterializer(system).materializer

  /*
    Example: Max3 operator
    - 3 input of type int
    - gives the maximum of the 3
   */

  val max3StaticGraph = GraphDSL.create(){implicit builder =>
    import GraphDSL.Implicits._

    val max1 = builder.add(ZipWith[Int, Int, Int]((x, y)=> Math.max(x, y)))
    val max2 = builder.add(ZipWith[Int, Int, Int]((x, y)=> Math.max(x, y)))

    max1.out ~> max2.in0

    // this is called uniform as the these receive the element of the same type
    UniformFanInShape(max2.out, max1.in0, max1.in1, max2.in1)
  }

  val source1 = Source(1 to 10)
  val source2 = Source(1 to 10).map(_ => 5)
  val source3 = Source(10 to 1 by -1)

  val maxSink = Sink.foreach[Int](num => println(s"max num is: $num"))

  val max3RunnableGraph = RunnableGraph.fromGraph(
    GraphDSL.create() { implicit builder =>
      import GraphDSL.Implicits._

      val max3Shape = builder.add(max3StaticGraph)

      source1 ~> max3Shape.in(0)
      source2 ~> max3Shape.in(1)
      source3 ~> max3Shape.in(2)

      max3Shape.out ~> maxSink

      ClosedShape
    }
  )

  max3RunnableGraph.run()

}
