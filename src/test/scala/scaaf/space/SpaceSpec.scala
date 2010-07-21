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

import org.scalatest.WordSpec
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.MustMatchers
import scala.collection.mutable.Stack
import scaaf._
import space._
import test._

import sbinary._
import DefaultProtocol._
import Operations._

import java.io._
import java.util.UUID

class Foo(val a: String, val b: Int) extends Spacy
class Bar(val foo: Ref[Foo]) extends Spacy

object FooProtocol extends SpacyProtocol {      
  implicit object FooFormat extends SpacyFormat[Foo] {
    def reads(in : Input, u: UUID) =  {
      read[Header](in)
      new Foo(read[String](in), read[Int](in)) {
        override val uuid = u
      }
    }
    
    def writes(out : Output, foo: Foo) = {
      write(out, Header(1, 1, 1))
      write(out, foo.a)
      write(out, foo.b)
    }    
  }
 
  implicit object BarFormat extends SpacyFormat[Bar] {
    def reads(in : Input, u: UUID) =  {
      read[Header](in)
      new Bar(read[Ref[Foo]](in)) {
        override val uuid = u
      }
    }
    
    def writes(out : Output, bar: Bar) = {
      write(out, Header(1, 1, 1))
      write(out, bar.foo)
    }
  }
}

class SpaceSpec extends WordSpec with MustMatchers with BeforeAndAfterEach with InitSpec {
  Space.FormatRegistry.register(classOf[Foo].asInstanceOf[Class[Any]], FooProtocol.FooFormat.asInstanceOf[SpacyFormat[Spacy]])
  Space.FormatRegistry.register(classOf[Bar].asInstanceOf[Class[Any]], FooProtocol.BarFormat.asInstanceOf[SpacyFormat[Spacy]])

  override def bootstrap = {
    Space.memOnly = false
    server.bootstrap
  }
  
  def deleteAll(dir: File): Unit = {
    dir.listFiles.foreach(f => {
      if (f.isDirectory) deleteAll(f) 
      f.delete
    })
  }
  
  override def beforeEach = {
    deleteAll(new File("test/space"))
    Space !? Reboot
  }
  
  "The Space" should {
    
    "contain no objects when initialized." in {
      assert(Space.statistics.objectCount === 0)
    }
    "contain exactly one object when one is inserted immediately after inialization." in {
      Space.write(new Foo("test1", 42))
      Space !? Stats match {
        case s: Statistics =>
          assert(s.objectCount === 1)
        case _ =>
          fail("Stats didn't tell us anything.")
      } 
    }
    
    "contain one object immediately after reboot if there was one immediately before shutdown." in {
      Space.write(new Foo("test2", 43))
      Space ! Reboot
      Space !? Stats match {
        case s: Statistics =>
          assert(s.objectCount === 1)
        case _ =>
          fail("Stats didn't tell us anything.")
      }
    }
    
    "be able to store and retrieve objects with a Ref" in {
      val foo = new Foo("test4", 3)
      val bar = new Bar(new Ref(foo))
      Space.write(foo)
      Space.write(bar)
      Space !? Reboot
      val bar2 = Space.find[Bar](bar.uuid)
      assert(bar2.foo.get.uuid === foo.uuid)
      assert(bar2.foo.get.a === foo.a)
      assert(bar2.foo.get.b === foo.b)
    }
  }
  
  
  "Space.find" should {
    "return an object referenced by UUID which has just been written." in {
      val foo = new Foo("test3", 42)
      Space.write(foo)
      Space !? Reboot
      val foo2 = Space.find[Foo](foo.uuid)
      assert(foo.uuid === foo2.uuid)
      assert(foo.a === foo2.a)
      assert(foo.b === foo2.b)
    }
    
    "return an object referenced by class which has just been written." in {
      val foo = new Foo("test3", 42)
      Space.write(foo)
      Space !? Reboot
      val results = Space.find[Foo]
      assert(results.size === 1)
      val foo2 = results.head
      assert(foo.uuid === foo2.uuid)
      assert(foo.a === foo2.a)
      assert(foo.b === foo2.b)
    }
    
    "should only return items which are the same class or a subclass of the queries one." in {
      val foo = new Foo("test4", 3)
      val bar = new Bar(new Ref(foo))
      Space.write(foo)
      Space.write(bar)
      Space !? Reboot
      val results = Space.find[Foo]
      assert(results.size === 1)
      val foo2 = results.head
      assert(foo.uuid === foo2.uuid)
      assert(foo.a === foo2.a)
      assert(foo.b === foo2.b)
    }
    
    "return an empty set when finding a class for which there are no objects." in {
      val results = Space.find[Foo]
      assert(results.size === 0)
    }
    
    "throw ObjectNotFoundException if asked to read an object which doesn't exist." is (pending)
  }
  
  "Space.reboot" should {
    "pass over but log when reading corrupt files" is (pending)
  }
}