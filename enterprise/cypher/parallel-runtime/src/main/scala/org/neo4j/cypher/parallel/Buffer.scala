package org.neo4j.cypher.parallel

import scala.collection.mutable.ArrayBuffer

class Buffer[T]() {

  var list = new ArrayBuffer[T]

  def add(t:T): Unit = list += t
  def take():T = list.remove(0)
  def size:Long = list.size

  override def toString:String =
    list.map(_.toString).mkString("\n")
}
