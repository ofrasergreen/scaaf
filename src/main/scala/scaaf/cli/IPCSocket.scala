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

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

import org.newsclub.net.unix.AFUNIXServerSocket
import org.newsclub.net.unix.AFUNIXSocketAddress

import scaaf.logging.Logging
import scaaf.Configuration

/**
 * @author ofrasergreen
 *
 */
trait IPCSocket extends IPCConfiguration with Logging {
  System.setProperty("org.newsclub.net.unix.library.path", Configuration.libDir)

  val size = 1024
  val timeout = 1000
  var inputStream: InputStream = _
  var outputStream: OutputStream = _
  private val out = new ByteArrayOutputStream(size)
  
  def receive() = {
    val buf: Array[Byte] = new Array(size)

    var r: Int = -1
    var end: Int = -1
    var output = Array[Byte]()
    do {
      println("*** T1 ***: " + inputStream.available)
      
      if (inputStream.available == 0) {
        // TODO: Change implementation to use NIO with proper timeout
        Thread.sleep(timeout)
        if (inputStream.available == 0)
          r = -1
        else
          r = inputStream.read(buf)
      } else {
        r = inputStream.read(buf)
      }
      
      if (r == -1) {
        end = 0
      } else {        
        for (i <- 0 to (r - 2))
          if ((end <= 0) && (buf(i) == 0) && (buf(i + 1) == 1)) end = i
        
        
        if (end > 0) {
          println("Breaking at " + end)
          out.write(buf, 0, end)
          output = out.toByteArray
          out.reset
          val startNext = end + 2
          if (startNext < r)
            out.write(buf, startNext, r - startNext)
        } else if (end == -1) {
          out.write(buf, 0, r)
        } else {
          output = out.toByteArray
        }
      }
    } while(end == -1)
    
    println("*** T2 ***")
      
    output
  }
  
  def send(buf: Array[Byte]) {
    outputStream.write(buf)
    outputStream.write(Array[Byte](0, 1))
    outputStream.flush
  }
}