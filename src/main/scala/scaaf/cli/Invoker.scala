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

import java.io.PrintWriter
import exchange.Exchange
import scaaf.remote.IPCClient
import exchange.Request
import exchange.Output
import scaaf.remote.Message
import scaaf.remote.Reply
import scaaf.remote.End


/**
 * @author ofrasergreen
 *
 */
object Invoker {
  def invoke(args: Array[String]) {
    // Test if the server is running
    val ipcClient = new IPCClient
    
    if (ipcClient.serverExists) {
      // Connect to the existing server and invoke the CLI commands remotely
      ipcClient.connect
      
      // Create the message
      val address = Exchange.address
      val request = new Request(args)
      val message = Message(address, request)
      
      // Send the CLI request
      ipcClient.send(message)
      
      // Display the output
      var done = false
      do {
        ipcClient.receive match {
          case r: Reply => {
            r.payload match {
              case Output(s) => print(s)
            }
          }
          case e: End => done = true
        }
      } while (!done) 
      
      // Disconnect
      ipcClient.disconnect
    } else {
      // Run the commands locally
      val writer = new PrintWriter(System.out)
      Exchange.invoke(args, writer)
      writer.close
    }
  }
}