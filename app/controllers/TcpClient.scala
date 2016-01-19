package controllers

import java.net.InetSocketAddress
import akka.actor.Actor
import akka.io.{IO, Tcp}
import akka.util.ByteString
import scala.concurrent.Promise
import Tcp._

class TcpClient(remote: InetSocketAddress, requestData: String, thePromise: Promise[String]) extends Actor {
    import context.system

    println("Connecting")
    IO(Tcp) ! Connect(remote)

    def receive = {
        case CommandFailed(_: Connect) =>
            println ("Connect failed")
            context stop self

        case c @ Connected(remote, local) =>
            println ("Connect succeeded")
            val connection = sender()
            connection ! Register(self)
            println("Sending request early")
            connection ! Write(ByteString(requestData))

            context become {
                case CommandFailed(w: Write) =>
                    println("Failed to write request.")
                case Received(data) =>
                    println("Received response.")
                    thePromise.success(data.decodeString("UTF-8"))
                case "close" =>
                    println("Closing connection")
                    connection ! Close
                case _: ConnectionClosed =>
                    println("Connection closed by server.")
                    context stop self
            }
        case _ => println("Something else is up.")
    }
}