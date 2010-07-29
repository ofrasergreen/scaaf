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

trait Exchange[T] extends Logging {
  protected val listeners = mutable.Map[Int, Listener[T]]()

  def register(listener: Listener[T]) {
    val cls = listener.getClass
    Log.debug("Registering listener %d:%s".format(cls.getCanonicalName.hashCode, cls.getName))
    listeners(cls.getCanonicalName.hashCode) = listener
  }
}

trait Connection