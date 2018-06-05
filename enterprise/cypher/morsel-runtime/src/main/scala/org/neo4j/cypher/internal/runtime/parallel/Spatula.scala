package org.neo4j.cypher.internal.runtime.parallel

import java.util
import java.util.concurrent._

import org.neo4j.cypher.internal.runtime.vectorized.Morsel

trait Spatula {

  def execute(task: Task): QueryExecution
}

trait Task {

  def executeWorkUnit( morsel: Morsel ): Seq[Task]
  def canContinue(): Boolean
}

trait ExecutableQuery {
  def initialTask(): Task
}

trait QueryExecution {
  def await(): Unit
}

class ASpatula(val concurrency: Int) extends Spatula {

  private val fishslice = new ExecutorCompletionService[Void](Executors.newFixedThreadPool(concurrency))
//
  override def execute(task: Task): QueryExecution = {

    val future: util.concurrent.Future[Void] = fishslice.submit(wrapMe(task))

    new QueryExecution {
      override def await(): Unit = future.get(30, TimeUnit.SECONDS)
    }
  }

  def wrapMe(task: Task) : Callable[Void] = {
    new Callable[Void] {
      override def call(): Void = {
        val tasks = task.executeWorkUnit(null)
        tasks.foreach(t => fishslice.submit(wrapMe(t)))
        return null
      }
    }
  }
}

