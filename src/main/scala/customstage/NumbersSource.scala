package customstage

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.Source
import akka.stream._
import akka.stream.stage.{GraphStage, GraphStageLogic, OutHandler}

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

class NumbersSource extends GraphStage[SourceShape[Int]] {
  val numberSource: Outlet[Int] = Outlet[Int]("NumberSource")
  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = {
    new GraphStageLogic(shape) {
      //Do not provide state to class but inside createLogic only
      private var counter = 1

      setHandler(numberSource, new OutHandler {
        override def onPull(): Unit = {
          push(numberSource, counter)
//          push(numberSource, counter)   // On pushing twice GraphInterpreter will error out. Not possible in reactive streams
          counter += 1
        }
      })
    }
  }

  override def shape: SourceShape[Int] = SourceShape(numberSource)
}

object NumbersSource {
  def apply(): NumbersSource = new NumbersSource()
//  def apply(start: Int, end: Int) = new Num

  private val numbersSource: Graph[SourceShape[Int], NotUsed] = NumbersSource()

  val mysource: Source[Int, NotUsed] = Source.fromGraph(numbersSource)

}

object Main1 extends App {
  private implicit val actorSystem = ActorSystem()
  private implicit val materializer = ActorMaterializer()
//  mysource.runForeach(println)

  private val eventualInt = NumbersSource.mysource.buffer(1, OverflowStrategy.backpressure).take(10).runFold(0)(_ + _)

  println(Await.result(eventualInt, 10.seconds))


  private val eventualInt1 = NumbersSource.mysource.take(100).runFold(0)(_ + _)
  println(Await.result(eventualInt1, 10.seconds))


}