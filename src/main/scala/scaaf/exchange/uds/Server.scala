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
package scaaf.exchange.uds

import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import scaaf.GUID

import org.newsclub.net.unix.AFUNIXServerSocket
import org.newsclub.net.unix.AFUNIXSocketAddress

import scaaf.logging.Logging
import scaaf.space.Space

import sbinary._
import DefaultProtocol._
import Operations._
import JavaIO._

/**
 * @author ofrasergreen
 *
 */
object Server extends Thread with Configuration with Logging {
  val server = AFUNIXServerSocket.newInstance
  server.bind(new AFUNIXSocketAddress(socketFile))
  
  override def run() = {
    while(true) {
      Log.debug("Waiting for socket connection...")
      
      Exchange ! Connect(server.accept)
    }
  }
}