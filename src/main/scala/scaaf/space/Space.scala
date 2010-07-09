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

package scaaf.space

import java.util.UUID
import scala.collection._
import scaaf.logging._
import scala.actors._
import java.io._
import sbinary._
import DefaultProtocol._
import Operations._
import JavaIO._;
import scala.reflect.Manifest

case class ValidationException(reason: String) extends Exception(reason) 

trait Spacy {
  val uuid: UUID = UUID.randomUUID
  def valid = true
}

case class Ref[T <: Spacy](obj: T) {
  private var pobj = obj
  private var puuid:UUID = null
  
  def this(uuid: UUID) = { 
    this(null.asInstanceOf[T])
    this.puuid = uuid
  }
  
  def get = {
    if (obj == null) pobj = Space.find[T](puuid)
    pobj
  }
  
  def uuid = {
    if (puuid == null) puuid = pobj.uuid 
    puuid
  }
}

case class Write[T <: Spacy](obj: T)
case object Stats
case object Reboot
case object Done

trait SpacyFormat[T <: Spacy] {
  def reads(in: Input, uuid: UUID): T
  def writes(out: Output, t: T)
}

object Space extends Actor with Logging {
  private val cache = mutable.Map[UUID, Spacy]()
  private var stats = Statistics(0)
  private var spaceDir = "space"
  private var memOnly = false
  
  def statistics = stats
  
  def act = loop {
    react {
      case Write(obj) =>
        // Store it to disk
        if (!memOnly) writeToDisk(obj)
        
        addToCache(obj)
        reply(Done)
      case Stats =>
        Log.debug("Getting stats")
        reply(stats)
        
      case Reboot =>
        Log.info("Rebooting Space.")
        cache.clear
        stats = Statistics(0)
        if (!memOnly) {
          val f = new File(spaceDir)
          f.mkdirs
          readFromDisk("", f)
        }
        Log.debug("Completed Space reboot. " + cache.toString)
        reply(Done)
      }
    
  }
  
  def start(spaceDir: Option[String]): Actor = {
    println("Spacedir: " + spaceDir)
    spaceDir match {
      case Some(dir) => this.spaceDir = dir
      case None => memOnly = true
    }
    
    start
  }
  
  private def addToCache(obj: Spacy) = {
    cache(obj.uuid) = obj
    stats = stats copy (objectCount = stats.objectCount + 1)
  }
  
  private def writeToDisk(obj: Spacy): Unit = {
    //Log.debug("Writing " + obj.uuid)
    // TODO - Find a cleaner way to do this
    val cls = if (obj.getClass.getName.contains("$anon$")) obj.getClass.getSuperclass else obj.getClass
    val className = cls.getCanonicalName
    
    // Create the directory
    val dirName = spaceDir + File.separator + className.replaceAll("\\.", File.separator)
    new File(dirName).mkdirs
    
    val file = new File(dirName + File.separator + obj.uuid.toString)
    val format = FormatRegistry.get(cls.asInstanceOf[Class[Any]]).getOrElse(
        throw new Exception("No formatter found for " + className))
    
    // Write it
    val out = new BufferedOutputStream(new FileOutputStream(file))
    val target = new ByteArrayOutputStream();
    format.writes(target, obj)
    try {
      out.write(target.toByteArray)
    } finally {
      out.close
    }
  }
  
  private def readFromDisk(classPath: String, dir: File): Unit = {
    dir.listFiles.foreach(f => 
      if (f.isFile) {
        val uuid = UUID.fromString(f.getName)
        val cls = Class.forName(classPath.substring(1)) // OPTIMIZE
        addToCache(fromFile(cls.asInstanceOf[Class[Any]], f))
      } else readFromDisk(classPath + "." + f.getName, f)
    )
  }
  
  private def fromFile(cls: Class[Any], file: File): Spacy = {
    val format = FormatRegistry.get(cls.asInstanceOf[Class[Any]]).getOrElse(
        throw new Exception("No formatter found for " + cls.getName))
    
    val uuid = UUID.fromString(file.getName)
        
    val in = new BufferedInputStream(new FileInputStream(file))
    try {
      format.reads(in, uuid)
    } finally {
      in.close() 
    }
  }
  
  def find[T <: Spacy](u: UUID): T = {
    //Log.debug("Find " + u.toString)
    cache(u).asInstanceOf[T]
  }
  
  def find[T <: Spacy](implicit m: Manifest[T]): immutable.Set[T] = {
    //Log.debug("Find " + m.toString)
    val cls = Class.forName(m.toString)
    val matches = cache.values.filter(x => (cls.isAssignableFrom(x.getClass)))
    //Log.debug("Found: " + matches)
    matches.toSet.asInstanceOf[immutable.Set[T]]
  }
  
  def write[T <: Spacy](obj: T): T = {
    if (obj.valid) {
      this !? Write(obj)
      obj
    } else {
      throw ValidationException("Validation failed when storing " + obj.getClass + " " + obj.uuid)
    }
  }
  
  object FormatRegistry extends Logging {
    val registry = mutable.Map[Class[Any], SpacyFormat[Spacy]]()
    
    def get(cls: Class[Any]) = registry.get(cls)
    
    def register(cls: Class[Any], format: SpacyFormat[Spacy]) {
      //Log.debug("Registering spacy formatter for " + cls.getName)
      registry(cls) = format
    }
  }
}