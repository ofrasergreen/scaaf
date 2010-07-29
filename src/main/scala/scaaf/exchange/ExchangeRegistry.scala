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

import scaaf.logging.Logging

import scala.collection._

// TODO: This is a temporary class in the absence of the extension mechanism
/**
 * @author ofrasergreen
 *
 */
object ExchangeRegistry extends Logging {
  val map = mutable.Map[Int, Exchange[_]]()

  def getExchange(exchangeID: Int) = map(exchangeID)
    
  def register(exchange: Exchange[_]) {
    val cls = exchange.getClass
    Log.debug("Registering exchange %d:%s".format(cls.getCanonicalName.hashCode, cls.getName))
    map(cls.getCanonicalName.hashCode) = exchange
  }
}