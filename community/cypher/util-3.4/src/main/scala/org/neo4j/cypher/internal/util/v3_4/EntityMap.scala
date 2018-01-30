/*
 * Copyright (c) 2002-2018 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.neo4j.cypher.internal.util.v3_4

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * Assumptions
  *  1) An entity with key k is created once and then deleted. k will not be reused during the lifetime of this map.
  *  2) An entity can be deleted without being added. This marks a deletion relative store.
  *  3) Only current version is modified, snapshots are read-only.
  *
  * @tparam VALUE
  */
class EntityMap[VALUE] {

  val lookup:mutable.Map[Int, Int] = mutable.Map[Int, Int]()
  val values:ArrayBuffer[VALUE] = new ArrayBuffer[VALUE]()
  val deleteVersion:ArrayBuffer[Int] = new ArrayBuffer[Int]()
  var currentVersion:Int = 0

  def put(key:Int, value:VALUE): Option[VALUE] = {

    val oldValue: Option[VALUE] =
      lookup.get( key ).map(
        previousValueOffset => {
          deleteVersion(previousValueOffset) = currentVersion
          values(previousValueOffset)
        } )

    val newValueOffset = values.size
    lookup.put( key, newValueOffset )
    values += value
    deleteVersion += Int.MaxValue
    oldValue
  }

  def get(key:Int, version:Int): Option[VALUE] = {
    lookup.get( key )
      .filter(offset => deleteVersion(offset) > version)
      .map(offset => values(offset))
  }

  def remove(key:Int): Option[VALUE] = {
    lookup.get( key ) match {
      case Some(valueOffset) =>
        deleteVersion(valueOffset) = currentVersion
        Some(values(valueOffset))

      case None =>
        val deletedValueOffset = values.size
        values += values.last // should really null it here
        deleteVersion += currentVersion
        lookup.put( key, deletedValueOffset )
        None
    }
  }

  def isRemoved(key:Int, version:Int): Boolean = {
    lookup.get(key).exists(valueOffset => {
      deleteVersion(valueOffset) <= version
    })
  }

  def foreach(version:Int, f:VALUE => ()): Unit = {
    var i = 0
    while (i < version) {
      if (deleteVersion(i) >= version) {
        f(values(i))
      }
      i += 1
    }
  }

  def getSnapshot():Int = {
    val version = values.size
    currentVersion = version + 1
    version
  }
}
