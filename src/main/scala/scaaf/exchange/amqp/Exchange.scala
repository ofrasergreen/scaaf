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

import scaaf.exchange.Listener
import scaaf.logging.Logging

import com.rabbitmq._
import client._

class Exchange(hostName: String, 
    portNumber: Int, 
    userName: String,
    password: String,
    virtualHost: String) extends scaaf.exchange.Exchange[Message, ListenerMessage] with Logging {
  
  // Connect to the AMQP server
  val conn = {
    val params = new ConnectionParameters()
    params.setUsername(userName)
    params.setPassword(password)
    params.setVirtualHost(virtualHost)
    params.setRequestedHeartbeat(0)
    val factory = new ConnectionFactory(params)
    factory.newConnection(hostName, portNumber)
  }
    
  Log.debug("Created AMQP exchange to server %s:%s".format(hostName, portNumber))

  // Create a channel and start the actor
  private val connection = new Connection(conn.createChannel)
  connection.start  
    
  def send(message: Message) {
    Log.debug("Sending message of type '%s' to exchange '%s' with key '%s'".format(message.properties.messageType.getOrElse(""), message.envelope.exchange, message.envelope.routingKey))
    connection ! Publish(message)
  }
  
  def deliver(msg: ListenerMessage, channel: scaaf.exchange.Channel[ListenerMessage]) {
    listeners(msg.listenerID).deliver(msg.message, new Channel(this))
  }
  
  def register(
      exchangeName: String, 
      exchangeType: String, 
      queueName: String, 
      routingKey: String, 
      listener: Listener[Message]) {
    // register the listener in the normal way
    register(listener)

    // Create a channel to consume on
    val chan = conn.createChannel
    chan.exchangeDeclare(exchangeName, "direct", true)
    chan.queueDeclare(queueName, true)
    chan.queueBind(queueName, exchangeName, routingKey)
    
    // Create a consumer
    chan.basicConsume(queueName, true, new Consumer(chan, this, listener.getClass.getCanonicalName.hashCode))
  }
}