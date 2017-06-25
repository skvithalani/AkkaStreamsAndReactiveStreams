package reactive

import org.reactivestreams.{Processor, Subscriber, Subscription}

class MyProcessor extends Processor[Int, Int]{

  override def onError(t: Throwable): Unit = ???

  override def onComplete(): Unit = ???

  override def onNext(t: Int): Unit = ???

  override def onSubscribe(s: Subscription): Unit = ???

  override def subscribe(s: Subscriber[_ >: Int]): Unit = ???
}
