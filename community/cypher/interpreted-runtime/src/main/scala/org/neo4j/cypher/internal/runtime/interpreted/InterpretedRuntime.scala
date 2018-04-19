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

//import org.neo4j.cypher.internal.compatibility.v3_4.runtime.CommunityPipeBuilderFactory
import org.neo4j.cypher.internal.runtime.interpreted.commands.convert.{CommunityExpressionConverter, ExpressionConverters}
import org.neo4j.cypher.internal.runtime.{ExecutableQuery, PhysicalCompilationContext, QueryExecutionState, Runtime}
import org.neo4j.cypher.internal.runtime.interpreted.pipes.{NestedPipeExpression, Pipe, ProduceResultsPipe, QueryState}
import org.neo4j.cypher.internal.util.v3_4.{Rewriter, bottomUp}
import org.neo4j.cypher.internal.v3_4.expressions.Expression
import org.neo4j.cypher.internal.v3_4.logical.plans.{LogicalPlan, LogicalPlans, NestedPlanExpression}

object InterpretedRuntime {
  val EXPRESSION_CONVERTERS = new ExpressionConverters(CommunityExpressionConverter)
}

class InterpretedRuntime() extends Runtime[QueryState] {



  override def allocateExecutionState: QueryExecutionState = ???

  override def compileToExecutable(query: String,
                                   logicalPlan: LogicalPlan,
                                   context: PhysicalCompilationContext
                                  ): ExecutableQuery[QueryState] = {

    val resultsPipe = buildPipe(context)(logicalPlan).asInstanceOf[ProduceResultsPipe]
    InterpretedExecutableQuery(resultsPipe, context.periodicCommit)
  }

  private def buildPipe(context: PhysicalCompilationContext)(logicalPlan: LogicalPlan): Pipe = {
    val pipeBuilder = InterpretedPipeBuilder(buildPipe(context),
                                             context.readOnly,
                                             InterpretedRuntime.EXPRESSION_CONVERTERS,
                                             recursePipes(buildPipe(context)),
                                             context.tokenContext
                                        )(context.semanticTable)
    LogicalPlans.map(logicalPlan, pipeBuilder)
  }

  def recursePipes(recurse: LogicalPlan => Pipe)
                  (in: Expression): Expression = {

    val buildPipeExpressions = new Rewriter {
      private val instance = bottomUp(Rewriter.lift {
                                                      case expr@NestedPlanExpression(patternPlan, expression) =>
                                                        val pipe = recurse(patternPlan)
                                                        val result = NestedPipeExpression(pipe, expression)(expr.position)
                                                        result
                                                    })

      override def apply(that: AnyRef): AnyRef = instance.apply(that)
    }

    in.endoRewrite(buildPipeExpressions)
  }

  override def releaseExecutionState(executionState: QueryExecutionState): Unit = ???
}
