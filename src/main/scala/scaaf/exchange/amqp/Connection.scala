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

import scala.actors.Actor
import Actor._
import com.rabbitmq.client._

case class Connection(val hostName: String, 
    val portNumber: Int, 
    val userName: String,
    val password: String,
    val virtualHost: String) {

  val conn = {
    val params = new ConnectionParameters()
    params.setUsername(userName)
    params.setPassword(password)
    params.setVirtualHost(virtualHost)
    params.setRequestedHeartbeat(0)
    val factory = new ConnectionFactory(params)
    factory.newConnection(hostName, portNumber)
  }

//  def getChannel(exchangeName: String, exchangeType: String, queueName: String, 
//      routingKey: String): Channel = {
//    val channel = new Channel(conn, exchangeName, exchangeType, queueName, routingKey)
//    channel.start()
//    return channel
//  }

}