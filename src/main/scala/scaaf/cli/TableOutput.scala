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
import scala.collection.mutable.ListBuffer
import java.io.PrintWriter

/**
 * @author ofrasergreen
 *
 */
case class TableOutput(rows: List[TableRowOutput]) extends CLIView {
  def render(w: PrintWriter) {
    format.foreach(row => w.println(row))
  }
  
  def format(): List[String] = {
    // Convert to a List[List[String]]
    if (rows.isEmpty) {
      return List()
    }
    
    val first = rows.head
    val table = first.columns.keys.toList :: rows.map(o => o.columns.values.map(_.toString).toList)    
    
    // Initiate the lengths list to 0
    val lengths = Array.fill(table(0).size)(0)
    
    // Look for the longest item in each column
    table.foreach(y => 
      for (i <- 0 to y.size - 1)(lengths(i) = lengths(i).max(y(i).toString.length))
    )
    
    val output = ListBuffer[String]()
    table.foreach(y => {
      var row = ""
      for (i <- 0 to y.size - 1)({
        row += y(i).toString + " " * (lengths(i) - y(i).toString.length + 1)
      })
      output += row
    })
    
    output.toList
  }
}