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
package scaaf.isc.exchange

import scaaf.logging.Logging
import scaaf.exchange.ReplyingSubscriber
import scaaf.exchange.Subscribable

import scaaf.remote.Frame
import scaaf.remote.Message
import scaaf.exchange.Replyable

/**
 * @author ofrasergreen
 *
 */
object Exchange extends scaaf.exchange.Exchange with Subscribable[ReplyingSubscriber[Envelope]] with Logging {
  def deliver(frame: Frame, channel: Replyable[Frame]) {
    frame match {
      case m: Message => 
        // TODO: Examine the node ID
        Log.debug("Routing message " + m.msgID)
        Log.debug("address " + m.address.addID)
        
        // TODO: Look up the exchange ClsID from the Exchange ID, then find 
        // it in the extension point register
        val exchange = subscribers(m.address.addID.cls)
        val chan = new Channel(channel)
        exchange.deliver(Envelope(m.address.addID.data, m.payload), chan)
    }
  }
}