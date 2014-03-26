/**
 * Copyright 2014 EPAM Systems
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.flowforwarding.warp.controller.session

import scala.util.Try

trait OFMessage // Marker trait

trait MessageDriver[T <: OFMessage]{
  def getDPID(in: Array[Byte]): Try[Long]
  def decodeMessage(in: Array[Byte]): Try[T]
  def encodeMessage(dict: T): Try[Array[Byte]]
  val versionCode: Short
}

trait MessageDriverFactory[T <: OFMessage]{
  def get(versionCode: Short): Option[MessageDriver[T]]

  def getVersion(msg: Array[Byte]): Option[Short] = Try((msg(0).toShort & 0xff).toShort).toOption
  def get(msg: Array[Byte]): Option[MessageDriver[T]] = getVersion(msg).flatMap(get)
  //def get(version: String): MessageDriver[T]
}