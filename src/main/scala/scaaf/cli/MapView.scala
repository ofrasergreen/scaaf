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
import scala.collection.Map

/**
 * @author ofrasergreen
 */
trait MapView extends CLIView {
  this: Map[String, Any] =>
  
  def render(io: IO) {
    // Find the longest key
    var maxLength = 0
    keys.foreach(k => if (k.length > maxLength) maxLength = k.length)
    
    for ((k, v) <- this.reversed) {
      val value = v match {
        case childView: CLIView =>
          val sw = new java.io.StringWriter
          childView.render(new IO(io.in, new PrintWriter(sw), io.err))
          val lineSeparator = System.getProperty("line.separator")
          val margin = " " * maxLength
          lineSeparator + margin + 
            ("X" + sw.toString).trim.substring(1).replace(lineSeparator, lineSeparator + margin)
        case _ => 
          v.toString
      }
      
      io.out.println(" " * (maxLength - k.length) + k + ": " + value)          
    }
  }  
}

object MapView {
  def apply(elems: (String, Any)*) = empty ++= elems
  def apply(xs: Map[String, Any]) = empty ++= xs
  def empty = new ListMap[String, Any] with MapView
}
