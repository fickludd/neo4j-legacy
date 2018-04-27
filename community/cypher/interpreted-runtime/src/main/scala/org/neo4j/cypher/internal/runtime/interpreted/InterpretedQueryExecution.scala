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
package org.neo4j.cypher.internal.runtime.interpreted

import org.neo4j.kernel.impl.query.{QueryExecution, ResultBuffer}

class InterpretedQueryExecution(override val header: Array[String],
                                override val resultBuffer: ResultBuffer,
                                val resultIterator: Iterator[ExecutionContext]) extends QueryExecution {

  override def waitForResult(): Boolean = {
    if (resultIterator.hasNext) {
      val row = resultIterator.next()
      val resultRowId = resultBuffer.prepareResultStage()
      if (resultRowId >= 0) {
        var i = 0
        while (i < header.length) {
          val columnName = header(i)
          resultBuffer.writeValueToStage(i, row(columnName))
          i += 1
        }
        resultBuffer.commitResultStage()
      }
    }
    false
  }

  override def terminate(): Unit = ???
}
