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
package scaaf.cli.exchange

import org.scalatest.WordSpec
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.MustMatchers
import scala.collection.mutable.Stack
import scaaf._
import space._
import test._
import java.io.File
import scaaf.remote._

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
    "accept a help request" in {
      // Create the message
      val address = scaaf.cli.exchange.Exchange.address
      val request = new Request(Array[String]("help"))
      val message = Message(address, request)
      
      // Send the CLI request
      client.send(message)
      
      var done = false
      do {
        client.receive match {
          case r: Reply => {
            r.payload match {
              case Output(s) =>
                print(s)
              case _ =>
                Log.error("Unrecognized payload.")
            }
          }
          case e: End => done = true
        }
      } while (!done)     
    }
  }
}