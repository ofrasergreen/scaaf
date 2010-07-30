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
package scaaf.exchange.service

import scaaf.exchange.isc.Envelope
import scaaf.space.Spacy

/**
 * @author ofrasergreen
 *
 */
class Channel(upstream: scaaf.exchange.Channel[Envelope]) extends scaaf.exchange.Channel[Spacy] {
  def reply(spacy: Spacy) = upstream.reply(new Envelope(0L, spacy))
  def close() = upstream.close
}