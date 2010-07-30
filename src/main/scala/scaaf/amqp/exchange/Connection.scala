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
package scaaf.amqp.exchange

import scaaf.logging.Logging

import com.rabbitmq._
import client._

import scala.collection.JavaConversions._
import scala.actors.Actor
import Actor._

//case class Incoming(message: Message)
case class DeclareQueue(name: String, durable: Boolean)
case class DeclareExchange(name: String, exchangeType: String, durable: Boolean)
case class BindQueue(queueName: String, exchangeName: String, routingKey: String)
case class Publish(address: Address, body: MessageBody)

/**
 * @author ofrasergreen
 *
 */
class Connection(channel: client.Channel) extends Actor with Logging {
 def act = loop {
    react {
      case DeclareQueue(name, durable) =>
        channel.queueDeclare(name, durable) 
      case DeclareExchange(name, exchangeType, durable) =>
        channel.exchangeDeclare(name, exchangeType, true)
      case BindQueue(queueName, exchangeName, routingKey) => 
        channel.queueBind(queueName, exchangeName, routingKey) 
      case Publish(address, body) =>
        val properties = new AMQP.BasicProperties(
          address.properties.contentType.orNull,
          address.properties.contentEncoding.orNull,
          address.properties.headers,
          address.properties.deliveryMode match {
            case Some(s) => s
            case None => null
          },
          address.properties.priority match {
            case Some(s) => s
            case None => null
          },
          address.properties.correlationId.orNull,
          address.properties.replyTo.orNull,
          address.properties.expiration.orNull,
          address.properties.messageId.orNull,
          address.properties.timestamp.orNull,
          address.properties.messageType.orNull,
          address.properties.userId.orNull,
          address.properties.appId.orNull,
          address.properties.clusterId.orNull        
        )
    
        channel.basicPublish(address.envelope.exchange, address.envelope.routingKey, properties, body.bytes)
    }
 }
}