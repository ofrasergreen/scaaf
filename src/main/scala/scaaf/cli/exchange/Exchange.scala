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
package cli.exchange

import scaaf.logging.Logging
import scaaf.exchange.Replyable
import scaaf.exchange.Subscribable
import scaaf.exchange.Subscriber

import scaaf.remote.Frame
import scaaf.remote.Message
import scaaf.remote.End
import scaaf.GUID
import scaaf.Configuration
import scaaf.cluster.LocalNode
import scaaf.isc.exchange.Envelope
import scaaf.cli._
import service.Service

import scala.collection.mutable.ListBuffer
import scala.actors.Actor
import Actor._

import scaaf.space.Spacy
import scaaf.exchange.ReplyingSubscriber

import java.io.PrintWriter
import java.io.BufferedWriter
import java.io.BufferedReader
import java.io.InputStreamReader

class IllegalCLIArgumentException(message: String) extends Exception(message) {}

/**
 * @author ofrasergreen
 *
 */
object Exchange extends scaaf.exchange.Exchange with ReplyingSubscriber[Envelope] with Service with Logging {
  def address = new scaaf.isc.exchange.Address {
    override def addID = GUID.newAddID(Exchange.getClass, LocalNode.ID, 0)
  }

  def deliver(env: Envelope, channel: Replyable[Envelope]) {
    // Create buffered 1k print writers which will return messages
    val out = new PrintWriter(new BufferedWriter(new RemoteWriter(channel, false), 1024))
    val err = new PrintWriter(new BufferedWriter(new RemoteWriter(channel, true), 1024))
    // TODO: Handle stdin
    val in = new BufferedReader(new InputStreamReader(System.in))
    val io = new IO(in, out, err)
    
    env.spacy match {
      case r: Request => 
        try {
          invoke(r.args, io)
        } catch {
          case e: Exception => io.err.println(e.getMessage)
        }
      case _ => Log.error("Received unwanted message of type " + env.spacy.getClass)
    }
    
    io.close
    channel.eos()
  }
  
  def invoke(args: Array[String], io: IO) {
    Log.debug("Invoking CLI with args: " + args.mkString(", "))
    if (args.length == 0) {
      throw new IllegalCLIArgumentException("Interactive shell not implemented.\n" +
        "Type '" + Configuration.name + " help' for a list of available commands.")
    }
    
    try {
      $RootGroup().deliver(args, io)
    } catch {
      case e: MatchError => throw new IllegalCLIArgumentException("Unregonized argument: " + e.getMessage + "\n" + "Type '" + Configuration.name + " help' for a list of available commands.")
    }
    
//    // Find the registry entry for the CLI
//    var i = 0
//    var group: Group = RootGroup$()
//    var command: Option[Command] = None
//    
//    while (command.isEmpty && i < args.length) {
//      group.entries.find(n => n == args(i)) match {
//        case Some(name) =>
//          group.entries(name) match {
//            case c: Command => command = Some(c)
//            case g: Group => {
//              group = g
//              i += 1
//            }
//          }
//        case None => throw new IllegalCLIArgumentException("Unknown command: '" + args(i) + "'\n" + 
//            "Type '" + Configuration.name + " help " + args.view(0, i).reduceLeft(_+" "+_) + "' for more help.")
//      }
//    }
//    
//    command match {
//      case Some(c) => invoke(c, args.view(0, i + 1).mkString(" "), args.view(i + 1, args.length), writer)
//      case _ => throw new IllegalCLIArgumentException("Not enough arguments provided.\n" + 
//            "Type '" + Configuration.name + " help " + args.view(0, i).reduceLeft(_+" "+_) + "' for more help.")
  }
  
//  def mapArgs(args: Seq[String], argSpec: Seq[Arg]): Array[AnyRef] = {
//    var result = ListBuffer[AnyRef]()
//  
//    // TODO: Handle options
//    
//    var i = 0
//    argSpec.foreach(spec => {
//      if (spec.repeated) {
//        result += args.slice(i, args.length)
//      } else {
//        result += args(i)
//      }
//      
//      i += 1
//    })
//    
//    Log.debug("Arguments: " + result)
//    
//    result.toArray
//  }
//  
//  def invoke(command: Command, cliCommand: String, args: Seq[String], writer: PrintWriter) {
//    val typedArgs = mapArgs(args, command.args)
//      
//    //if (target.remote) {
//    command.listener.deliver(Invocation(cliCommand, typedArgs), writer)
//    //view.render(writer)
//    //}
//  }
}