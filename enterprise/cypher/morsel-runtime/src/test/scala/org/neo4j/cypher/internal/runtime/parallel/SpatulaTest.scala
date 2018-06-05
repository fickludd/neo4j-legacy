package org.neo4j.cypher.internal.runtime.parallel

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

import org.neo4j.cypher.internal.runtime.vectorized.Morsel
import org.opencypher.v9_0.util.test_helpers.CypherFunSuite

import scala.collection.JavaConversions._

class SpatulaTest extends CypherFunSuite {

  test("execute a bunch of things") {

    val s = new ASpatula( 1 )

    val testThread = Thread.currentThread().getId
    val taskThreadId = new AtomicLong(testThread)

    val sb = new StringBuilder
    val queryExecution = s.execute(NoopTask(() => {
      sb ++= "great success"
      taskThreadId.set(Thread.currentThread().getId)
    }))

    queryExecution.await()

    sb.result() should equal("great success")
    taskThreadId.get() should not equal(testThread)
  }

  test("execute more things") {
    val concurrency = 4
    val s = new ASpatula( concurrency )

    val map = new ConcurrentHashMap[Int, Long]()
    val futures =
      for ( i <- 0 until 1000 ) yield
        s.execute(NoopTask(() => {
          map.put(i, Thread.currentThread().getId)
      }))

    futures.foreach(f => f.await())

    val countsPerThread = map.toSeq.groupBy(kv => kv._2).mapValues(_.size)
    for ((threadId, count) <- countsPerThread) {
      count should be > 200
    }
  }

  test("execute a subtask thing") {

    val s = new ASpatula( 2 )

    val testThread = Thread.currentThread().getId
    val taskThreadId = new AtomicLong(testThread)

    val sb = new StringBuilder
    val queryExecution = s.execute(
      SubTasker(List(
        NoopTask(() => sb ++= " once"),
        NoopTask(() => sb ++= " upon"),
        NoopTask(() => sb ++= " a"),
        NoopTask(() => sb ++= " time")
      )))

    queryExecution.await()
    println(sb.result())
  }

  case class SubTasker(subtasks: Seq[Task]) extends Task {

    private val taskSequence = subtasks.iterator

    override def executeWorkUnit(morsel: Morsel): Seq[Task] =
      if (taskSequence.hasNext) List(taskSequence.next())
      else Nil

    override def canContinue(): Boolean = taskSequence.nonEmpty
  }

  case class NoopTask(f: () => Any) extends Task {
    override def executeWorkUnit(morsel: Morsel): Seq[Task] = {
      f()
      Nil
    }

    override def canContinue(): Boolean = false
  }
}
