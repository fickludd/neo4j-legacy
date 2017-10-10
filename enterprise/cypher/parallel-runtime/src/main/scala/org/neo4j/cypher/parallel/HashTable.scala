package org.neo4j.cypher.parallel

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class HashTable {

  val map: mutable.Map[Long, mutable.Map[String, ArrayBuffer[Long]]] = mutable.Map()

  def add(key:Long, row:Seq[(String, Long)]): Unit = {
    val values = map.getOrElseUpdate(key, mutable.Map())
    for (t <- row) {
      val column = values.getOrElseUpdate(t._1, new ArrayBuffer())
      column += t._2
    }
  }

  def contains(key:Long): Boolean = map.contains(key)
}
