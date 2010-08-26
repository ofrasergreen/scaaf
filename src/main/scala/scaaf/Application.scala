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
package scaaf

import scaaf.logging._
import scaaf.cli.Registry
import scaaf.cli.CLIEntry
import scaaf.cli.InvocationTarget
import java.io.PrintWriter

/**
 * The main Application trait.
 * 
 * @author ofrasergreen
 */
trait Application extends Logging {
  def main(args: Array[String]): Unit = {
    ApplicationRef.application = this
    
    // Initialize the logger
    Log.initialize
    
    // Bootstrap
    Log.info("Bootstrapping...")
    scaaf.kernel.Bootstrap
    
    // Add the server CLI (TODO: Find a better place for this)
    val serverCLI = new CLIEntry("server", "view or change the state of the server", None)
    serverCLI.entries += new CLIEntry("start", "start the server", Some(
        InvocationTarget(
            "scaaf.kernel.Server", 
            "start", 
            true,
            List())))
        
    scaaf.cli.exchange.Exchange.invoke(args, new PrintWriter(System.out))
    //start
    //init
  }
  
  /**
   * Called after the server has been started. The default implementation
   * doesn't do anything so the method can be overridden application-specific
   * initialization.
   */
  def init {
    
  }
}

object ApplicationRef {
  var application: Application = new Application() {}
}