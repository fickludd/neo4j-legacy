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

import org.neo4j.cypher.internal.runtime._
import org.neo4j.cypher.internal.runtime.interpreted.pipes.{ProduceResultsPipe, QueryState}
import org.neo4j.internal.kernel.api.Transaction
import org.neo4j.values.virtual.MapValue

case class InterpretedExecutableQuery(pipe: ProduceResultsPipe,
                                      periodicCommit: Option[Long] = None
                                ) extends ExecutableQuery[QueryState] {

  override def execute(params: MapValue,
                       state: QueryState,
                       resultBufferManager: ResultBufferManager,
                       transaction: Option[Transaction]
                      ): QueryExecution = {

    val resultIterator = pipe.createResults(state)
    val resultBuffer = resultBufferManager.allocateResultBuffer(pipe.columns.size)

    new InterpretedQueryExecution(pipe.columns.toArray, resultBuffer, resultIterator)
  }
}
