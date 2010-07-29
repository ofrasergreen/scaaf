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

import scaaf.cli.CLIService
import scaaf.logging.Logging
import scaaf.remote.SelectingRunner
import scaaf.space.Space
import scaaf.space.Reboot
import scaaf.ApplicationRef
import scaaf.remote.RemoteProtocol
import scaaf.remote.EchoProtocol

/**
 * @author ofrasergreen
 *
 */
class Server extends CLIService with Logging {
  def start() {
    Log.info("Bootstrapping...")
    bootstrap
    SelectingRunner.start
    Log.info("Server started")
    ApplicationRef.application.init
  }
  
  def bootstrap {
    // TODO: Move this:
    // Register protocols
    RemoteProtocol
    EchoProtocol
    scaaf.cli.RemoteProtocol
    
    // Initialize space
    Space.start
    Space !? Reboot
    
    // Initialize exchanges
    scaaf.exchange.uds.Exchange.start
    scaaf.exchange.isc.Exchange.start
    scaaf.exchange.service.Exchange.start
    scaaf.cli.Exchange.start
    
    // Initialize listeners
    scaaf.exchange.service.Exchange.register(new scaaf.remote.EchoService())
  }
}