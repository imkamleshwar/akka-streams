package exercises

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Balance, GraphDSL, Merge, RunnableGraph, Sink, Source}
import akka.stream.{ClosedShape, Materializer, SystemMaterializer}

object MergeAndBalance extends App {

  implicit val system: ActorSystem = ActorSystem("MergeAndBalance")
  implicit val materializer: Materializer = SystemMaterializer(system).materializer

  val fastSource = Source(1 to 1000)
  val slowSource = Source(1 to 1000).map { num =>
    Thread.sleep(1000)
    num + 10
  }
  val sink1 = Sink.fold[Int, Int](0)((count, _) => {
    println(s"sink 1 number of element: $count")
    count + 1
  })
  val sink2 = Sink.fold[Int, Int](0)((count, _) => {
    println(s"sink 2 number of element: $count")
    count + 1
  })

  val graph = RunnableGraph.fromGraph(
    GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>

      import GraphDSL.Implicits._
      val merge = builder.add(Merge[Int](2))
      val balance = builder.add(Balance[Int](2))

      /*fastSource ~> merge
      slowSource ~> merge
      merge ~> balance
      balance.out(0) ~> sink1
      balance.out(1) ~> sink2*/
      fastSource ~> merge ~> balance ~> sink1
      slowSource ~> merge
      balance ~> sink2

      ClosedShape
    }
  )

  graph.run()

}
