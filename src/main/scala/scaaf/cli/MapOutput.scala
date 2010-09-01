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

import scaaf.space.Spacy

import java.io.PrintWriter
import scala.collection.mutable.ListMap
import scala.collection.mutable.MapLike


/**
 * @author ofrasergreen
 */
case class MapOutput() extends ListMap[String, Any] with MapLike[String, Any, MapOutput] with CLIView {
  override def empty = MapOutput.empty
  
  def render(w: PrintWriter) {
    // Find the longest key
    var maxLength = 0
    keys.foreach(k => if (k.length > maxLength) maxLength = k.length)
    
    keys.foreach(k => {
      val v = this(k)
      v match {
        case l: MapOutput =>
          w.println("%s %s:".format(" " * (maxLength - k.length), k))
          l.writeIndented(maxLength, w)
        case _ => w.println("%s %s: %s".format(" " * (maxLength - k.length), k, this(k)))
      }
    })
  }
  
  def writeIndented(indentation: Int, w: PrintWriter) {
    // TODO: Rationalize this with render
    val margin = " " * (indentation + 2)
    keys.foreach(k => {
      val v = this(k)
      v match {
        case l: MapOutput =>
          w.println("%s %s:".format(margin, k))
          l.writeIndented(margin.length + 3, w)
        case _ => w.println("%s %s: %s".format(margin, k, this(k)))
      }
    })
  }
}

object MapOutput {
  def apply(elems: (String, Any)*): MapOutput = empty ++= elems
  def empty = new MapOutput
}
