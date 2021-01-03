package part1primer

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.{Materializer, OverflowStrategy, SystemMaterializer}

object BackPressureBasics extends App {

  implicit val system: ActorSystem = ActorSystem("BackPressureBasics")
  implicit val materalizer: Materializer = SystemMaterializer(system).materializer

  val fastSource = Source(1 to 100)
  val slowSink = Sink.foreach[Int] { x =>
    Thread.sleep(1000)
    println(s"Sink: $x")
  }
  //fastSource.to(slowSink).run()
  // NOT Back pressure

  //fastSource.async.to(slowSink).run()
  //Back Pressure

  val aFlow = Flow[Int].map { x =>
    println(s"value in flow: $x")
    x + 1
  }

  //fastSource.via(aFlow).to(slowSink).run()

  // Below is back pressure in action
  // Since the source is much faster and sink is slow
  // sink sends back pressure signal to upstream
  // once upstream(aFlow in this case) receives the backpressure, it buffers the element
  // default buffer is 16
  fastSource.async
    .via(aFlow).async
    .to(slowSink)
  //.run()

  /*
   reactions to backpressure (in order):
   - try to slow down if possible
   - buffer elements until there's more demand
   - drop down elements from the buffer if it overflows
   - tear down/kill the whole stream (failure)
  */

  val bufferedFlow = aFlow.buffer(10, overflowStrategy = OverflowStrategy.dropHead)
  fastSource.async
    .via(bufferedFlow).async
    .to(slowSink)
  //    .run()

  /*
   1-16: nobody is backpressured
   17-26: flow will buffer, flow will start dropping at the next element
   26-1000: flow will always drop the oldest element
     => 991-1000 => 992 - 1001 => sink
  */

  /*
    overflow strategies:
    - drop head = oldest
    - drop tail = newest
    - drop new = exact element to be added = keeps the buffer
    - drop the entire buffer
    - backpressure signal
    - fail
   */

  // throttling

  import scala.concurrent.duration._

  fastSource.throttle(10, 1 second).runWith(Sink.foreach(println))

}
