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

import java.nio.channels.Selector
import java.nio.channels.spi.SelectorProvider
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.net.InetSocketAddress;
import java.nio.ByteBuffer

object SelectingRunner extends Thread {
  val selector = initSelector(5938);

  override def run() = {
    val dispatcher = new Dispatcher()
    var i = 0

    dispatcher.start()

    while (true) {
      selector.select()

      val selectedKeysItr = selector.selectedKeys().iterator()

      while (selectedKeysItr.hasNext()) {
        val key = selectedKeysItr.next().asInstanceOf[SelectionKey]
        selectedKeysItr.remove()

        if (key.isValid) {
          if (key.isAcceptable) {
            accept(key)
          } else if (key.isReadable) {
            i += 1
            dispatcher ! Read(key.channel().asInstanceOf[SocketChannel], i)
            key.cancel
          }
        }
      }
    }
  }

  def accept(key:SelectionKey) = {
    val serverSocketChannel = key.channel().asInstanceOf[ServerSocketChannel]
    val socketChannel = serverSocketChannel.accept

    socketChannel.configureBlocking(false)
    socketChannel.register(selector, SelectionKey.OP_READ)
  }

  def initSelector(port: Int): Selector = {
    val socketSelector = SelectorProvider.provider().openSelector()
    val serverChannel = ServerSocketChannel.open()
    val isa = new InetSocketAddress(port)
    serverChannel.configureBlocking(false)
    serverChannel.socket().bind(isa)
    serverChannel.register(socketSelector, SelectionKey.OP_ACCEPT)
    return socketSelector
  }
}