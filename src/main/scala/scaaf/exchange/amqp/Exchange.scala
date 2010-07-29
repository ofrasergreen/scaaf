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

package scaaf.exchange.amqp

import scaaf.exchange._
import scaaf.logging._
import scala.actors.Actor
import Actor._

case class Bind(
    val listener: Listener[Message], 
    val exchangeName: String, 
    val exchangeType: String, 
    val queueName: String, 
    val routingKey: String)
case class Incoming(message: Message)
case class DeclareQueue(name: String, durable: Boolean)
case class DeclareExchange(name: String, exchangeType: String, durable: Boolean)
case class BindQueue(queueName: String, exchangeName: String, routingKey: String)

class Exchange(connection: Connection) extends scaaf.exchange.Exchange[Message] with Actor with Logging {  
  private val thesender = new Sender(connection)
  private var receivers: List[Receiver] = List.empty
  private val channel = connection.conn.createChannel 
  
  Log.debug("Creating AMQP exchange to %s:%s".format(connection.hostName, connection.portNumber))
  
  def act = loop {
    react {
      case b: Bind =>
        receivers = new Receiver(connection, b, this) :: receivers
        Log.debug("Receivers now contains: " + receivers)
      case m: Message =>
        thesender.send(m)
      case DeclareQueue(name, durable) =>
        channel.queueDeclare(name, durable) 
      case DeclareExchange(name, exchangeType, durable) =>
        channel.exchangeDeclare(name, exchangeType, true)
      case BindQueue(queueName, exchangeName, routingKey) => channel.queueBind(queueName, exchangeName, routingKey) 
    }
  }
}