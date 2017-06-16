package org.neo4j.cypher.internal.runtime;

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
        long[] nodeIds = morsel.refCol( nodeIdCol );
        long[] labelIds = morsel.labelsOrRefCol( labelIdCol );
        long[] edgeGroupRefs = morsel.refCol( edgeGroupRefCol );
        long[] propertyRefs = morsel.refCol( propertyRefCol );

        int i = 0;
        while ( i < morsel.maxSize() && node.next() )
        {
            nodeIds[i] = node.getId();
            labelIds[i] = node.labels();
            edgeGroupRefs[i] = node.edgeGroupReference();
            propertyRefs[i] = node.propertyReference();
            i++;
        }
        morsel.setSize( i );

        next.process( morsel );
    }
}
