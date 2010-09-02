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

import scaaf.service.Service

import java.io.PrintWriter

/**
 * A registry mapping CLI commands or groups to their implementation
 * 
 * @author ofrasergreen
 */
class RootGroup extends Group("", "") with CLIListener {
  entries += scaaf.kernel.$NodeCLIGroup()
  entries += $HelpGroup()
  
  def deliver(args: Seq[String], io: IO) {
    val tail = args.tail
    args.head match {
      case "node" => scaaf.kernel.$NodeCLIGroup().deliver(tail, io)
      case "help" => $HelpGroup().deliver(tail, io)
    }
  }
}

object $RootGroup {
  var o = new RootGroup
  def apply() = o
}