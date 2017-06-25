package reactive

import org.reactivestreams.{Publisher, Subscriber}

class MyPublisher extends Publisher[Int] {
  var subscriber: Subscriber[_>: Int] = _
  var isCancelled = false

  def cancel(): Unit = isCancelled = true

  def publish(n: Long): Unit = {
  var counter = 1
    try {
      for(c ← 1 to n.toInt) {
        if(!isCancelled)
          subscriber.onNext(counter)
        else throw new RuntimeException("Subscription got cancelled from downstream")
        counter += 1
      }

//      subscriber.onNext(10)  // Uncomment this line to try sending element to subscriber without demand
      subscriber.onComplete()
    } catch {
      case ex: Throwable ⇒ subscriber.onError(ex)
    }
  }

  override def subscribe(sub: Subscriber[_ >: Int]): Unit = {
    if(sub == null) throw new IllegalArgumentException("Subscriber cannot be null")
    subscriber = sub
    val subscription = new MySubscription[Int](this, sub.asInstanceOf[MySubscriber])
    sub.onSubscribe(subscription)
  }
}