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

import org.scalatest.WordSpec
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.MustMatchers
import scala.collection.mutable.Stack
import scaaf._
import space._
import test._
import java.io.File
import java.io.ByteArrayInputStream

import org.newsclub.net.unix.AFUNIXSocket
import org.newsclub.net.unix.AFUNIXSocketAddress
import org.newsclub.net.unix.AFUNIXSocketException

/**
 * @author ofrasergreen
 *
 */
class IPCPosixSpec extends WordSpec with MustMatchers with BeforeAndAfterEach with InitSpec {
  override def beforeEach = reset
  "The IPCSocket receiver" should {
    "return all bytes up to a 00-01 sequence" in {
      val channel = new IPCSocket {
        inputStream = new ByteArrayInputStream(Array[Byte](01, 02, 03, 00, 01, 04))
      }
      
      channel.receive must equal (Array[Byte](1, 2, 3))
    }

    "handle multiple blocks in a single stream" in {
      val channel = new IPCSocket {
        inputStream = new ByteArrayInputStream(Array[Byte](1, 2, 3, 0, 1, 4, 5, 6, 0, 1, 7, 0, 1))
      }
      
      channel.receive must equal (Array[Byte](1, 2, 3))
      channel.receive must equal (Array[Byte](4, 5, 6))
      channel.receive must equal (Array[Byte](7))
    }
  }
  
  "The IPC POSIX controller" should {
    val f = new File("test" + File.separator + "cli.sock")
    
    "create a Unix Domain Socket called 'cli.sock'" in {
      assert(f.exists === true)
    }
    
    "let clients connect to the socket" in {
      val sock = AFUNIXSocket.newInstance();
      sock.connect(new AFUNIXSocketAddress(f));
      sock.close
    }
    
    "respond to the CLI handshake." in {
      val sock = AFUNIXSocket.newInstance();
      sock.connect(new AFUNIXSocketAddress(f));
      
      val is = sock.getInputStream()
      val os = sock.getOutputStream()
      
      val buf:Array[Byte] = new Array(128)
      os.write("CLI 0.1".getBytes)
      os.write(Array[Byte](0, 1))
      os.flush
      
      // Do this with a timeout
      val read = is.read(buf)
      val output = new String(buf, 0, read)
      
      sock.close
      assert(output === "OK")
    }
  }
}