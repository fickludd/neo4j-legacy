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

import org.neo4j.cypher.internal.util.v3_4.symbols.CypherType
import org.neo4j.cypher.internal.v3_4.expressions.Expression
import org.neo4j.cypher.internal.v3_4.logical.plans.LogicalPlan
import org.neo4j.values.AnyValue
import org.neo4j.values.virtual.MapValue

/**
  * Really a semantic table.
  */
trait ExpressionTypes {
  def typeFor(expression: Expression): CypherType
}

/**
  * Writes a result row into a serialized result.
  *
  * @tparam SERIALIZED_RESULT the type of serialized result that is produced.
  */
trait ResultWriter[SERIALIZED_RESULT] {

  /**
    * initialize next result.
    *
    * @param nValues the number of values in this result.
    */
  def initResult(nValues:Int): Unit

  /**
    * Write the next value of the result. This method will be called as many times are specified in initResult().
    *
    * @param value the Value to write.
    */
  def writeValue(value:AnyValue): Unit

  /**
    * Returns the serialized result.
    *
    * @return the serialized result.
    */
  def result(): SERIALIZED_RESULT
}

/**
  * Consumes results.
  *
  * @tparam SERIALIZED_RESULT the type of result that is consumed.
  */
trait ResultConsumer[SERIALIZED_RESULT] {

  /**
    * Consume the next result.
    *
    * @param result the result to consume.
    * @return true if this consumer can consume more values currently.
    */
  def onResult(result: SERIALIZED_RESULT): Boolean
}

/**
  * Representation of the execution of a query.
  *
  * @tparam RESULT the type of result rows that will be produced.
  */
trait QueryExecution[RESULT] {

  /**
    * The names of the result columns
    *
    * @return Array containing the names of the result columns in order.
    */
  def header(): Array[String]

  /**
    * Consume some result of the execution. For every available result ResultConsumer.onResult is called unless
    * onResult return false, in which case no further results are consumed.
    *
    * @param consumer The consumer which will act of the results.
    * @return The number of consumed results, or -1 if the last result has been consumed.
    */
  def consume(consumer: ResultConsumer[RESULT]): Int

  /**
    * Abort this execution, throwing away any buffered results, and releasing any other resources.
    */
  def abort(): Unit
}

/**
  * An executable representation of a query.
  *
  * The ExecutableQuery holds no mutable state, and is safe to cache, reuse and use concurrently.
  *
  * @tparam State The type of execution state needed to execute this query.
  */
trait ExecutableQuery[State <: QueryExecutionState] {
  def execute[RESULT](query:String, params: MapValue, state: State, resultWriter: ResultWriter[RESULT]): QueryExecution[RESULT]
}

/**
  * A QueryExecutionState holds the mutable state needed during execution of a query.
  *
  * QueryExecutionStates of the correct type are allocated and released by the relevant runtime.
  */
trait QueryExecutionState

/**
  * A runtime knows how to compile logical plans into executable queries. Executable queries are intended to be reused
  * for executing the same multiple times, also concurrently. To facilitate this, all execution state is held in a
  * QueryExecutionState object. The runtime has the power to allocate and release these execution states,
  *
  * @tparam State the execution state type for this runtime.
  */
trait Runtime[State <: QueryExecutionState] {

  def allocateExecutionState: QueryExecutionState
  def compileToExecutable(logicalPlan: LogicalPlan, expressionTypes: ExpressionTypes): ExecutableQuery[State]
  def releaseExecutionState(executionState: QueryExecutionState): Unit
}


