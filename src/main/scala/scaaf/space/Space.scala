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

import scaaf.GUID
import scaaf.ObjID
import scala.collection._
import scaaf.logging._
import scala.actors._
import java.io._
import sbinary._
import DefaultProtocol._
import Operations._
import JavaIO._
import scala.reflect.Manifest
import scaaf.Configuration
import java.io.File

case class ValidationException(reason: String) extends Exception(reason) 

trait Spacy {
  val objID: ObjID = GUID.newObjID(this.getClass)
  def valid = true
}

// TODO: This shouldn't be a case class. Create a companion object for convenient construction
case class Ref[T <: Spacy](obj: T) {
  private var pobj = obj
  private var pObjID:ObjID = null
  
  def this(objID: ObjID) = { 
    this(null.asInstanceOf[T])
    this.pObjID = objID
  }
  
  def get = {
    if (obj == null) pobj = Space.find[T](pObjID)
    pobj
  }
  
  def objID = {
    if (pObjID == null) pObjID = pobj.objID 
    pObjID
  }
  
  override def hashCode(): Int = objID.hashCode
  override def equals(that: Any): Boolean = that match {
    case other: Ref[T] => (objID == other.objID)
    case _ => false
  }
  override def toString(): String = "Ref(%s)".format(objID.toString)
}

case class Write[T <: Spacy](obj: T)
case object Stats
case object Reboot
case object Done

object Space extends Actor with Logging {
  private val cache = mutable.Map[ObjID, Spacy]()
  private var stats = Statistics(0)
  private val spaceDir = Configuration.varDir + File.separator + "space"
  var memOnly = false
  
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
        cache.clear
        stats = Statistics(0)
        if (!memOnly) {
          val f = new File(spaceDir)
          f.mkdirs
          readFromDisk("", f)
        }
        reply(Done)
      }
    
  }
    
  private def addToCache(obj: Spacy) = {
    cache(obj.objID) = obj
    stats = stats copy (objectCount = stats.objectCount + 1)
  }
 
  def serialize(obj: Spacy, os: Output) {
    val cls: Class[_] = if (obj.getClass.getName.contains("$anon$")) obj.getClass.getSuperclass else obj.getClass
    val className = cls.getCanonicalName
    val format = FormatRegistry.getFormat(cls).getOrElse(
        throw new Exception("No formatter found for " + className))
    
    format.writes(os, obj)
  }
  
  private def writeToDisk(obj: Spacy): Unit = {
    //Log.debug("Writing " + obj.guid)
    // TODO - Find a cleaner way to do this
    val cls = if (obj.getClass.getName.contains("$anon$")) obj.getClass.getSuperclass else obj.getClass
    val className = cls.getCanonicalName
    
    // Create the directory
    val dirName = spaceDir + File.separator + className.replaceAll("\\.", File.separator)
    new File(dirName).mkdirs
    
    val file = new File(dirName + File.separator + obj.objID.toString)
    
    // Write it
    val out = new BufferedOutputStream(new FileOutputStream(file))
    val target = new ByteArrayOutputStream()
    // TODO: Write real headers
    SpacyProtocol.HeaderFormat.writes(target, new Header(1, 1, 1))
    serialize(obj, target)
    try {
      out.write(target.toByteArray)
    } finally {
      out.close
    }
  }
  
  private def readFromDisk(classPath: String, dir: File): Unit = {
    dir.listFiles.foreach(f => 
      if (f.isFile) {
        val cls = Class.forName(classPath.substring(1)) // OPTIMIZE
        addToCache(fromFile(cls.asInstanceOf[Class[Any]], f))
      } else readFromDisk(classPath + "." + f.getName, f)
    )
  }
  
  private def fromFile(cls: Class[Any], file: File): Spacy = {
    val format = FormatRegistry.getFormat(cls).getOrElse(
        throw new Exception("No formatter found for " + cls.getName))
    
    val objID = GUID.newFromString(file.getName).asInstanceOf[ObjID]
        
    val in = new BufferedInputStream(new FileInputStream(file))
    try {
      SpacyProtocol.HeaderFormat.reads(in)
      format.reads(in, objID)
    } finally {
      in.close() 
    }
  }
  
  def objectFromStream[T <: Spacy](in: InputStream, objID: ObjID)(implicit m: Manifest[T]): T = {
    val cls = Class.forName(m.toString)
    val format = FormatRegistry.getFormat(cls).getOrElse(
        throw new Exception("No formatter found for " + cls.getName))
    
    format.reads(in, objID).asInstanceOf[T]
  }
  
  def objectFromStream(in: Input, objID: ObjID): Spacy = {
    val format = FormatRegistry.getFormat(objID.cls).getOrElse(
        throw new Exception("No formatter found for " + objID.cls))
    
    format.reads(in, objID)
  }
  
  def find[T <: Spacy](objID: ObjID): T = {
    //Log.debug("Find " + u.toString)
    cache(objID).asInstanceOf[T]
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
      throw ValidationException("Validation failed when storing " + obj.getClass + " " + obj.objID)
    }
  }
  
  object FormatRegistry extends Logging {
    val classMap = mutable.Map[Int, SpacyFormat[Spacy]]()
    
    def getFormat(cls: Class[_]) = classMap.get(cls.getCanonicalName.hashCode)
    def getFormat(clsID: Int) = classMap.get(clsID)
    
    def register(cls: Class[_], format: SpacyFormat[Spacy]) {
      //Log.debug("Registering spacy formatter for " + cls.getName)
      classMap(cls.getCanonicalName.hashCode) = format
    }
  }
}