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
package scaaf.remote

import org.scalatest.WordSpec
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.MustMatchers
import scala.collection.mutable.Stack
import scaaf._
import exchange._

import space._
import test._
import java.io.File
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import scaaf.GUID

import org.newsclub.net.unix.AFUNIXSocket
import org.newsclub.net.unix.AFUNIXSocketAddress
import org.newsclub.net.unix.AFUNIXSocketException

import sbinary._
import DefaultProtocol._
import Operations._
import JavaIO._


/**
 * @author ofrasergreen
 *
 */
class PosixSpec extends WordSpec with MustMatchers with BeforeAndAfterEach with InitSpec {
  override def beforeEach = reset

  "The IPC POSIX controller" should {
    val f = new File("test" + File.separator + "ipc.sock")
    
    "create a Unix Domain Socket called 'ipc.sock'" in {
      assert(f.exists === true)
    }
    
    "let clients connect to the socket" in {
      val sock = AFUNIXSocket.newInstance();
      sock.connect(new AFUNIXSocketAddress(f));
      sock.close
    }
  }
}