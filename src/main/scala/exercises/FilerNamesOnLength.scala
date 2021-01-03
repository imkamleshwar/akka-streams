package exercises

import akka.actor.ActorSystem
import akka.stream.{Materializer, SystemMaterializer}
import akka.stream.scaladsl.Source

object FilerNamesOnLength extends App {

  val nameList = List("Jason", "Bourne", "Joe", "Dwayne", "John", "Cena", "Rock", "HHH")

  val system = ActorSystem("FilerNames")
  implicit val systemMaterializer: Materializer = SystemMaterializer(system).materializer

  Source(nameList)
    .filter(name => name.length > 5)
    .take(2)
    .runForeach(println)
}
