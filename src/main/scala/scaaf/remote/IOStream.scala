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

import scaaf.exchange.Address
import scaaf.space.Space
import scaaf.space.Spacy
import RemoteProtocol._
import sbinary.Operations._

import java.io.ByteArrayOutputStream
import scaaf.GUID
import java.io.InputStream
import java.io.OutputStream

/**
 * @author ofrasergreen
 *
 */
class IOStream(is: InputStream, os: OutputStream) {
  def send(obj: Spacy, address: Address) {
    // Create and send the header
    //val header = Header(1, UUID.randomUUID, Space.FormatRegistry.getClassID(obj.getClass), obj.uuid, address.uuid)
    //Space.serialize(header, os)
    
    // Send the object
    //Space.serialize(obj, os)
  }
}