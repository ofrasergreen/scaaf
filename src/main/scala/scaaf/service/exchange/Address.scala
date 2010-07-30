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
package scaaf.service.exchange

import scaaf.cluster.Node
import scaaf.AddID
import scaaf.GUID

/**
 * @author ofrasergreen
 *
 */
object Address {
  def newAddress(node: Node, subscriber: Class[_]) = {
    new scaaf.exchange.Address {
      override def addID = GUID.newAddID(Exchange.getClass, node.ID, subscriber.getCanonicalName.hashCode)
    }
  }
}