package org.neo4j.cypher.internal.runtime;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.cypher.internal.runtime.columns.FlagColumn;
import org.neo4j.cypher.internal.runtime.columns.ReferenceColumn;
import org.neo4j.cypher.internal.runtime.executable.ExecutableAllNodeScan;
import org.neo4j.cypher.internal.runtime.executable.ExecutableHasLabel;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RuntimeTest
{
    public static final int LABEL = 1;
    public static Graph graph;
    public static long id;

    @BeforeClass
    public static void setup()
    {
        List<Node> nodes = new ArrayList<>();
        for ( int i = 0; i < 16; i++ )
        {
            nodes.add( new Node( id++ ) );
        }
        nodes.add( new Node( id++, LABEL ) );

        graph = new Graph( nodes.toArray( new Node[0]) );
    }

    @Test
    public void allNodeScan()
    {
        // given
        Collector collector = new Collector();
        ColumnId n = new ColumnId( "n" );
        ColumnId nLabels = new ColumnId( "n:" );
        ColumnId nEdges = new ColumnId( "n-->" );
        ColumnId nProperties = new ColumnId( "n{}" );
        ExecutableAllNodeScan scan = new ExecutableAllNodeScan(
                new TestNodeCursor().setNodes( graph.nodes ),
                n, nLabels, nEdges, nProperties );
        scan.next = collector;

        // when
        Morsel morsel = new Morsel( 100 );
        scan.process( morsel );

        // then
        assertEquals( "number of nodes scanned", N_NODES(), morsel.size() );
        ReferenceColumn nCol = morsel.refCol( n );
        for ( int i = 0; i < N_NODES(); i++ )
        {
            assertEquals( "node id is offset", i, nCol.get( i ) );
        }
    }

    @Test
    public void labelFilter()
    {
        // given
        ColumnId n = new ColumnId( "n" );
        ColumnId nLabels = new ColumnId( "n:" );
        ColumnId nEdges = new ColumnId( "n-->" );
        ColumnId nProperties = new ColumnId( "n{}" );
        ExecutableAllNodeScan scan = new ExecutableAllNodeScan(
                new TestNodeCursor().setNodes( graph.nodes ),
                n, nLabels, nEdges, nProperties );

        Collector collector = new Collector();

        ExecutableHasLabel filter =
                new ExecutableHasLabel( LABEL, nLabels, new TestLabelCursor(), graph );

        scan.next = filter;
        filter.next = collector;

        // when
        Morsel morsel = new Morsel( 100 );
        scan.process( morsel );

        // then
        assertEquals( "number of rows", N_NODES(), morsel.size() );
        ReferenceColumn nCol = morsel.refCol( n );
        for ( int i = 0; i < N_NODES(); i++ )
        {
            assertEquals( "node id is offset", i, nCol.get( i ) );
        }
        FlagColumn inUse = morsel.inUse();
        for ( int i = 0; i < N_NODES()-1; i++ )
        {
            assertFalse( "nodes not in use", inUse.array()[i] );
        }
        assertTrue( "node with label in use", inUse.array()[N_NODES()-1] );
    }

    private static int N_NODES()
    {
        return (int)id;
    }

}
