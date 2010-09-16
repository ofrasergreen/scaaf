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
package scaaf.cli

import scala.collection.mutable.ArrayBuffer

/**
 * @author ofrasergreen
 *
 */
trait ListView extends CLIView {
  this: Seq[Any] =>
  
  def render(io: IO) {
    if (!this.isEmpty) {
      this.foreach(y => io.out.println(y.toString))
    }
  }
}

object ListView {
  def apply(xs: Seq[Any]) = empty ++= xs
  def empty = new ArrayBuffer[Any] with ListView
}