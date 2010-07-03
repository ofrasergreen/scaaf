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

package scaaf.logging

import org.slf4j.{Logger, LoggerFactory}
import org.apache.log4j.BasicConfigurator

trait Logging {
  private val log = LoggerFactory.getLogger(getClass)
  
  object Log {
    // Initialize log4j logging
    BasicConfigurator.configure
    
    def trace(message:String, values:Any*) = 
        log.trace(message, values.map(_.asInstanceOf[Object]).toArray)
    def trace(message:String, error:Throwable) = log.trace(message, error)
    def trace(message:String, error:Throwable, values:Any*) =
        log.trace(message, error, values.map(_.asInstanceOf[Object]).toArray)
   
    def debug(message:String, values:Any*) =
        log.debug(message, values.map(_.asInstanceOf[Object]).toArray)
    def debug(message:String, error:Throwable) = log.debug(message, error)
    def debug(message:String, error:Throwable, values:Any*) = 
        log.debug(message, error, values.map(_.asInstanceOf[Object]).toArray)
   
    def info(message:String, values:Any*) =
        log.info(message, values.map(_.asInstanceOf[Object]).toArray)
    def info(message:String, error:Throwable) = log.info(message, error)
    def info(message:String, error:Throwable, values:Any*) = 
        log.info(message, error, values.map(_.asInstanceOf[Object]).toArray)
   
    def warn(message:String, values:Any*) =
        log.warn(message, values.map(_.asInstanceOf[Object]).toArray)
    def warn(message:String, error:Throwable) = log.warn(message, error)
    def warn(message:String, error:Throwable, values:Any*) = 
        log.warn(message, error, values.map(_.asInstanceOf[Object]).toArray)
   
    def error(message:String, values:Any*) = 
        log.error(message, values.map(_.asInstanceOf[Object]).toArray)
    def error(message:String, error:Throwable) = log.error(message, error)
    def error(message:String, error:Throwable, values:Any*) =
        log.error(message, error, values.map(_.asInstanceOf[Object]).toArray)
  }
}