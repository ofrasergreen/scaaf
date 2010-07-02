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

import scala.actors.Actor
import scala.actors.Actor._

import java.nio.channels.Selector
import java.nio.channels.spi.SelectorProvider
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.net.InetSocketAddress;
import java.nio.ByteBuffer

case class Idle(worker: ServerWorker)
case class Read(channel:SocketChannel, id: Int)

class ServerWorker(val id: Int, val dispatcher: Dispatcher) extends Actor {
  private val readBuffer = ByteBuffer.allocate(8192);
  
  def act() {
    loop {
      react {
        case Read(channel, id) =>
          // Clear the read buffer and read into it
          val numRead = channel.read(readBuffer)
          if (numRead > -1) {
            val request = new String(readBuffer.array)
            channel.write(ByteBuffer.wrap(("You said: " + request).getBytes))
            //channel.close()
            dispatcher ! Idle(this)
          }
      }
    }
  }

  override def hashCode(): Int = id

  override def equals(other: Any): Boolean = other match {
    case that: ServerWorker => this.id == that.id
    case _                  => false
  }
}

