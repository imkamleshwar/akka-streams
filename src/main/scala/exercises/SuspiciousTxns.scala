package exercises

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, RunnableGraph, Sink, Source}
import akka.stream.{ClosedShape, FanOutShape2, SystemMaterializer}

import java.util.Date

object SuspiciousTxns extends App {

  implicit val system = ActorSystem("SuspiciousTxns")
  implicit val materializer = SystemMaterializer(system).materializer

  case class Transaction(txnId: String, sender: String, recipient: String, amount: Int, data: Date)

  val sourceTxn = Source(List(
    Transaction("1111111", "Jason", "Bourne", 1000, new Date),
    Transaction("2222222", "HHH", "John", 10000, new Date),
    Transaction("3333333", "Steph", "Vince", 100000, new Date),
    Transaction("4444444", "Vince", "Jack", 20000, new Date)
  ))

  val normalBankProcess = Sink.foreach[Transaction](println)
  val suspiciousTxnGetter = Sink.foreach[String](txnId => println(s"Suspicious Transaction ID: $txnId"))

  val suspiciousTxnGetterGraph = GraphDSL.create()({ implicit builder: GraphDSL.Builder[NotUsed] =>
    import GraphDSL.Implicits._

    val broadcast = builder.add(Broadcast[Transaction](2))
    val suspiciousTxn = builder.add(Flow[Transaction].filter(txn => txn.amount > 10000))
    val txnIdExtractor = builder.add(Flow[Transaction].map(_.txnId))

    broadcast.out(0) ~> suspiciousTxn ~> txnIdExtractor

    new FanOutShape2(broadcast.in, broadcast.out(1), txnIdExtractor.out)
  }
  )

  val suspiciousTxnsRunnableGraph = RunnableGraph.fromGraph(
    GraphDSL.create() { implicit builder =>
      import GraphDSL.Implicits._

      val suspiciousTxnsShape = builder.add(suspiciousTxnGetterGraph)

      sourceTxn ~> suspiciousTxnsShape.in
      suspiciousTxnsShape.out0 ~> normalBankProcess
      suspiciousTxnsShape.out1 ~> suspiciousTxnGetter

      ClosedShape
    }
  )

  suspiciousTxnsRunnableGraph.run()
}
