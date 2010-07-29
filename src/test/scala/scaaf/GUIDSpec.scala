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

import org.scalatest.WordSpec
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.MustMatchers
import scala.collection.mutable.Stack
import scaaf._
import space._
import test._


/**
 * @author ofrasergreen
 *
 */
class GUIDSpec extends WordSpec with MustMatchers with BeforeAndAfterEach with InitSpec {
  override def beforeEach = reset

  "An AddID" should {
    val g = GUID.newAddID(getClass, 123, 0)
    
    "have the correct version field" in {
      g.version must equal (3)
    }
    
    "have the correct exchange ID" in {
      g.cls must equal (getClass.getCanonicalName.hashCode)
    }
    
    "have the correct node ID" in {
      g.node must equal (123)
    }
  }
  
  "An ObjID" should {
    val g = GUID.newObjID(getClass)
    
    "have 1 in the version field" in {
      g.version must equal (1)
    }
    
    "have this class ID" in {
      g.cls must equal (getClass.getCanonicalName.hashCode)
    }
  }
  
  "A MsgID" should {
    val g = GUID.newMsgID(getClass)
    
    "have 2 in the version field" in {
      g.version must equal (2)
    }
    
    "a timestamp that's fairly recent" in {
      (System.currentTimeMillis - g.timestamp) must be < (100L) 
    }
    
    "have this class ID" in {
      g.cls must equal (getClass.getCanonicalName.hashCode)
    }
  }
  
  "Two MsgIDs generated in succession" should {
    "have consequetive sequence numbers" in {
      val a = GUID.newMsgID(getClass)
      val b = GUID.newMsgID(getClass)
      
      (b.sequence - a.sequence) must equal (1)
    }
  }
  
  "A GUID created from a string" should {
    val gs = "b8245a06-4a64-2f43-ebcc-0abf53b09014"
    val guid = GUID.newFromString(gs)

    "return the correct msb" in {
      guid.g._1 must equal (-5177914688504844477L)
    }
      
    "return the correct lsb" in {
      guid.g._2 must equal (-1455776762688401388L)
    } 
    
    "return the correct string" in {
      guid.toString must equal (gs)
    }
  }
}