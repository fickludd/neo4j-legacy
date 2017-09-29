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

import org.neo4j.cypher.internal.runtime.ColumnId;
import org.neo4j.cypher.internal.runtime.Morsel;
import org.neo4j.cypher.internal.runtime.columns.LabelColumn;
import org.neo4j.cypher.internal.runtime.columns.ReferenceColumn;
import org.neo4j.internal.kernel.api.NodeCursor;

public class ExecutableAllNodeScan extends ExecutablePusher
{
    NodeCursor node;
    ColumnId nodeIdCol;
//    ColumnId labelIdCol;
    ColumnId edgeGroupRefCol;
    ColumnId propertyRefCol;

    public ExecutableAllNodeScan( NodeCursor node, ColumnId nodeIdCol, ColumnId labelIdCol, ColumnId edgeGroupRefCol,
            ColumnId propertyRefCol )
    {
        this.node = node;
        this.nodeIdCol = nodeIdCol;
//        this.labelIdCol = labelIdCol;
        this.edgeGroupRefCol = edgeGroupRefCol;
        this.propertyRefCol = propertyRefCol;
    }

    @Override
    public void process( Morsel morsel )
    {
        ReferenceColumn nodeIds = morsel.refCol( nodeIdCol );
//        LabelColumn labelIds = morsel.labelsCol( labelIdCol );
        ReferenceColumn edgeGroupRefs = morsel.refCol( edgeGroupRefCol );
        ReferenceColumn propertyRefs = morsel.refCol( propertyRefCol );

        int i = 0;
        while ( i < morsel.maxSize() && node.next() )
        {
            nodeIds.setAt( i, node.nodeReference() );
//            labelIds.setAt( i, node.labelReference() );
            edgeGroupRefs.setAt( i, node.relationshipGroupReference() );
            propertyRefs.setAt( i, node.propertiesReference() );
            i++;
        }
        morsel.setSize( i );

        next.process( morsel );
    }

//    private long labelReference( LabelCursor labels )
//    {
//        throw new UnsupportedOperationException( "How do we solve this?" );
//    }
}
