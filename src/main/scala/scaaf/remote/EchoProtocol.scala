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

import sbinary._
import DefaultProtocol._
import Operations._

import scaaf.GUID
import scaaf.ObjID

/**
 * @author ofrasergreen
 *
 */
object EchoProtocol extends SpacyProtocol {

  implicit object EchoRequestFormat extends SpacyFormat[EchoRequest] {
   def reads(in : Input, o: ObjID) =  new EchoRequest(
        read[Int](in),
        read[Array[Byte]](in)) {
        override val objID = o
      }
     
    def writes(out : Output, e: EchoRequest) = {
      write(out, e.sequenceNumber)
      write(out, e.data)
    }
  }
  
  implicit object EchoReplyFormat extends SpacyFormat[EchoReply] {
    def reads(in : Input, g: ObjID) = new EchoReply(
        read[Int](in),
        read[Array[Byte]](in)) {
        override val objID = g
    }
    
    def writes(out : Output, e: EchoReply) = {
      write(out, e.sequenceNumber)
      write(out, e.data)
    }
  }
  
  Space.FormatRegistry.register(classOf[EchoRequest].asInstanceOf[Class[Any]], EchoRequestFormat.asInstanceOf[SpacyFormat[Spacy]])
  Space.FormatRegistry.register(classOf[EchoReply].asInstanceOf[Class[Any]], EchoReplyFormat.asInstanceOf[SpacyFormat[Spacy]])
}
