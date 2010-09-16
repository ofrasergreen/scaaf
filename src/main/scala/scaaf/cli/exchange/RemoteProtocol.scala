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
package scaaf.cli.exchange

import scaaf.space.Space
import scaaf.space.SpacyProtocol
import scaaf.space.SpacyFormat
import scaaf.space.Spacy

import scaaf.exchange.Address

import scaaf._

import sbinary._
import DefaultProtocol._
import Operations._

/**
 * @author ofrasergreen
 *
 */
object RemoteProtocol extends SpacyProtocol {
  implicit object RequestFormat extends SpacyFormat[Request] {
    def reads(in : Input, o: ObjID) = new Request(
        read[Array[String]](in)) {
        override val objID = o
      }   
    def writes(out: sbinary.Output, r: Request) = write(out, r.args)
  }

  implicit object OutputFormat extends SpacyFormat[Output] {
    def reads(in : Input, o: ObjID) = new Output(
        read[String](in)) {
        override val objID = o
      }   
    def writes(out: sbinary.Output, o: Output) = write(out, o.str)
  }
  
  implicit object ErrorFormat extends SpacyFormat[Error] {
    def reads(in : Input, o: ObjID) = new Error(
        read[String](in)) {
        override val objID = o
      }   
    def writes(out: sbinary.Output, o: Error) = write(out, o.str)
  }
  
  Space.FormatRegistry.register(classOf[Request].asInstanceOf[Class[Any]], RequestFormat.asInstanceOf[SpacyFormat[Spacy]])
  Space.FormatRegistry.register(classOf[Output].asInstanceOf[Class[Any]], OutputFormat.asInstanceOf[SpacyFormat[Spacy]])
  Space.FormatRegistry.register(classOf[Error].asInstanceOf[Class[Any]], ErrorFormat.asInstanceOf[SpacyFormat[Spacy]])
}