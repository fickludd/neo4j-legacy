package org.neo4j.cypher.internal.runtime;

import org.neo4j.cypher.internal.runtime.columns.LabelColumn;
import org.neo4j.cypher.internal.runtime.columns.ReferenceColumn;

public class ExecutableAllNodeScan extends ExecutablePusher
{
    NodeCursor node;
    ColumnId nodeIdCol;
    ColumnId labelIdCol;
    ColumnId edgeGroupRefCol;
    ColumnId propertyRefCol;

    @Override
    void process( Morsel morsel )
    {
        ReferenceColumn nodeIds = morsel.refCol( nodeIdCol );
        LabelColumn labelIds = morsel.labelsCol( labelIdCol );
        ReferenceColumn edgeGroupRefs = morsel.refCol( edgeGroupRefCol );
        ReferenceColumn propertyRefs = morsel.refCol( propertyRefCol );

        int i = 0;
        while ( i < morsel.maxSize() && node.next() )
        {
            nodeIds.setAt( i, node.getId() );
            labelIds.setAt( i, node.labels() );
            edgeGroupRefs.setAt( i, node.edgeGroupReference() );
            propertyRefs.setAt( i, node.propertyReference() );
            i++;
        }
        morsel.setSize( i );

        next.process( morsel );
    }
}
