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

import java.io.ByteArrayOutputStream

import org.newsclub.net.unix.AFUNIXSocket
import org.newsclub.net.unix.AFUNIXSocketAddress
import org.newsclub.net.unix.AFUNIXSocketException

import sbinary._
import DefaultProtocol._
import Operations._
import JavaIO._
import java.io.InputStream
import java.io.OutputStream

/**
 * @author ofrasergreen
 *
 */
class IPCClient extends scaaf.ipc.uds.exchange.Configuration {
  val sock = AFUNIXSocket.newInstance
  
  private var is: InputStream = _
  private var os: OutputStream = _
  
  def send(frame: Frame) = RemoteProtocol.RemoteMessageFormat.writes(os, frame)
  def receive() = RemoteProtocol.RemoteMessageFormat.reads(is)
  
  def serverExists = socketFile.exists
  
  def connect {
    sock.connect(new AFUNIXSocketAddress(socketFile))
    
    // Connect the streams
    is = sock.getInputStream
    os = sock.getOutputStream
  }
  
  def disconnect {
    is.close
    os.close
    sock.close
  }
}