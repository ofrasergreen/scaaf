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

import scaaf.cli.CLIListener
import scaaf.cli.Group
import scaaf.cli.Arg
import scaaf.cli.Command
import scaaf.cli.RootGroup
import scaaf.cli.Invocation
import scaaf.cli.Help
import scaaf.service.Locator

import java.io.PrintWriter

/**
 * @author ofrasergreen
 *
 */
object BaseCLI extends CLIListener with Locator {
  // server commands
  val server = new Group("view or change the state of the server")
  RootGroup += ("server" -> server)
  server += ("start" -> new Command("start the server", this, true, Array[Arg]())) 
  
  // help commands
  RootGroup += ("help" -> new Command("display help and exit", this, true, 
      List(new Arg("COMMAND", "COMMANDs are command and optionally sub-commands to display help about.", true, true, classOf[String]))))
    
  def deliver(invocation: Invocation, writer: PrintWriter) {
    (invocation match {
      case Invocation("help", args) => $[Help].help(args(0).asInstanceOf[Seq[String]])
      case Invocation("server start", args) => new Server().start
    }) render writer
  }
}