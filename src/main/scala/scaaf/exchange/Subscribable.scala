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

import scala.collection._


/**
 * @author ofrasergreen
 *
 */
trait Subscribable[T <: Subscriber[_]] {
  protected val subscribers = mutable.Map[Int, T]()

  def register(subscriber: T) {
    subscribers(subscriber.hashCode) = subscriber
  }
}