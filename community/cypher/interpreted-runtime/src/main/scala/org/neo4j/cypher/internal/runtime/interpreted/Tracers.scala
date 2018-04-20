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

import org.neo4j.cypher.internal.util.v3_4.attribution.{Attribute, Id}
import org.neo4j.cypher.internal.v3_4.logical.plans.{AllNodesScan, LogicalPlan}
import org.neo4j.internal.kernel.api.tracers.KernelTracer

class Tracers extends Attribute[CountingTracer] {

  /**
    * Override attribute get() with NOOP tracer. This should go away once we know where to
    * create tracers for specific operators.
    */
  override def get(id: Id): CountingTracer = {
    if (isDefinedAt(id)) super.get(id)
    else CountingTracer.NOOP
  }

  def create(plan: LogicalPlan): Unit =
    plan match {
      case x: AllNodesScan =>
        set(x.id, new CountingTracer)
      case x => println(s"Cannot trace $x yet")
    }

}

class CountingTracer() extends KernelTracer {

  var count: Long = 0L

  /**
    * Called before NodeCursor.next() returns true.
    *
    * @param reference The new cursor reference.
    */
  override def trace(reference: Long): Unit = {
    count += 1;
  }
}

object CountingTracer {
  val NOOP = new CountingTracer {
    override def trace(reference: Long): Unit = {}
  }
}
