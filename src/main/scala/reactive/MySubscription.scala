package reactive

import org.reactivestreams.Subscription

class MySubscription[V](publisher: MyPublisher, subscriber: MySubscriber) extends Subscription{
  var canRequest = true
  override def cancel(): Unit = {
    canRequest = false
    println("cancelling subscription")
    publisher.cancel()
  }

  override def request(n: Long): Unit = {
    if(canRequest) {
      println(s"requesting $n from publisher")
      publisher.publish(n)
    } else println("This subscription is cancelled")
  }
}
