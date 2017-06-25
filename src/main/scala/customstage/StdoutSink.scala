package customstage

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.Sink
import akka.stream.{ActorMaterializer, Attributes, Inlet, SinkShape}
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler}

class StdoutSink() extends GraphStage[SinkShape[Int]] {
  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = {
    new GraphStageLogic(shape) {
      override def preStart(): Unit = pull(inlet)

      setHandler(inlet, new InHandler {
        override def onPush(): Unit = {
          val value = grab(inlet)
          if(value == 10)
            cancel(inlet)
          else {
            println(value)
            pull(inlet)
          }
        }
      })

      //Will be always called even in error scenarios. Like finally block of java. Put all clean up logic here.
      override def postStop(): Unit = {
        println("==============Poststop was called===================")
//        throw new RuntimeException("Exception in post stop") // Exceptions in post stop will be handled by akka
        //We can also delegate completion to built-iin mechanism
        super.postStop()
      }
    }
  }

  val inlet: Inlet[Int] = Inlet("In")
  override def shape: SinkShape[Int] = SinkShape(inlet)
}


object Main2 extends App {

  private implicit val actorSystem = ActorSystem()
  private implicit val materializer = ActorMaterializer()

  private val MySink: Sink[Int, NotUsed] = Sink.fromGraph(new StdoutSink())


  NumbersSource.mysource.runWith(MySink)

}
