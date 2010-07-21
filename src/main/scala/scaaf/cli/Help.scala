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

import scala.collection.mutable.ListBuffer
import scala.collection.immutable.ListMap
import scaaf.Configuration

/**
 * @author ofrasergreen
 *
 */
class Help extends CLIService {
  def help(category: String): CLIOutput = {
    println("Category: " + category)
    val categories = TableOutput(Registry.entries.keySet.map(k => TableRowOutput(ListMap(
      "category" -> k,
      "description" -> Registry.entries(k).description
    ))).toList)
    
    
    
    new CLIOutput() {
      def format = {
        val output = ListBuffer[String]()
        output += "usage: " + Configuration.name + " <command> [subcommand] [options] [args]"
        output += "Type '" + Configuration.name + " help <command>' for help on a specific command."
        output += ""
        output += "Available commands:"
        output ++= categories.format.drop(1).map("   " + _)
        output.toList
      }
    }
  }
}