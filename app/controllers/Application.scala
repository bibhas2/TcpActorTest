package controllers

import java.net.InetSocketAddress
import akka.actor.{Props, ActorSystem}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc._
import scala.concurrent.Promise

class Application extends Controller {

  def index = Action.async {
    val host = "example.com"
    val promise = Promise[String]()
    val props = Props(classOf[TcpClient],
      new InetSocketAddress(host, 80),
      s"GET / HTTP/1.1\r\nHost: ${host}\r\nAccept: */*\r\n\r\n", promise)

    //Discover the actor
    val sys = ActorSystem.create("MyActorSystem")
    val tcpActor = sys.actorOf(props)

    //Convert the promise to Future[Result]
    promise.future map { data =>
        tcpActor ! "close"
        Ok(data)
    }
  }

}
