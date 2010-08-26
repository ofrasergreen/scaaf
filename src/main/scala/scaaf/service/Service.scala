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
package scaaf.service

import scala.reflect.Manifest


/**
 * @author ofrasergreen
 *
 */
trait Service {
  // We override this so that two instances of the same service
  // will have the same hashcode and hashcodes will be the same
  // even if a service implementation is changed. This makes addresses
  // constant so long as the class name remains the same.
  override def hashCode() = this.getClass.getName.hashCode
}

/**
 * @author ofrasergreen
 *
 */
object Service {
  private var services = Map[Class[_], Service]() 
  
  def lookup[T <: Service](implicit m: Manifest[T]): T = {
    val cls = Class.forName(m.toString)
    services(cls).asInstanceOf[T]
  }
  
  def add(o: Service) = {
    services += (o.getClass -> o)
  }
}