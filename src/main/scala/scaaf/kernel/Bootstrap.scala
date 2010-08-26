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

import scaaf.space.Space
import scaaf.space.Reboot
import scaaf.service.Service
import scaaf.service.Locator

/**
 * @author ofrasergreen
 *
 */
object Bootstrap {
    // TODO: Move this:
    // Register protocols
    scaaf.remote.RemoteProtocol
    scaaf.remote.EchoProtocol
    scaaf.cli.exchange.RemoteProtocol
    
    // Initialize space
    Space.start
    Space !? Reboot
    
    // Register services
    Service.add(new scaaf.cli.Help)
    
    // Initialize exchanges
    scaaf.ipc.uds.exchange.Exchange
    scaaf.isc.exchange.Exchange
    scaaf.service.exchange.Exchange
    scaaf.cli.exchange.Exchange
    
    // Initialize subscribers
    scaaf.service.exchange.Exchange.register(new scaaf.remote.EchoService())
    scaaf.isc.exchange.Exchange.register(scaaf.cli.exchange.Exchange)
    scaaf.isc.exchange.Exchange.register(scaaf.service.exchange.Exchange)
    
    // Initialize the CLI
    BaseCLI
}