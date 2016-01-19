import java.net.InetSocketAddress

import akka.actor.{ActorSystem, Props}
import controllers._
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import scala.concurrent._
import org.scalatest.concurrent.ScalaFutures
/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
class ApplicationSpec extends PlaySpec with OneAppPerSuite {

  "Application" should {
    "Test client" in {
      val host = "example.com"
      val sys = ActorSystem.create("BibhasActor")
      val p = Promise[String]()
      val c = sys.actorOf(
        Props(classOf[TcpClient],
          new InetSocketAddress(host, 80),
          s"GET / HTTP/1.1\r\nHost: ${host}\r\nAccept: */*\r\n\r\n", p))

      ScalaFutures.whenReady(p.future) {data =>
        println(data)
        c ! "close"
      }
    }
  }
}
