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

import scaaf.space._
import scala.collection.mutable.ArrayBuffer
import java.io.PrintWriter

/**
 * @author ofrasergreen
 *
 */
trait TableView extends CLIView {
  this: Seq[Map[String, Any]] =>
  
  def render(io: IO) {
    format.foreach(row => io.out.println(row))
  }
  
  def format(): Seq[String] = {
    // Convert to a List[List[String]]
    if (this.isEmpty) {
      return List()
    }
    
    val first = this.head
    val table = first.keys.toList :: this.map(o => o.values.map(_.toString)).toList
    
    // Initiate the lengths list to 0
    val lengths = Array.fill(table(0).size)(0)
    
    // Look for the longest item in each column
    table.foreach(y => {
      var i = 0
      y.foreach(col => {
        lengths(i) = lengths(i).max(col.toString.length)
        i += 1
      })
    })
    
    val output = ArrayBuffer[String]()
    table.foreach(y => {
      var row = ""
      var i = 0
      y.foreach(col => {
        row += col.toString + " " * (lengths(i) - col.toString.length + 1)
        i += 1
      })
      output += row
    })
    
    output.toSeq
  }
}

object TableView {
  def apply(xs: Seq[Map[String, Any]]) = empty ++= xs
  def empty = new ArrayBuffer[Map[String, Any]] with TableView
}