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
package scaaf.exchange.isc

import scaaf.remote.Frame
import scaaf.remote.Reply
import scaaf.remote.End

/**
 * @author ofrasergreen
 *
 */
class Channel(upstream: scaaf.exchange.Channel[Frame]) extends scaaf.exchange.Channel[Envelope] {
  def reply(env: Envelope) = upstream.reply(new Reply(env.spacy))
  def close() = {
    upstream.reply(new End())
    upstream.close
  }
}