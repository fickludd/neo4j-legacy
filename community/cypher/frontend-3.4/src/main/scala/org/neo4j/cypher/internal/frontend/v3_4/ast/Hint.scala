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
package org.neo4j.cypher.internal.frontend.v3_4.ast

import org.neo4j.cypher.internal.util.v3_4.{ASTNode, InputPosition, InternalException, NonEmptyList}
import org.neo4j.cypher.internal.frontend.v3_4.semantics.{SemanticAnalysisTooling, SemanticCheckable}
import org.neo4j.cypher.internal.util.v3_4.symbols._
import org.neo4j.cypher.internal.v3_4.expressions._

sealed trait Hint extends ASTNode with SemanticCheckable with SemanticAnalysisTooling {
  def variables: NonEmptyList[VarLike]
}

trait NodeHint {
  self: Hint =>
}

trait RelationshipHint {
  self: Hint =>
}

object Hint {
  implicit val byVariable: Ordering[Hint] =
    Ordering.by { (hint: Hint) => hint.variables.head }(Variable.byName)
}
// allowed on match

sealed trait UsingHint extends Hint

// allowed on start item

sealed trait ExplicitIndexHint extends UsingHint {
  self: StartItem =>

  def variable: VarLike
  def variables = NonEmptyList(variable)
}

case class UsingIndexHint(
                           variable: VarLoad,
                           label: LabelName,
                           properties: Seq[PropertyKeyName]
                         )(val position: InputPosition) extends UsingHint with NodeHint {
  def variables = NonEmptyList(variable)
  def semanticCheck = ensureDefined(variable) chain expectType(CTNode.covariant, variable)

  override def toString: String = s"USING INDEX ${variable.name}:${label.name}(${properties.map(_.name).mkString(", ")})"
}

case class UsingScanHint(variable: VarLoad, label: LabelName)(val position: InputPosition) extends UsingHint with NodeHint {
  def variables = NonEmptyList(variable)
  def semanticCheck = ensureDefined(variable) chain expectType(CTNode.covariant, variable)

  override def toString: String = s"USING SCAN ${variable.name}:${label.name}"
}

object UsingJoinHint {
  import NonEmptyList._

  def apply(elts: Seq[VarLoad])(pos: InputPosition): UsingJoinHint =
    UsingJoinHint(elts.toNonEmptyListOption.getOrElse(throw new InternalException("Expected non-empty sequence of variables")))(pos)
}

case class UsingJoinHint(variables: NonEmptyList[VarLoad])(val position: InputPosition) extends UsingHint with NodeHint {
  def semanticCheck =
    variables.map { variable => ensureDefined(variable) chain expectType(CTNode.covariant, variable) }.reduceLeft(_ chain _)

  override def toString: String = s"USING JOIN ON ${variables.map(_.name).toIndexedSeq.mkString(", ")}"
}

// start items

sealed trait StartItem extends ASTNode with SemanticCheckable with SemanticAnalysisTooling {
  def variable: VarDeclare
  def name = variable.name
}

sealed trait NodeStartItem extends StartItem {
  def semanticCheck = declareVariable(variable, CTNode)
}

case class NodeByIdentifiedIndex(variable: VarDeclare, index: String, key: String, value: Expression)(val position: InputPosition)
  extends NodeStartItem with ExplicitIndexHint with NodeHint

case class NodeByIndexQuery(variable: VarDeclare, index: String, query: Expression)(val position: InputPosition)
  extends NodeStartItem with ExplicitIndexHint with NodeHint

case class NodeByParameter(variable: VarDeclare, parameter: Parameter)(val position: InputPosition) extends NodeStartItem
case class AllNodes(variable: VarDeclare)(val position: InputPosition) extends NodeStartItem

sealed trait RelationshipStartItem extends StartItem {
  def semanticCheck = declareVariable(variable, CTRelationship)
}

case class RelationshipByIds(variable: VarDeclare, ids: Seq[UnsignedIntegerLiteral])
                            (val position: InputPosition) extends RelationshipStartItem
case class RelationshipByParameter(variable: VarDeclare, parameter: Parameter)
                                  (val position: InputPosition) extends RelationshipStartItem
case class AllRelationships(variable: VarDeclare)(val position: InputPosition) extends RelationshipStartItem
case class RelationshipByIdentifiedIndex(variable: VarDeclare, index: String, key: String, value: Expression)
                                        (val position: InputPosition) extends RelationshipStartItem
                                                                      with ExplicitIndexHint with RelationshipHint
case class RelationshipByIndexQuery(variable: VarDeclare, index: String, query: Expression)
                                   (val position: InputPosition) extends RelationshipStartItem
                                                                 with ExplicitIndexHint with RelationshipHint

// no longer supported non-hint legacy start items

case class NodeByIds(variable: VarDeclare, ids: Seq[UnsignedIntegerLiteral])
                    (val position: InputPosition) extends NodeStartItem

