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

import scaaf.GUID
import scaaf.AddID
import scaaf.cluster.Node
import scaaf.cluster.LocalNode
import scaaf.cluster.Broadcast
/**
 * @author ofrasergreen
 *
 */
trait Address {
  /**
   * The AddID (Address GUID).
   * 
   * @return  A {@code GUID} uniquely and reproducibly identifying the address.
   */
  def addID: AddID

  /**
   * Test if the address is can be routed locally
   * 
   * @return  {@code true} if the address is specifically local or global, {@code false} otherwise.
   */
  def local: Boolean = addID.local
  
  /**
   * Test if the address is global
   * 
   * @return  {@code true} if the address is global
   */
  def global: Boolean = addID.global
}

object Address {
  def newFromAddID(_addID: AddID) = {
    new Address {
      def addID = _addID
    }
  }
  
  def newFromDetails(exchange: Exchange[_, _], node: Node, data: Long) = {
    new Address {
      private var _addID: AddID = null
      
      override def addID = {
        if (_addID == null) _addID = GUID.newAddID(exchange.getClass, node.ID, 0)
        println(_addID)
        _addID
      }
      
      override def local = (node == LocalNode || global)
      override def global = (node == Broadcast)
    }
  }
  //private var paddID: AddID = null
  
  //def addID: AddID = {
  //  if (paddID == null) paddID = GUID.newAddID(ExchangeRegistry.getID(exchange.getClass), subscriber.getClass, false)
  //  paddID
  //}
  
}