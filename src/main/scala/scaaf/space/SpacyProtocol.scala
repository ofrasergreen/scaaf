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
  
  implicit object UUIDFormat extends Format[UUID] {
    def reads(in : Input) = new UUID(read[Long](in), read[Long](in))
    
    def writes(out : Output, uuid: UUID) = {
      write(out, uuid.getMostSignificantBits)
      write(out, uuid.getLeastSignificantBits)
    }
  }
  
  implicit def refFormat[T <: Spacy] = new Format[Ref[T]] {  
    def reads(in : Input) = new Ref[T](read[UUID](in))
    def writes(out : Output, v: Ref[T]) = write(out, v.uuid)
  } 
}

object SpacyProtocol extends SpacyProtocol
