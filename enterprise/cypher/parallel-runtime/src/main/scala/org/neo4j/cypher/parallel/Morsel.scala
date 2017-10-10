package org.neo4j.cypher.parallel

import scala.collection.mutable

class Morsel(val nRows:Int) {

  val columns: mutable.Map[String, Array[Long]] = mutable.Map()

  def setAt(name:String, row:Int, value:Long): Unit = {
    val column = columns.getOrElseUpdate(name, new Array[Long](nRows))
    column(row) = value
  }

  override def toString:String = {
    val sb = new mutable.StringBuilder()
    sb ++= columns.keys.map("%8s".format(_)).mkString("", " ", "\n")
    for (i <- 0 until nRows)
      sb ++= columns.keys.map(key => "%8d".format(columns(key)(i))).mkString("", " ", "\n")
    sb.result
  }


}
