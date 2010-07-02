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

import scaaf.kernel._

trait ReplySender {
  def sendReply(msg: Object)
}

trait Listener extends Service {
  protected def react: PartialFunction[Any, Unit]
  // FIXME: Do this better e.g. with Futures
  protected var sender: ReplySender = null
  
  def deliver(msg: Object, sender: ReplySender): Unit = {
    this.sender = sender
    react(msg)
  }
  
  def reply(msg: Object) = {
    sender.sendReply(msg)
  }
  
}

trait StatelessListener extends Listener with StatelessService