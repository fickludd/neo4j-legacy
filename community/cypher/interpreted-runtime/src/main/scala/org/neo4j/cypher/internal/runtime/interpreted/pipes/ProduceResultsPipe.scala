/*
 * Copyright (c) 2002-2018 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.cypher.internal.runtime.interpreted.pipes

import org.neo4j.cypher.internal.runtime.interpreted.{ExecutionContext, MutableMaps}
import org.neo4j.cypher.internal.util.v3_4.attribution.Id
import org.neo4j.kernel.impl.query.ResultBuffer

case class ProduceResultsPipe(source: Pipe, columns: Seq[String])
                             (val id: Id = Id.INVALID_ID) extends PipeWithSource(source) {
  protected def internalCreateResults(input: Iterator[ExecutionContext], state: QueryState) = {
    // do not register this pipe as parent as it does not do anything except filtering of already fetched
    // key-value pairs and thus should not have any stats

    input.map {
      original =>
        val m = MutableMaps.create(columns.size)
        columns.foreach {
          case (name) => m.put(name, original(name))
        }

        ExecutionContext(m)
    }
  }

  def execute(state: QueryState, resultBuffer: ResultBuffer): Execution = {
    new Execution(columns.toArray, source.createResults(state), resultBuffer)
  }
}

class Execution(columns: Array[String],
                sourceResult: Iterator[ExecutionContext],
                resultBuffer: ResultBuffer) {

  def nextRow(): Boolean = {
    if (sourceResult.hasNext) {
      val resultRowId = resultBuffer.prepareResultStage()
      if (resultRowId >= 0) {
        val next = sourceResult.next()
        var i = 0
        while (i < columns.length) {
          resultBuffer.writeValueToStage(i, next(columns(i)))
          i += 1
        }
        return resultBuffer.commitResultStage()
      }
    }
    false
  }
}
