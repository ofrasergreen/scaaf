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

import scaaf.logging.Logging
import scaaf.remote.RemoteProtocol
import scaaf.remote.Frame

import java.net.Socket

import sbinary.JavaIO._

import scala.actors.Actor
import Actor._


case class Read()
case class Close()
case class Write(frame: Frame)

/**
 * @author ofrasergreen
 *
 */
class Connection(socket: Socket) extends Actor with Logging {
  val is = socket.getInputStream
  val os = socket.getOutputStream
     
        
  def act = loop {
    react {
      case Read =>
        try {
          val frame = RemoteProtocol.RemoteMessageFormat.reads(is)
          Log.debug("Received message (MsgID=" + frame.msgID + ")")
          Exchange.deliver(frame, new Channel(this))
        } catch {
          case eof: sbinary.EOF => 
            Log.warn("EOF when reading message.")
            this ! Close
        }
      case Write(frame) =>
        Log.debug("Sending message (MsgID=" + frame.msgID + ")")
        RemoteProtocol.RemoteMessageFormat.writes(os, frame)
      case Close =>
        // Close the streams, socket and exit the Actor
        is.close
        os.close
        socket.close
        Exchange ! Disconnect(this)
        exit
    }
  }
}