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

import scaaf.space.Space
import scaaf.space.SpacyProtocol
import scaaf.space.SpacyFormat
import scaaf.space.Spacy

import scaaf.isc.exchange.Address

import scaaf._

import sbinary._
import DefaultProtocol._
import Operations._


/**
 * @author ofrasergreen
 *
 */
object RemoteProtocol extends SpacyProtocol {
  implicit object AddressFormat extends Format[Address] {
    def reads(in : Input) =  Address.newFromAddID(read[AddID](in))    
    def writes(out : Output, a: Address) = write(out, a.addID)
  }
  
  implicit object RemoteMessageFormat extends Format[Frame] {
    def reads(in: Input) =  {
      val _msgID = read[MsgID](in)
      
      _msgID.cls match {
        // TODO: There must be a better way than hard-coding these values
        case -1561764041 =>
          new Message(read[Address](in), payload(in, read[ObjID](in))) { override val msgID = _msgID }
        case -1713209830 =>
          new Reply(payload(in, read[ObjID](in))) { override val msgID = _msgID }
        case 418316299 =>
          new End()
        case _ =>
          throw new Exception("TODO: Handle message type: " + _msgID.cls)
      }
    }
    
    def payload(in: Input, objID: ObjID) = {
      Space.objectFromStream(in, objID)
    }
            
    def writes(out : Output, f: Frame) = {
      write(out, f.msgID)
      
      if (f.isInstanceOf[Addressable]) write(out, f.asInstanceOf[Addressable].address)
      if (f.isInstanceOf[Payloaded]) {
        val payload = f.asInstanceOf[Payloaded].payload 
        write(out, payload.objID)
        Space.serialize(payload, out) // The payload
      }
    }
  }
  
  
}