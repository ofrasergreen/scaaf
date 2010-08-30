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
    Log.debug("Bootstrapping...")
    scaaf.kernel.Bootstrap
    
    // Invoke
    scaaf.cli.Invoker.invoke(args)
    
    System.exit(0)
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