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
package org.neo4j.cypher.internal.runtime.executable;

import org.neo4j.collection.primitive.PrimitiveIntSet;
import org.neo4j.cypher.internal.runtime.ColumnId;
import org.neo4j.cypher.internal.runtime.Morsel;
import org.neo4j.cypher.internal.runtime.columns.ReferenceColumn;
import org.neo4j.internal.kernel.api.NodeCursor;
import org.neo4j.internal.kernel.api.Read;
import org.neo4j.internal.kernel.api.RelationshipGroupCursor;
import org.neo4j.internal.kernel.api.RelationshipTraversalCursor;

public class ExecutableExpandAll extends ExecutablePusher
{
    Read read;

    RelationshipGroupCursor edgeGroupCursor;
    RelationshipTraversalCursor edgeCursor;
    NodeCursor nodeCursor;

    ColumnId originRefCol;
    ColumnId edgeGroupRefCol;
    ColumnId edgeRefCol;
    ColumnId targetRefCol;
    ColumnId relIdCol;
    ColumnId neighbourCol;

    Morsel source;
    int sourceIndex = -1;
//    PrimitiveIntSet edgeLabels;

    public ExecutableExpandAll( RelationshipGroupCursor edgeGroupCursor, ColumnId edgeGroupRefCol, ColumnId relIdCol,
            ColumnId
            neighbourCol )
    {
        this.edgeGroupCursor = edgeGroupCursor;
        this.edgeGroupRefCol = edgeGroupRefCol;
        this.relIdCol = relIdCol;
        this.neighbourCol = neighbourCol;
    }

    @Override
    public void process( Morsel result )
    {
        ReferenceColumn edgeGroupRef = source.refCol( edgeGroupRefCol );
        ReferenceColumn originRef = source.refCol( originRefCol );
        ReferenceColumn edgeRef = result.refCol( edgeRefCol );
        ReferenceColumn nodeRef = result.refCol( targetRefCol );

        int i = 0;

        // consume any cursors that did not fit in a previous result morsel
        i = consumeEdgeCursor( result, i, edgeRef, nodeRef );
        i = consumeRelationshipGroupCursor( result, i, nodeRef, edgeRef );

        while ( i < result.maxSize() && sourceIndex < source.size() )
        {
            // continue with next origin node
            sourceIndex++;
            long edgeGroup = edgeGroupRef.get( sourceIndex );
            long originNode = originRef.get( sourceIndex );
            read.relationshipGroups( originNode, edgeGroup, edgeGroupCursor );
            consumeRelationshipGroupCursor( result, i, nodeRef, edgeRef );
        }

        result.setSize( i );

        next.process( result );
    }

    private int consumeRelationshipGroupCursor( Morsel result, int resultIndex, ReferenceColumn nodeRef,
            ReferenceColumn
            edgeRef )
    {
        while ( edgeGroupCursor.next() && resultIndex < result.maxSize() )
        {
//            if ( edgeLabels.contains( edgeGroupCursor.edgeLabel() ) )
//            {
            edgeGroupCursor.outgoing( edgeCursor );
            resultIndex = consumeEdgeCursor( result, resultIndex, edgeRef, nodeRef );
//            }
        }
        return resultIndex;
    }

    private int consumeEdgeCursor( Morsel result, int resultIndex, ReferenceColumn edgeRef, ReferenceColumn originNodeRef )
    {
        while ( edgeCursor.next() && resultIndex < result.maxSize() )
        {
            edgeCursor.neighbour( nodeCursor );
            if ( nodeCursor.next() )
            {
                result.copyRowFromTo( source, sourceIndex, resultIndex );
                edgeRef.setAt( resultIndex, edgeCursor.relationshipReference() );
                originNodeRef.setAt( resultIndex, nodeCursor.nodeReference() );
                resultIndex++;
            }
        }
        return resultIndex;
    }
}
