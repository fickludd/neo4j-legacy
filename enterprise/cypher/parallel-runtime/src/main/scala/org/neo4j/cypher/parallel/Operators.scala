package org.neo4j.cypher.parallel

object Operators {

  trait Continuation {
    def execute(morsel: Morsel): Continuation
    def canContinue: Boolean
  }

  trait Operator {
    def init():Continuation
  }

  trait BatchOperator[IN, OUT] {
    def execute(input: Buffer[IN], output:Buffer[OUT], outRowSize:Int): Unit
  }

  /**
    * All nodes scan operator
    */
  class AllNodes(nodeName:String, nNodes:Long) extends Operator {

    class X() extends Continuation {
      var nodeId:Long = 0

      override def execute(morsel: Morsel): Continuation = {
        var i:Int = 0
        while (nodeId < nNodes && i < morsel.nRows) {
          morsel.setAt(nodeName, i, nodeId)
          nodeId += 1
          i += 1
        }
        this
      }

      override def canContinue: Boolean = nodeId + 1 < nNodes
    }

    def init():Continuation = new X()
  }

  /**
    * Expand all operator
    */
  class ExpandAll(
                   dependency:Buffer[Morsel],
                   nodeName:String,
                   relName:String,
                   otherName:String,
                   scaleFactor:Int
                 ) extends Operator {

    class X(dependency:Morsel) extends Continuation {
      private var dependencyIndex = 0
      private var startNodes = dependency.columns(nodeName)
      private var rel:Int = 0

      override def execute(morsel: Morsel): Continuation = {
        var i:Int = 0
        while (dependencyIndex < dependency.nRows && i < morsel.nRows) {
          val n = startNodes(dependencyIndex)
          val r = (n+1) * 1000 + rel
          val other = r * 1000
          morsel.setAt(nodeName, i, n)
          morsel.setAt(relName, i, r)
          morsel.setAt(otherName, i, other)
          rel += 1
          if (rel == scaleFactor) {
            rel = 0
            dependencyIndex += 1
          }
          i += 1
        }
        this
      }

      override def canContinue: Boolean = dependencyIndex < dependency.nRows
    }

    override def init(): Continuation = new X(dependency.take())
  }

  /**
    * Sort operator
    */
  class Sort(
             dependency:Buffer[Morsel],
             sortName:String
            ) extends BatchOperator[Morsel, Morsel] {

    case class MorselIndex(morsel:Morsel, index:Int)

    override def execute(input: Buffer[Morsel], output:Buffer[Morsel], outRowSize:Int): Unit = {
      val indices =
        for {
          m <- input.list
          i <- 0 until m.nRows
        } yield MorselIndex(m, i)

      val sorted = indices.sortBy( x => x.morsel.columns(sortName)(x.index) )

      var i = 0
      var m = new Morsel(outRowSize)
      for ( x <- sorted ) {
        for ( column <- x.morsel.columns)
          m.setAt(column._1, i, column._2(x.index))
        i += 1
        if (i == outRowSize) {
          i = 0
          output.add(m)
          m = new Morsel(outRowSize)
        }
      }
      if (i != 0) output.add(m)
    }
  }

  /**
    * Hash build operator
    */
  class HashBuild(
                   dependency:Buffer[Morsel],
                   hashName:String
                 ) extends BatchOperator[Morsel, HashTable] {

    override def execute(
                          input: Buffer[Morsel],
                          output: Buffer[HashTable],
                          outRowSize: Int
                        ): Unit = {

      var result:HashTable = new HashTable()

      for {
        m <- input.list
        i <- 0 until m.nRows
      } {
        val key = m.columns(hashName)(i)
        val row = m.columns.toList.map(kv => (kv._1, kv._2(i)))
        result.add(key, row)
      }

      output.add(result)
    }
  }

  /**
    * Hash filter operator
    */
  class HashFilter(
                 dependency: Buffer[HashTable],
                 hashName:String
                 ) extends Operator {

    private val lookup = dependency.take()

    class X() extends Continuation {
      override def execute(morsel: Morsel): Continuation = {
        var i = 0
        var j = 0
        while (i < morsel.nRows) {
          val probe = morsel.columns(hashName)(i)
          if (lookup.contains(probe)) {
            if (i != j) {
              for (column <- morsel.columns.values)
                column(j) = column(i)
            }
            j += 1
          }
          i += 1
        }
        this
      }

      override def canContinue: Boolean = true
    }

    override def init(): Continuation = new X()
  }
}
