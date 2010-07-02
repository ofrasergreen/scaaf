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

case class Envelope (
    val exchange: String, 
    val routingKey: String,
    val deliveryTag: Long,  
    val isRedeliver: Boolean)
    
class OutboundEnvelope(exchange: String, routingKey: String) extends Envelope(exchange, routingKey, 0, false)

case class Properties(
    val appId: Option[String],
    val classId: Option[Int],
    val className: Option[String],
    val clusterId: Option[String],
    val contentEncoding: Option[String],
    val contentType: Option[String],
    val correlationId: Option[String],
    val deliveryMode: Option[Int],
    val expiration: Option[String],
    val headers: Map[String, Object],
    val messageId: Option[String],
    val priority: Option[Int],
    val replyTo: Option[String],
    val timestamp: Option[java.util.Date],
    val messageType: Option[String],
    val userId: Option[String])      
  
case class Message(envelope: Envelope, properties: Properties, body: Array[Byte])

object EmptyProperties extends Properties(
    None,
    None,
    None,
    None,
    None,
    None,
    None,
    None,
    None,
    Map.empty,
    None,
    None,
    None,
    None,
    None,
    None)
