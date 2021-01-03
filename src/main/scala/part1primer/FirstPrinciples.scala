package part1primer

import akka.actor.ActorSystem
import akka.stream.{Materializer, SystemMaterializer}
import akka.stream.scaladsl.{Flow, Sink, Source}

import scala.concurrent.Future

object FirstPrinciples extends App {

  val system = ActorSystem("FirstPrinciples")
  implicit val systemMaterializer: Materializer = SystemMaterializer(system).materializer
  // materializer is an object which allocates right resources to running akka stream

  // Source
  val source = Source(1 to 10)
  //sink
  val sink = Sink.foreach[Int](println)

  //  val runnableGraph = source.to(sink)
  //
  //  runnableGraph.run()

  // Flows
  val flow = Flow[Int].map(x => x * x)

  //new runnable graph
  //source.via(flow).to(sink).run()

  // Nulls are NOT ALLOWED
  //  val illegalSource = Source.single[String](null)
  //  illegalSource.to(Sink.foreach(println)).run()
  val legalSource = Source.single[Option[String]](None)
  legalSource.to(Sink.foreach(println)).run()

  /// various kinds of sources
  val finiteSource = Source.single(1)
  val anotherFiniteSource = Source(List(1,2,3,4,5))
  val emptySource = Source.empty[String]
  val infiniteSource =  Source(Stream.from(1)) // akka stream is different from "collection" Stream
  import scala.concurrent.ExecutionContext.Implicits.global
  val futureSink = Source.future(Future(42))

  /// type of sink
  val boringSink = Sink.ignore
  val foreachSink = Sink.foreach[Int](println)
  val headSink = Sink.head[Int]
  val foldSink = Sink.fold[Int, Int](0)((x, y) => x+ y )

  /// flow -> usually mapped to collection
  val mapFlow = Flow[Int].map(x => x * 2)
  val takeFlow = Flow[Int].take(5) // turns this into finite stream
  // other are filter, drop
  // do not have flatMap


}
