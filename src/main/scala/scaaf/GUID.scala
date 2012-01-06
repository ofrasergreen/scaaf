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

import scala.actors.Actor
import Actor._

import scaaf.exchange.Exchange
import scaaf.exchange.Subscriber

import java.security._
import scaaf.cluster.LocalNode

/**
 * @author ofrasergreen
 *
 */
trait GUID {
  val g: (Long, Long)
  
  private var hashcode: Int = -1
  
  override def toString() = {
    "%s-%s-%s-%s-%s".format(
        rangeString(g._1, 0, 4),
        rangeString(g._1, 4, 2),
        rangeString(g._1, 6, 2),
        rangeString(g._2, 0, 2),
        rangeString(g._2, 2, 6))
  }
  
  override def hashCode() = {
    if (hashcode == -1) hashcode = (g._1 >> 32).toInt ^ g._1.toInt ^ (g._2 >> 32).toInt ^ g._2.toInt
    hashcode
  }
  
  override def equals(other: Any): Boolean = {
    if (!(other.isInstanceOf[GUID])) return false
    val o = other.asInstanceOf[GUID]
    return (g._2 == o.g._2 && g._1 == o.g._1)
  }
  
  private def rangeString(n: Long, start: Int, length: Int) = {
    val max: Long = (1L << (length * 8))
    java.lang.Long.toHexString(max | rangeLong(n, start, length)).substring(1)
  }
  
  protected def rangeLong(n: Long, start: Int, length: Int): Long = {
    val max: Long = (1L << (length * 8))
    (n >> ((8 - (start + length)) * 8)) & (max - 1)
  }
  
  def toBytes() = {
    val bytes = new Array[Byte](16)
    for (i <- 0 to 7) bytes(i) = ((g._1 >> ((7 - i) * 8)) & 0xff).toByte
    for (i <- 8 to 15) bytes(i) = ((g._2 >> ((15 - i) * 8)) & 0xff).toByte    
    bytes
  }
  
  def version = ((g._1 >>> 12) & 0x0f)
}

trait NodeReader extends GUID {
  def local: Boolean = (node == 0 || global)
  def global: Boolean = (node == 0xfff)
  
  def node = {
    (((g._1 >>> 8) & 0x0f) |
    (g._1 & 0xff) |
    ((g._2 >>> 56) & 0x0f) |
    ((g._2 >>> 48) & 0xff))
  }
}

trait ClassReader extends GUID {
  def cls: Int = rangeLong(g._1, 0, 4).toInt
}

trait TimestampReader extends GUID {
  def timestamp: Long = rangeLong(g._2, 2, 6)
}

trait SequenceReader extends GUID {
  def sequence: Int = rangeLong(g._1, 4, 2).toInt
}

trait DataReader extends GUID {
  def data: Long = (rangeLong(g._1, 4, 2) << 56) | rangeLong(g._2, 2, 6)
}

trait ObjID extends GUID with NodeReader with ClassReader
trait MsgID extends GUID with NodeReader with ClassReader with TimestampReader with SequenceReader
trait AddID extends GUID with NodeReader with ClassReader with DataReader

case class GetSequence()
case class Sequence(seq: Int)

object GUID extends Actor {
  val rng = new SecureRandom
  var seq: Int = 0
  this.start
  
  def act = loop {
    react {
      case GetSequence =>
        val s = Sequence(seq)
        seq += 1
        if (seq > 0xffff) seq = 0
        reply(s)
    }
  }
  
  def newFromTuple(_g: (Long, Long)) = {
    val version = ((_g._1 >>> 12) & 0x0f)
    version match {
      case 1 => new ObjID { override val g = _g }
      case 2 => new MsgID { override val g = _g }
      case 3 => new AddID { override val g = _g }
      case _ => throw new Exception("Unrecognized GUID version: " + version)
    }
  }
  
  private def fromBytes(bytes: Array[Byte]) = {
    var m = 0L
    var l = 0L
    
    for (i <- 0 to 7)  m = (m << 8) | (bytes(i) & 0xff)
    for (i <- 8 to 15)  l = (l << 8) | (bytes(i) & 0xff)
    
    (m, l)
  }
  
  private def setFlags(t: Byte, bytes: Array[Byte]) = {
    // Set the type
    bytes(6) = ((bytes(6) & 0x0f) | t << 4).toByte
    // Set the variant
    bytes(8) = ((bytes(8) & 0x1f) | 0xe0).toByte
    bytes
  }
  
  private def randomBytes() = {
    // Add location field
    val bytes = new Array[Byte](16)
    rng.nextBytes(bytes)
    bytes
  }
  
  private def setTimestamp(bytes: Array[Byte]) = {
    val t = System.currentTimeMillis
    for (i <- 10 to 15) bytes(i) = ((t >>> ((15 - i) * 8)) & 0xff).toByte
    bytes
  }
  
  private def nilBytes() = {
    new Array[Byte](16)
  }
  
  private def hashBytes(name: Array[Byte], length: Int) = {
    val sha1 = MessageDigest.getInstance("SHA-1")
    val bytes = sha1.digest(name)
    bytes.slice(0, length)
  }
    
  def newFromString(name: String) = {
    val parts = name.split("-")
    if (parts.length != 5) throw new IllegalArgumentException("Invalid UUID string: " + name)

    newFromTuple((java.lang.Long.parseLong(parts(0), 16) << 32) |
            (java.lang.Long.parseLong(parts(1), 16) << 16) |
            (java.lang.Long.parseLong(parts(2), 16)),
            (java.lang.Long.parseLong(parts(3), 16) << 48) |
            (java.lang.Long.parseLong(parts(4), 16)))
     
  }
  
  private def setData(data: Long, bytes: Array[Byte]) = {
    bytes(4) = ((data >>> 56) & 0xff).toByte
    bytes(5) = ((data >>> 48) & 0xff).toByte
    bytes(10) = ((data >>> 40) & 0xff).toByte
    bytes(11) = ((data >>> 32) & 0xff).toByte
    bytes(12) = ((data >>> 24) & 0xff).toByte
    bytes(13) = ((data >>> 16) & 0xff).toByte
    bytes(14) = ((data >>> 8) & 0xff).toByte
    bytes(15) = (data & 0xff).toByte
    bytes
  }
  
  private def setClass(cls: Class[_], bytes: Array[Byte]) = {
    val hc = cls.getName.hashCode
    bytes(0) = ((hc >>> 24) & 0xff).toByte
    bytes(1) = ((hc >>> 16) & 0xff).toByte
    bytes(2) = ((hc >>> 8) & 0xff).toByte
    bytes(3) = (hc & 0xff).toByte
    bytes
  }
  
  private def setNode(nodeID: Int, bytes: Array[Byte]) = {
    bytes(6) = ((nodeID >>> 20) & 0x0f).toByte
    bytes(7) = ((nodeID >>> 12) & 0xff).toByte
    bytes(8) = ((nodeID >>> 8) & 0x0f).toByte
    bytes(9) = (nodeID & 0xff).toByte
    bytes
  }
  
  private def setSequence(bytes: Array[Byte]) = {
    val seq: Int = this !? GetSequence match {
      case Sequence(s) => s
      case _ => 0
    }
    
    bytes(4) = ((seq >>> 8) & 0xff).toByte
    bytes(5) = (seq & 0xff).toByte
    bytes
  }
  
  // TODO: Fix with the actual node address
  def newObjID(cls: Class[_]): ObjID = newObjID(cls, LocalNode.ID)
  
  def newObjID(klass: Class[_], nodeID: Int): ObjID = new ObjID {
    override val g = fromBytes(
        setFlags(1, 
            setNode(nodeID,
                setClass(klass, randomBytes))))
  }
  
  def newMsgID(klass: Class[_]) = new MsgID {
    // TODO: Fix with the actual node address
    override val g = fromBytes(
        setFlags(2, 
            setNode(LocalNode.ID,
                setClass(klass,
                    setSequence(
                        setTimestamp(nilBytes))))))
  }
  
  def newAddID(_exchange: Class[_], nodeID: Int, _data: Long) = new AddID { 
    override val g = fromBytes(
        setFlags(3,
            setNode(nodeID, 
                setData(_data, 
                    setClass(_exchange, nilBytes)))))
  }
}