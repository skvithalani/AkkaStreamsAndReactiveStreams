package reactive

import org.reactivestreams.{Subscriber, Subscription}

class MySubscriber extends Subscriber[Int]{
  var subscription: MySubscription[Int] = _
  override def onError(t: Throwable): Unit = {
    println("Error")
    subscription.cancel()
    throw t
  }

  override def onComplete(): Unit = {
    println("subscription completed successfully")
    subscription.cancel()
  }

  override def onNext(t: Int): Unit = {
    println(s"== $t")
  }

  override def onSubscribe(sub: Subscription): Unit = {
    println("Subscribed")
    subscription = sub.asInstanceOf[MySubscription[Int]]
    sub.request(10)
  }
}

object Main extends App {
  private val myPublisher = new MyPublisher()
  private val mySubscriber = new MySubscriber()
  myPublisher.subscribe(mySubscriber)
//  myPublisher.subscribe(new MySubscriber())
}