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
  def help(cats: Seq[String]): CLIView = {
    val groups = TableView($RootGroup().entries.map(e => ListMap(
      "category" -> e.name,
      "description" -> e.description
    )))
    
    new CLIView() {
      def render(io: IO) = {
        io.out.println("usage: " + Configuration.name + " <command> [subcommands...] [options] [args]")
        io.out.println("Type '" + Configuration.name + " <command> help' for help on a specific command.")
        io.out.println()
        io.out.println("Available commands:")
        groups.format.drop(1).foreach(c => io.out.println("   " + c ))
      }
    }
  }
}

object $Help {
  var imp = new Help()
  def apply() = imp
}