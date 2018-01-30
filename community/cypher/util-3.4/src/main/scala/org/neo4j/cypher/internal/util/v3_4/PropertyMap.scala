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
  *  1) A property may be added and removed many times from a node. Thus every property key is associated with a time
  *     line of value sets (setting NO_VALUE means removing it). Every value set is associated with a version. Current
  *     version may be changed until that version is snapshoted.
  *
  */
class PropertyMap {

  type Value = AnyRef

  sealed trait Version
  case object Stable extends Version
  case object Active extends Version

  class ValueStates(var stable:Value, var active:Value) {
    def activeOrStable:Option[Value] =
      if (active != null) Some(active)
      else Option(stable)
  }

  class EntityState {
    val values:ArrayBuffer[ValueStates] = new ArrayBuffer()
    val ids:ArrayBuffer[Int] = new ArrayBuffer()
    val labels:ArrayBuffer[Boolean] = new ArrayBuffer()

    def foreachProperty(version:Version, f:(Int, Value) => ()): Unit = {
      var i = 0
      while (i < ids.length) {

      }
    }

    def put(propertyKey:Int, value:Value): Option[Value] = {
      ids.indexOf(propertyKey) match {
        case -1 =>
          ids += propertyKey
          values += new ValueStates(null, value)
          None

        case offset =>
          val valueStates = values(offset)

          val existing = valueStates.activeOrStable
          valueStates.active = value
          existing
      }
    }
  }

  val entityLookup:mutable.Map[Int, EntityState] = mutable.Map()
  var currentVersion:Int = 0

  def put(entityKey:Int, propertyKey:Int, value:Value): Option[Value] = {
    val entityState = entityLookup.getOrElseUpdate( entityKey, new EntityState )
    entityState.put( propertyKey, value )
  }
}
