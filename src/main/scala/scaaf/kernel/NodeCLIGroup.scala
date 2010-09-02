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
package scaaf.kernel

import scaaf.cli.Group
import scaaf.cli.CLIListener
import scaaf.cli.IO

/**
 * @author ofrasergreen
 *
 */
class NodeCLIGroup extends Group("node", "View or change the state of nodes.") with CLIListener {
  def deliver(args: Seq[String], io: IO) {
    args.head match {
      case "start" => $Node().start
    }
  }
}

object $NodeCLIGroup {
  var imp = new NodeCLIGroup
  def apply() = imp
}