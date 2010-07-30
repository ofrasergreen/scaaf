/* scaaf - The Scalable Application Framework
 *
 * Owen Fraser-Green me fecit (C) MMX
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package scaaf.ipc.uds.exchange

import scaaf.logging.Logging
import scaaf.remote.Frame
import scaaf.remote.Message
import scaaf.exchange.Subscribable
import scaaf.exchange.ReplyingSubscriber
import scaaf.exchange.ReplyableChannel

import java.net.Socket

import scala.actors.Actor
import scala.collection._
import Actor._

case class Connect(socket: Socket)
case class Disconnect(connection: Connection)

/**
 * @author ofrasergreen
 *
 */
object Exchange extends scaaf.exchange.Exchange with Subscribable[ReplyingSubscriber[Frame]] with Actor with Logging {  
  // The connections
  val connections = mutable.Set[Connection]()
  this.start
  
  // Start up the socket server
  Server.start 
  
  def act = loop {
    react {
      case Connect(socket) =>
        Log.debug("UDS client connection.")
        val connection = new Connection(socket)
        connection.start
        connections += connection
        // The client makes the first move
        connection ! Read
      case Disconnect(connection) =>
        Log.debug("Disconnecting UDS client.")
        connections -= connection
    }
  }
  
  def deliver(frame: Frame, channel: ReplyableChannel[Frame]) {
    // Pass everything along to the ISC exchange
    // TODO: This should act as an extension to the ISC exchange, not invoke it directly.
    scaaf.isc.exchange.Exchange.deliver(frame, channel)
  }
}