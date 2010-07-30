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
package scaaf.exchange

import scaaf.kernel.StatelessService

/**
 * @author ofrasergreen
 *
 */
trait StatelessListener[T] extends Listener[T] with StatelessService {
  protected def react: PartialFunction[Any, Unit]
  // FIXME: Do this better e.g. with Futures
  private var channel: Channel[T] = null
  
  def deliver(msg: T, channel: Channel[T]) {
    this.channel = channel
    react(msg)
  }
  
  def reply(msg: T) = {
    channel.reply(msg)
  }
}