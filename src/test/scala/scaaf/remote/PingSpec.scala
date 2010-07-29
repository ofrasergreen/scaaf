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
package scaaf.remote

import org.scalatest.WordSpec
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.MustMatchers
import scala.collection.mutable.Stack
import scaaf._
import exchange._
import cluster._
import space._
import test._
import remote._
import java.io.File
import org.newsclub.net.unix.AFUNIXSocket
import org.newsclub.net.unix.AFUNIXSocketAddress
import org.newsclub.net.unix.AFUNIXSocketException

/**
 * @author ofrasergreen
 *
 */
class PingSpec extends WordSpec with MustMatchers with BeforeAndAfterEach with InitSpec {
  var client: IPCClient = _
  
  override def beforeEach = {
    reset
    client = new IPCClient
  }
  
  override def afterEach {
    client.disconnect
  }
  
  "The echo service" should {
    "respond to an echo request with an echo reply" in {
      // Create the message
      val address = scaaf.exchange.service.Address.newAddress(LocalNode, classOf[EchoService])
      val data = Array[Byte](1, 2, 3, 4, 5)
      val request = EchoRequest(1, data)
      val message = Message(address, request)
      
      // Send the ping
      client.send(message)
      
      val reply = client.receive.asInstanceOf[Reply]
      val echoReply = reply.payload.asInstanceOf[EchoReply]
      echoReply.sequenceNumber must equal (1)
      echoReply.data must equal (data)
    }
  }
}