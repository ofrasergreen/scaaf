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

import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

import org.newsclub.net.unix.AFUNIXServerSocket
import org.newsclub.net.unix.AFUNIXSocketAddress

import scaaf.logging.Logging
import scaaf.Configuration

/**
 * TODO: This should be rewritten as an exchange. The exchanges should be
 * managed as workers scaled to the number of execution units.
 * @author ofrasergreen
 *
 */
object IPCService extends Thread with IPCConfiguration with IPCSocket with Logging {
  val server = AFUNIXServerSocket.newInstance
  server.bind(new AFUNIXSocketAddress(socketFile))
  
  override def run() = {
    while(true) {
      Log.debug("Waiting for CLI socket connection...")
      
      var sock = server.accept
      Log.debug("Connected: " + sock)

      inputStream = sock.getInputStream
      outputStream = sock.getOutputStream

      val handshake = new String(receive)
      
      Log.debug("Client handshake: " + handshake)
      if (handshake == "CLI 0.1") {
        send("OK".getBytes)
        handleRequest
      } else {
        send("Invalid protocol version".getBytes())
      }

      inputStream.close()
      outputStream.close()

      sock.close()
    }
  }
  
  def handleRequest() {
    Log.debug(receive.toString)
    send("Hello!".getBytes)
  }
}