package exercises

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, GraphDSL, Sink, Source}
import akka.stream.{FlowShape, SystemMaterializer}

object OpenGraphsComplexFlows extends App {

  implicit val system = ActorSystem("OpenGraphsComplexFlows")
  implicit val materializer = SystemMaterializer(system).materializer

  val flow1 = Flow[Int].map(num => num + 1)
  val flow2 = Flow[Int].map(num => num * 10)

  val flowGraph = Flow.fromGraph(
    GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>
      import GraphDSL.Implicits._

      // ~> operator works with Shapes
      val flow1Shape = builder.add(flow1)
      val flow2Shape = builder.add(flow2)

      flow1Shape ~> flow2Shape

      FlowShape(flow1Shape.in, flow2Shape.out)
    }
  )

  Source(1 to 10).via(flowGraph).to(Sink.foreach(println)).run()

}
