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

import scaaf.exchange.Replyable
import scaaf.space.Spacy
import scaaf.isc.exchange.Envelope

/**
 * @author ofrasergreen
 *
 */
class RemoteWriter(channel: Replyable[Envelope], error: Boolean) extends java.io.Writer {
  override def close {
    // Do nothing
  }
  
  override def flush {
    // Do nothing
  }
  
  override def write(cbuf: Array[Char], off: Int, len: Int) {
    val str = new String(cbuf, off, len)
    val msg = if (error) new Error(str) else new Output(str)
    channel.reply(Envelope(0, msg))
  }
}