package org.neo4j.cypher.parallel

object Runtime {

  trait Dependency

  case class StreamingDependency() extends Dependency {

  }

  case class CompleteDependency() extends Dependency {

  }

  trait GraphState

  trait DependencyState

  case class Pipeline[RESULT](operator:Operators.Operator) {
  }

}
