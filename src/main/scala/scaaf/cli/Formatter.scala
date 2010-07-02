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

object Formatter {
  def format(out: List[CLIOutput]): String = {
    // Convert to a List[List[String]]
    if (out.isEmpty) {
      return "Nothing"
    }
    
    val first = out(0)
    val table = first.tableFormat.keys.toList :: out.map(o => o.tableFormat.values.map(v => first.format(v.toString)).toList)
    
    
    // Initiate the lengths list to 0
    val lengths = Array.fill(table(0).size)(0)
    
    // Look for the longest item in each column
    table.foreach(y => 
      for (i <- 0 to y.size - 1)(lengths(i) = lengths(i).max(y(i).length))
    )
    
    var output = ""
    table.foreach(y => {
      for (i <- 0 to y.size - 1)({
        output += y(i) + " " * (lengths(i) - y(i).length + 1)
      })
      output += "\n"
    })
    
    output
  }
}