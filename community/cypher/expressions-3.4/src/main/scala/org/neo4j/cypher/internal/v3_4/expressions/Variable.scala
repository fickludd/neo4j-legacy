/*
 * Copyright (c) 2002-2017 "Neo Technology,"
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
package org.neo4j.cypher.internal.v3_4.expressions

import org.neo4j.cypher.internal.util.v3_4.{ASTNode, InputPosition}

case class Variable(name: String)(val position: InputPosition) extends LogicalVariable {

  override def copyId = copy()(position)

  override def renameId(newName: String) = copy(name = newName)(position)

  override def bumpId = copy()(position.bumped())

  override def asCanonicalStringVal: String = name
}

object Variable {
  implicit val byName: Ordering[VarLike] =
    Ordering.by { (variable: VarLike) =>
      (variable.name, variable.position)
    }(Ordering.Tuple2(implicitly[Ordering[String]], InputPosition.byOffset))
}

object VarDeclare {
  def of(v: VarLike): VarDeclare = VarDeclare(v.name)(v.position)
}

case class VarDeclare(name: String)(val position: InputPosition) extends ASTNode with VarLike {

}

object VarLoad {
  def of(v: VarLike): VarLoad = VarLoad(v.name)(v.position)
}

case class VarLoad(name: String)(val position: InputPosition) extends LogicalVariable with VarLike {

  override def copyId = copy()(position)

  override def renameId(newName: String) = copy(name = newName)(position)

  override def bumpId = copy()(position.bumped())

  override def asCanonicalStringVal: String = name
}

case class VarAmbiguous(name: String)(val position: InputPosition) extends ASTNode with VarLike {

}

trait VarLike {
  def name: String
  def position: InputPosition

  def load: VarLoad = VarLoad(name)(position)

  def declare: VarDeclare = VarDeclare(name)(position)
}
