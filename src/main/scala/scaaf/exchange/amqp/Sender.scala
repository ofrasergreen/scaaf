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

import scala.collection.JavaConversions._
import com.rabbitmq._
import scaaf.logging._
import com.rabbitmq.client.AMQP.BasicProperties

class Sender(connection: Connection) extends Logging {
  val chan = connection.conn.createChannel
  
  def send(message: Message) {
    Log.debug("Sending message of type '%s' to exchange '%s' with key '%s'".format(message.properties.messageType.getOrElse(""), message.envelope.exchange, message.envelope.routingKey))
    val properties = new BasicProperties(
        message.properties.contentType.orNull,
        message.properties.contentEncoding.orNull,
        message.properties.headers,
        message.properties.deliveryMode match {
          case Some(s) => s
          case None => null
        },
        message.properties.priority match {
          case Some(s) => s
          case None => null
        },
        message.properties.correlationId.orNull,
        message.properties.replyTo.orNull,
        message.properties.expiration.orNull,
        message.properties.messageId.orNull,
        message.properties.timestamp.orNull,
        message.properties.messageType.orNull,
        message.properties.userId.orNull,
        message.properties.appId.orNull,
        message.properties.clusterId.orNull        
        )
    
    chan.basicPublish(message.envelope.exchange, message.envelope.routingKey, properties, message.body)
  }
}