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
package org.neo4j.cypher.internal.runtime

import org.neo4j.cypher.internal.frontend.v3_4.semantics.SemanticTable
import org.neo4j.cypher.internal.planner.v3_4.spi.TokenContext
import org.neo4j.cypher.internal.util.v3_4.symbols.CypherType
import org.neo4j.cypher.internal.v3_4.expressions.Expression
import org.neo4j.cypher.internal.v3_4.logical.plans.LogicalPlan
import org.neo4j.internal.kernel.api.Transaction
import org.neo4j.values.AnyValue
import org.neo4j.values.virtual.MapValue

// =============================================== /
// RUNTIME INTERFACES, implemented by each runtime /
// _______________________________________________ /

/**
  * A runtime knows how to compile logical plans into executable queries. Executable queries are intended to be reused
  * for executing the same multiple times, also concurrently. To facilitate this, all execution state is held in a
  * QueryExecutionState object. The runtime has the power to allocate and release these execution states,
  *
  * @tparam State the execution state type for this runtime.
  */
trait Runtime[State <: QueryExecutionState] {

  def allocateExecutionState: QueryExecutionState
  def compileToExecutable(query: String, logicalPlan: LogicalPlan, context: PhysicalCompilationContext): ExecutableQuery[State]
  def releaseExecutionState(executionState: QueryExecutionState): Unit
}

/**
  * An executable representation of a query.
  *
  * The ExecutableQuery holds no mutable state, and is safe to cache, reuse and use concurrently.
  *
  * @tparam State The type of execution state needed to execute this query.
  */
trait ExecutableQuery[State <: QueryExecutionState] {

  /**
    * Execute this query.
    *
    * @param params Parameters of the execution.
    * @param state The execution state to use.
    * @param resultBufferManager A result buffer manager, that will be used to allocate a ResultBuffer with
    *                            the correct number of output columns.
    * @param transaction The transaction to execute the query in. If None, a new transaction will be begun
    *                    for the duration of this execution.
    * @return A QueryExecution representing the started exeucution.
    */
  def execute( params: MapValue,
                       state: State,
                       resultBufferManager: ResultBufferManager,
                       transaction: Option[Transaction]
                     ): QueryExecution
}

/**
  * Representation of the execution of a query.
  */
trait QueryExecution {

  /**
    * The names of the result columns
    *
    * @return Array containing the names of the result columns in order.
    */
  def header(): Array[String]

  /**
    * Returns the result buffer of this execution.
    * @return the result buffer of this execution.
    */
  def resultBuffer(): ResultBuffer

  /**
    * Abort this execution, throwing away any buffered results, and releasing any other resources.
    */
  def abort(): Unit
}

/**
  * A QueryExecutionState holds the mutable state needed during execution of a query.
  *
  * QueryExecutionStates of the correct type are allocated and released by the relevant runtime.
  */
trait QueryExecutionState

// ====================================================== /
// SUPPORTING INTERFACES, implemented by execution engine /
// ______________________________________________________ /

/**
  * Really a semantic table.
  */
trait PhysicalCompilationContext {
  def typeFor(expression: Expression): CypherType
  def semanticTable: SemanticTable
  def readOnly: Boolean
  def tokenContext: TokenContext
  def periodicCommit: Option[Long]
}

/**
  * Manages result buffers.
  */
trait ResultBufferManager {
  def allocateResultBuffer(nValues: Int): ResultBuffer
  def releaseResultBuffer(buffer: ResultBuffer)
}

/**
  * Result buffer holding the results of a query execution.
  */
trait ResultBuffer {

  /**
    * Returns the number of values per result for this result buffer.
    * @return the number of values per result for this result buffer.
    */
  def valuesPerResult: Int

  /**
    * Prepare the result stage for the next result row.
    *
    * @return the id of the new row, or -1 if the stage could not be cleared.
    */
  def prepareResultStage(): Long

  /**
    * Write a value of the result stage.
    *
    * This method is expected to be called for every valueId in the range [0..valuesPerResult), for every row.
    *
    * @param columnId column id of the value to write.
    * @param value the Value to write.
    */
  def writeValueToStage(columnId: Int, value: AnyValue): Unit

  /**
    * Commit the result row in the result stage to the buffer.
    *
    * @return true if the buffer can accept the next result row.
    */
  def commitResultStage(): Boolean
}

