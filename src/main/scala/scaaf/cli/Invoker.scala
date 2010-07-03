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

import scaaf.Configuration

/**
 * @author ofrasergreen
 *
 */
object Invoker {
  def invoke(args: Array[String]) {
    if (args.length == 0) {
      errorExit(List("Interactive shell not implemented.", 
        "Type '" + Configuration.name + " help' for a list of available commands."))
    }
    
    // Find the registry entry for the CLI
    var i = 0
    var registry: RegistryEntry = Registry
    while (i < args.length && registry.entries.contains(args(i))) {
      registry = registry.entries(args(i))
      i += 1
    }
    
    if (registry.classMethod.isDefined) {
      invoke(registry, args.view(i, args.length).toList)
    } else {
      if (i >= args.length) {
        errorExit(List("Not enough arguments provided.", 
          "Type '" + Configuration.name + " help " + args.reduceLeft(_+" "+_) + "' for more help."))
      } else {
        if (i == 0) {
          errorExit(List("Unknown command: '" + args(i) + "'", 
            "Type '" + Configuration.name + " help' for a list of available commands."))
        } else {
          errorExit(List("Unknown command: '" + args(i) + "'", 
            "Type '" + Configuration.name + " help " + args.view(0, i).reduceLeft(_+" "+_) + "' for more help."))
          
        }
      }
    }
  }
  
  def errorExit(msgs: List[String]) {
    msgs.foreach(System.err.println)
    System.exit(1)
  }
  
  def invoke(entry: RegistryEntry, args: List[String]) {
    println("Invoking: " + entry.toString)
  }
}