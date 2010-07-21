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
package scaaf.cli

import org.scalatest.WordSpec
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.MustMatchers
import scala.collection.mutable.Stack
import scaaf._
import space._
import test._
import java.io.File

import org.newsclub.net.unix.AFUNIXSocket
import org.newsclub.net.unix.AFUNIXSocketAddress
import org.newsclub.net.unix.AFUNIXSocketException

/**
 * @author ofrasergreen
 *
 */
class CLIProtocolSpec extends WordSpec with MustMatchers with BeforeAndAfterEach with InitSpec {
  var client: IPCClient = _
  
  override def beforeEach = {
    reset
    client = new IPCClient
  }
  
  override def afterEach {
    client.disconnect
  }
  
  "The CLI server" should {
    "expect the client to send CLI <version> and return OK" in {
      client.handshake
    }
    
    "reject an invalid version number" in {
      client.send("CLI 0.123456789".getBytes)
      val output = new String(client.receive)
      assert(output === "Invalid protocol version")
    }
    
    "accept a help request" in {
      client.handshake
      client.send("help".getBytes)
    }
  }
}