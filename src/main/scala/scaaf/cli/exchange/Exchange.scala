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
package scaaf.cli.exchange

import scaaf.logging.Logging
import scaaf.exchange.ReplyableChannel

import scaaf.remote.Frame
import scaaf.remote.Message
import scaaf.remote.End
import scaaf.GUID
import scaaf.Configuration
import scaaf.cluster.LocalNode
import scaaf.isc.exchange.Envelope
import scaaf.cli._

import scala.collection.mutable.ListBuffer
import scala.actors.Actor
import Actor._

import scaaf.space.Spacy
import scaaf.exchange.ReplyingSubscriber

import java.io.PrintWriter
import java.io.BufferedWriter

class IllegalCLIArgumentException(message: String) extends Exception(message) {}

/**
 * @author ofrasergreen
 *
 */
object Exchange extends scaaf.exchange.Exchange with ReplyingSubscriber[Envelope] with Logging {
  def address = new scaaf.isc.exchange.Address {
    override def addID = GUID.newAddID(Exchange.getClass, LocalNode.ID, 0)
  }

  def deliver(env: Envelope, channel: ReplyableChannel[Envelope]) {
    // Create a buffered 1k print writer which will return messages
    val writer = new PrintWriter(new BufferedWriter(new RemoteWriter(channel), 1024)) 
    env.spacy match {
      case r: Request => 
        try {
          invoke(r.args, writer)
        } catch {
          case e: IllegalCLIArgumentException => 
            writer.print(e.getMessage)
        }
      case _ => Log.error("Received unwanted message of type " + env.spacy.getClass)
    }
  }
  
  def invoke(args: Array[String], writer: PrintWriter) {
    Log.debug("Invoking CLI with args: " + args.toString)
    if (args.length == 0) {
      errorExit(List("Interactive shell not implemented.", 
        "Type '" + Configuration.name + " help' for a list of available commands."))
    }
    
    // Find the registry entry for the CLI
    var i = 0
    var registry: CLIEntry = Registry
    while (i < args.length) {
      registry.entries.find(e => e.name == args(i)) match {
        case Some(entry) =>
          registry = entry
          i += 1
        case None => throw new IllegalCLIArgumentException("Unknown command: '" + args(i) + "'\n" + 
            "Type '" + Configuration.name + " help " + args.view(0, i).reduceLeft(_+" "+_) + "' for more help.")
      }
    }
    
    if (registry.target.isDefined) {
      invoke(registry, args.view(i, args.length).toList, writer)
    } else {
      if (i >= args.length) {
        throw new IllegalCLIArgumentException("Not enough arguments provided.\n" + 
            "Type '" + Configuration.name + " help " + args.view(0, i).reduceLeft(_+" "+_) + "' for more help.")
      } else {
        if (i == 0) {
          throw new IllegalCLIArgumentException("Unknown command: '" + args(i) + "'\n" + 
            "Type '" + Configuration.name + " help' for a list of available commands.")
        }
      }
    }
  }
  
  def errorExit(msgs: List[String]) {
    msgs.foreach(System.err.println)
    System.exit(1)
  }
  
  def mapArgs(args: List[String], argSpec: List[Arg]): Array[AnyRef] = {
    var result = ListBuffer[AnyRef]()
  
    // TODO: Handle options
    
    var i = 0
    argSpec.foreach(spec => {
      if (spec.repeated) {
        result += args.slice(i, args.length)
      }
      
      i += 1
    })
    
    Log.debug("Arguments: " + result)
    
    result.toArray
  }
  
  def invoke(entry: CLIEntry, args: List[String], writer: PrintWriter) {
    val target = entry.target.get
    val klass = Class.forName(target.cls)
    val method = klass.getMethods().find(_.getName == target.method).getOrElse(
        throw new Exception("Couldn't find method '" + target.method + "' on service '" + target.cls + "'")
      )
    
    val typedArgs = mapArgs(args, target.argSpec)
      
    if (target.local) {
      val cliService = klass.newInstance.asInstanceOf[CLIService]
      
      val view = method.invoke(cliService, typedArgs:_*).asInstanceOf[CLIView]
      view.render(writer)
    }
    
    writer.close
  }
}