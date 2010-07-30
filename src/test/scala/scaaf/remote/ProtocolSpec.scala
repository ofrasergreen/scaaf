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
import scaaf.cluster._

import space._
import test._
import java.io.File
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import scaaf.GUID

import org.newsclub.net.unix.AFUNIXSocket
import org.newsclub.net.unix.AFUNIXSocketAddress
import org.newsclub.net.unix.AFUNIXSocketException

import sbinary._
import DefaultProtocol._
import Operations._
import JavaIO._


/**
 * @author ofrasergreen
 *
 */
class ProtocolSpec extends WordSpec with MustMatchers with BeforeAndAfterEach with InitSpec {
  override def beforeEach = reset
 
  "A simple echo request sent via the RemoteProtocol" should {
    val is = new ByteArrayInputStream(new Array[Byte](0))
    val os = new ByteArrayOutputStream(0)
    
    val fmt = RemoteProtocol.RemoteMessageFormat
    
    // Create a stream and send an echo request on it
    val data = Array[Byte](1, 2, 3, 4, 5)
    val address = Address.newFromDetails(scaaf.service.exchange.Exchange, LocalNode, classOf[EchoService].getCanonicalName.hashCode)
    val request = EchoRequest(1, data)
    
    // Create the message
    val message = Message(address, request)
    
    fmt.writes(os, message)

    "be 61 bytes long" in {
      os.size must equal (61)
    }
    
    val bytes = os.toByteArray
    
    "have a message class of -1561764041" in {
      bytes.slice(0, 4) must equal (Array[Byte](-94, -23, 95, 55))
    }
    
    "have a sequence number of 1" in {
      bytes.slice(48, 52) must equal (Array[Byte](0, 0, 0, 1))
    }
    
    "contain the sent data" in {
      bytes.slice(56, 61) must equal (Array[Byte](1, 2, 3, 4, 5))
    }
  }
}