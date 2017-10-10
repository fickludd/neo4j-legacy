package org.neo4j.cypher.parallel

import org.scalatest.FunSuite

class RuntimeTest extends FunSuite {

  test("allNodeScan") {
    val x = new Operators.AllNodes("n", 13)
    var cont = x.init()
    val resultBuffer = new Buffer[Morsel]

    while (cont.canContinue) {
      val morsel = new Morsel(5)
      cont = cont.execute(morsel)
      resultBuffer.add(morsel)
    }

    println( resultBuffer.toString )
    assert(resultBuffer.size == 3)
    assertColumn(resultBuffer.take(), "n", Array[Long](0,1,2,3,4))
    assertColumn(resultBuffer.take(), "n", Array[Long](5,6,7,8,9))
    assertColumn(resultBuffer.take(), "n", Array[Long](10,11,12,0,0))
  }

  test("expand") {
    val inBuffer = new Buffer[Morsel]
    inBuffer.add(morsel("a", Array(0, 1, 2, 3, 4, 5, 6)))

    val resultBuffer = new Buffer[Morsel]

    val x = new Operators.ExpandAll(inBuffer, "a", "r", "b", 3)

    var cont = x.init()
    while (cont.canContinue) {
      val morsel = new Morsel(5)
      cont = cont.execute(morsel)
      resultBuffer.add(morsel)
    }

    assert(resultBuffer.size == 5)
  }

  test("sort") {
    val inBuffer = new Buffer[Morsel]
    inBuffer.add(morsel("a", Array(8,6,4,2,1), "b", Array(10,20,30,40,50)))
    inBuffer.add(morsel("a", Array(7,3,5,2,1), "b", Array(15,33,15,40,50)))
    val outBuffer = new Buffer[Morsel]

    val x = new Operators.Sort(inBuffer, "a")
    x.execute(inBuffer, outBuffer, 4)

    assert(outBuffer.size == 3)
  }

  test("hash build") {
    val inBuffer = new Buffer[Morsel]
    val outBuffer = new Buffer[HashTable]

    inBuffer.add(morsel("a", Array(8,6,4,4,2,1), "b", Array(10,20,31,32,40,50)))

    val x = new Operators.HashBuild(inBuffer, "a")
    x.execute(inBuffer, outBuffer, -1)

    assert(outBuffer.size == 1)
    val result = outBuffer.take()
    for (key <- List(1,2,4,6,8)) assert(result.contains(key))
    for (notKey <- List(10,20,31,32,40,50)) assert(!result.contains(notKey))
  }

  def morsel(name:String, nodeIds: Seq[Int]): Morsel = {
    val m = new Morsel(nodeIds.size)
    for (i <- nodeIds.indices) m.setAt(name, i, nodeIds(i))
    m
  }

  def morsel(aName:String, aNodeIds: Seq[Int], bName:String, bNodeIds: Seq[Int]): Morsel = {
    val m = new Morsel(aNodeIds.size)
    for (i <- aNodeIds.indices) {
      m.setAt(aName, i, aNodeIds(i))
      m.setAt(bName, i, bNodeIds(i))
    }
    m
  }

  private def assertColumn(morsel: Morsel, name: String, expected: Array[Long]) = {
    assert(morsel.columns(name) sameElements expected)
  }
}
