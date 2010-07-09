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
import scala.actors.Actor
import Actor._
import com.rabbitmq._

class Receiver(connection: Connection, bind: Bind, exchange: Exchange) {
  private class Consumer(chan: client.Channel) extends client.DefaultConsumer(chan) {
    override def handleDelivery(consumerTag: String, env: client.Envelope, props: client.AMQP.BasicProperties, body: Array[Byte]) {
      // Get the envelope
      val envelope = new Envelope(env.getExchange, env.getRoutingKey, env.getDeliveryTag, env.isRedeliver)
      
      // Get the properties
      val properties = new Properties(
          Option(props.getAppId),
          Option(props.getClassId),    
          Option(props.getClassName),
          Option(props.getClusterId),
          Option(props.getContentEncoding),
          Option(props.getContentType),
          Option(props.getCorrelationId),
          if (props.getDeliveryMode != null) Some(props.getDeliveryMode.intValue) else None,
          Option(props.getExpiration),
          if (props.getHeaders != null) Map.empty ++ props.getHeaders else Map.empty,
          Option(props.getMessageId),
          if (props.getPriority != null) Some(props.getPriority.intValue) else None,
          Option(props.getReplyTo),
          Option(props.getTimestamp),
          Option(props.getType),
          Option(props.getUserId))
      
      receive(Message(envelope, properties, body))
      chan.basicAck(envelope.deliveryTag, false);
    }
  }
  
  val channel = {
    val chan = connection.conn.createChannel
    chan.exchangeDeclare(bind.exchangeName, "direct", true)
    chan.queueDeclare(bind.queueName, true)
    chan.queueBind(bind.queueName, bind.exchangeName, bind.routingKey)
    
    chan.basicConsume(bind.queueName, true, new Consumer(chan))
    
    chan
  }
  
  def receive(m: Message) {
    bind.listener.deliver(m, exchange)
  }
}