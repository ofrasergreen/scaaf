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

import scala.actors.Actor
import scala.actors.Actor._
import scala.collection.mutable.{Map, ListBuffer}
import java.util.Random

class Dispatcher() extends Actor {
  val idleWorkers = new ListBuffer[ServerWorker]
  val busyWorkers = Map[Int, ServerWorker]()
  val rng = new Random()

  for (i <- 1 to Runtime.getRuntime().availableProcessors() * 4 + 1) {
    val w = new ServerWorker(i, this)
    w.start()
    idleWorkers += w
  }

  def act() {
    loop {
      react {
        case read: Read =>
          getWorker ! read
        case Idle(worker) =>
          busyWorkers -= worker.id
          idleWorkers += worker
      }
    }
  }

  def getWorker(): ServerWorker = {
    if (idleWorkers.length == 0)
      busyWorkers.get(rng.nextInt(busyWorkers.size)).get
    else {
      val w = idleWorkers.remove(0)
      busyWorkers += w.id -> w
      w
    }
  }
}
