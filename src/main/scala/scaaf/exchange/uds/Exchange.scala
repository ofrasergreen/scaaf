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
package scaaf.exchange.uds

import scaaf.logging.Logging
import scaaf.remote.Frame
import scaaf.remote.Message
import scaaf.exchange.ExchangeRegistry

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
object Exchange extends scaaf.exchange.Exchange[Frame] with Actor with Logging {
  // Register
  ExchangeRegistry.register(this)
  
  // The connections
  val connections = mutable.Set[Connection]()
  
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
  
  def deliver(frame: Frame, channel: Channel) {
    // Pass everything along to the ISC exchange
    // TODO: This should act as an extension to the ISC exchange, not invoke it directly.
    scaaf.exchange.isc.Exchange.deliver(frame, channel)
  }
}