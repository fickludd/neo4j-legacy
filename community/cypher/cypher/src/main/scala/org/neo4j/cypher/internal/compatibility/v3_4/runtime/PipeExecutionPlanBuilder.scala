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
package org.neo4j.cypher.internal.compatibility.v3_4.runtime

import org.neo4j.cypher.internal.compatibility.v3_4.runtime.executionplan._
import org.neo4j.cypher.internal.ir.v3_4.PeriodicCommit
import org.neo4j.cypher.internal.planner.v3_4.spi.TokenContext
import org.neo4j.cypher.internal.runtime.interpreted.InterpretedPipeBuilder
import org.neo4j.cypher.internal.runtime.interpreted.commands.convert.ExpressionConverters
import org.neo4j.cypher.internal.runtime.interpreted.pipes.{Pipe, PipeBuilderFactory, PipeExecutionBuilderContext}
import org.neo4j.cypher.internal.v3_4.logical.plans.{LogicalPlan, LogicalPlans, Limit => LimitPlan, LoadCSV => LoadCSVPlan, Skip => SkipPlan}

class PipeExecutionPlanBuilder(pipeBuilderFactory: PipeBuilderFactory,
                               expressionConverters: ExpressionConverters) {
  def build(periodicCommit: Option[PeriodicCommit], plan: LogicalPlan)
           (implicit context: PipeExecutionBuilderContext, tokenContext: TokenContext): PipeInfo = {

    val topLevelPipe = buildPipe(plan)

    val periodicCommitInfo = periodicCommit.map(x => PeriodicCommitInfo(x.batchSize))
    PipeInfo(topLevelPipe, periodicCommitInfo)
  }

  private def buildPipe(plan: LogicalPlan)(implicit context: PipeExecutionBuilderContext, tokenContext: TokenContext): Pipe = {
    val pipeBuilder = pipeBuilderFactory(recurse = p => buildPipe(p),
                                         readOnly = context.readOnly,
                                         expressionConverters = expressionConverters)
    LogicalPlans.map(plan, pipeBuilder)
  }
}

object InterpretedPipeBuilderFactory extends PipeBuilderFactory {
  def apply(recurse: LogicalPlan => Pipe,
            readOnly: Boolean,
            expressionConverters: ExpressionConverters)
           (implicit context: PipeExecutionBuilderContext, tokenContext: TokenContext): InterpretedPipeBuilder = {
    InterpretedPipeBuilder(recurse, readOnly, expressionConverters, recursePipes(recurse), tokenContext)(context.semanticTable)
  }
}
