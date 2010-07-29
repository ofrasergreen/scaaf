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

import scaaf.space.Spacy
import scaaf.exchange.Address

import scaaf.GUID
import scaaf.AddID

/**
 * @author ofrasergreen
 *
 */
trait Frame extends Spacy {
  val msgID = GUID.newMsgID(this.getClass)
}

trait Addressable {
  val address: Address
}

trait Payloaded {
  val payload: Spacy
}

trait ReplyOrEnd

case class Message(address: Address, payload: Spacy) extends Frame with Payloaded with Addressable
case class Reply(payload: Spacy) extends Frame with Payloaded with ReplyOrEnd
case class Error(payload: Spacy) extends Frame with Payloaded
case class End() extends Frame with ReplyOrEnd