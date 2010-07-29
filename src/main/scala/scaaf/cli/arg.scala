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

/**
 * @author ofrasergreen
 *
 */
class Arg(
    val label: String,
    val description: String,
    val repeated: Boolean,
    val optional: Boolean,
    val klass: Class[_])

class OptionArg(
    val abbreviation: Option[Char],
    val longName: Option[String],
    label: String,
    description: String,
    repeated: Boolean,
    klass: Class[_]
) extends Arg(label, description, repeated, true, klass)
