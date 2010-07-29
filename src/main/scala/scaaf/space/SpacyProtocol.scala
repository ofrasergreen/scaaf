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

package scaaf.space

import scaaf._

import sbinary._
import DefaultProtocol._
import Operations._
import java.io._;
import JavaIO._;
import java.util._

case class Header(
    val protocolVersion: Short,
    val classVersion: Short,
    val objectVersion: Int
    )

trait SpacyProtocol extends DefaultProtocol {
      
  implicit object HeaderFormat extends Format[Header] {
    def reads(in : Input) =  Header(read[Short](in), read[Short](in), read[Int](in))
    
    def writes(out : Output, h: Header) = {
      write(out, h.protocolVersion)
      write(out, h.classVersion)
      write(out, h.objectVersion)
    }
  }
  
  implicit object DateFormat extends Format[java.util.Date] {
    def reads(in : Input) =  new Date(read[Long](in))    
    def writes(out : Output, d: java.util.Date) = write(out, d.getTime)
  }
  
  implicit object UUIDFormat extends Format[java.util.UUID] {
    def reads(in : Input) =  new UUID(read[Long](in), read[Long](in))    
    def writes(out : Output, u: java.util.UUID) = {
      write(out, u.getMostSignificantBits)
      write(out, u.getLeastSignificantBits)
    }
  }
  
  trait GUIDFormat[T <: GUID] {
    def writes(out : Output, guid: T) = {
      write(out, guid.g._1)
      write(out, guid.g._2)
    }
  }
  
  implicit object AddIDFormat extends Format[AddID] with GUIDFormat[AddID] {
    def reads(in : Input) =  new AddID { override val g = (read[Long](in), read[Long](in)) } 
  }
  
  implicit object MsgIDFormat extends Format[MsgID] with GUIDFormat[MsgID] {
    def reads(in : Input) =  new MsgID { override val g = (read[Long](in), read[Long](in)) } 
  }
  
  implicit object ObjIDFormat extends Format[ObjID] with GUIDFormat[ObjID] {
    def reads(in : Input) =  new ObjID { override val g = (read[Long](in), read[Long](in)) } 
  }
  
  implicit def refFormat[T <: Spacy] = new Format[Ref[T]] {  
    def reads(in : Input) = new Ref[T](read[ObjID](in))
    def writes(out : Output, v: Ref[T]) = write(out, v.objID)
  } 
}

object SpacyProtocol extends SpacyProtocol
