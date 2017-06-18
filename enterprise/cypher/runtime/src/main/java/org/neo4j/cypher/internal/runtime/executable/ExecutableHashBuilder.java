package org.neo4j.cypher.internal.runtime.executable;

import java.util.Set;

import org.neo4j.cypher.internal.runtime.ColumnId;
import org.neo4j.cypher.internal.runtime.Morsel;
import org.neo4j.cypher.internal.runtime.columns.ReferenceColumn;

public class ExecutableHashBuilder extends ExecutablePusher
{
    ColumnId nodeIdCol;
    Set<Long> nodeIdSet; // I know it does work like this...

    public ExecutableHashBuilder( ColumnId nodeIdCol, Set<Long> nodeIdSet )
    {
        this.nodeIdCol = nodeIdCol;
        this.nodeIdSet = nodeIdSet;
    }

    @Override
    public void process( Morsel morsel )
    {
        ReferenceColumn nodeRefs = morsel.refCol( nodeIdCol );

        for ( int i = 0; i < morsel.size(); i++ )
        {
            nodeIdSet.add( nodeRefs.get( i ) );
        }
    }
}
