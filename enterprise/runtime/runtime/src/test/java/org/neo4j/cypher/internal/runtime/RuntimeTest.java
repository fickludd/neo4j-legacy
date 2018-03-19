/*
 * Copyright (c) 2002-2017 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.cypher.internal.runtime;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.cypher.internal.runtime.columns.FlagColumn;
import org.neo4j.cypher.internal.runtime.columns.ReferenceColumn;
import org.neo4j.cypher.internal.runtime.executable.ExecutableAllNodeScan;
import org.neo4j.cypher.internal.runtime.executable.ExecutableExpandAll;
import org.neo4j.graphdb.*;
import org.neo4j.internal.kernel.api.KernelAPIReadTestBase;
import org.neo4j.internal.kernel.api.NodeCursor;
import org.neo4j.kernel.impl.newapi.ReadTestSupport;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RuntimeTest extends KernelAPIReadTestBase<ReadTestSupport>
{
    private static List<Long> NODE_IDS;
    public static final int LABEL = 1;

    @Override
    public ReadTestSupport newTestSupport()
    {
        return new ReadTestSupport();
    }

    @Override
    protected void createTestGraph( GraphDatabaseService graphDb )
    {
        NODE_IDS = new ArrayList<>();
        try ( Transaction tx = graphDb.beginTx() )
        {
            for ( int i = 0; i < 18; i++ )
            {
                NODE_IDS.add( graphDb.createNode().getId() );
            }
            NODE_IDS.add( graphDb.createNode( Label.label( "LABEL" ) ).getId() );

            tx.success();
        }

        try ( Transaction tx = graphDb.beginTx() )
        {
            for ( int i = 0; i < NODE_IDS.size(); i++ )
            {
                long id = NODE_IDS.get(i);
                switch (i % 3) {
                case 0:
                    relate(graphDb, i, (i+3)%NODE_IDS.size());
                    relate(graphDb, i, (i+2)%NODE_IDS.size());

                case 1:
                    relate(graphDb, i, (i+1)%NODE_IDS.size());

                case 2:
                }

            }

            tx.success();
        }
    }

    private void relate( GraphDatabaseService graphDb, int a, int b )
    {
        Node node1 = graphDb.getNodeById( NODE_IDS.get(a) );
        Node node2 = graphDb.getNodeById( NODE_IDS.get(b) );
        node1.createRelationshipTo( node2, RelationshipType.withName( "R" ) );
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
        NodeCursor scanCursor = cursors.allocateNodeCursor();
        read.allNodesScan( scanCursor );
        ExecutableAllNodeScan scan = new ExecutableAllNodeScan( scanCursor, n, nLabels, nEdges, nProperties );
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
    public void expandAll()
    {
        // given
        ColumnId n = new ColumnId( "n" );
        ColumnId nLabels = new ColumnId( "n:" );
        ColumnId nEdges = new ColumnId( "n-->" );
        ColumnId nProperties = new ColumnId( "n{}" );
        NodeCursor scanCursor = cursors.allocateNodeCursor();
        read.allNodesScan( scanCursor );
        ExecutableAllNodeScan scan = new ExecutableAllNodeScan( scanCursor, n, nLabels, nEdges, nProperties );

        ColumnId rGroup = new ColumnId( "rGroupRef" );
        ColumnId rRef = new ColumnId( "rRef" );
        ColumnId n2 = new ColumnId( "n2" );
        ExecutableExpandAll expand = new ExecutableExpandAll(
                cursors.allocateRelationshipGroupCursor(),
                rGroup, rRef, n2 );
        scan.next = expand;

        Collector collector = new Collector();
        expand.next = collector;

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
        for ( int i = 0; i < N_NODES() - 1; i++ )
        {
            assertFalse( "nodes not in use", inUse.array()[i] );
        }
        assertTrue( "node with label in use", inUse.array()[N_NODES() - 1] );
    }

    private static int N_NODES()
    {
        return NODE_IDS.size();
    }
}
