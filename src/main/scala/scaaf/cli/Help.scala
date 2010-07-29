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

import java.io.PrintWriter

/**
 * @author ofrasergreen
 *
 */
class Help extends CLIService {
  def help(cats: List[String]): CLIView = {
    println("Category: " + cats.toString)
    val categories = TableOutput(Registry.entries.keySet.map(k => TableRowOutput(ListMap(
      "category" -> k,
      "description" -> Registry.entries(k).description
    ))).toList)
    
    
    
    new CLIView() {
      def render(w: PrintWriter) = {
        w.println("usage: " + Configuration.name + " <command> [subcommand] [options] [args]")
        w.println("Type '" + Configuration.name + " help <command>' for help on a specific command.")
        w.println()
        w.println("Available commands:")
        categories.format.drop(1).foreach(c => w.println("   " + c ))
      }
    }
  }
}