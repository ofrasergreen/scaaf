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

import scaaf.space.Spacy
import scaaf.remote.Reply
import scaaf.remote.End


/**
 * @author ofrasergreen
 *
 */
class Channel(connection: Connection) extends scaaf.exchange.Channel[Spacy] {
  def reply(payload: Spacy) = connection ! Write(new Reply(payload))
  def close() = connection ! Write(new End())
}