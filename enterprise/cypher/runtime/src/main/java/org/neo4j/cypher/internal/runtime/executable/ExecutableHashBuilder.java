package org.neo4j.cypher.internal.runtime.executable;

import java.util.Set;

import org.neo4j.cypher.internal.runtime.ColumnId;
import org.neo4j.cypher.internal.runtime.Morsel;
import org.neo4j.cypher.internal.runtime.PropertyCursor;
import org.neo4j.cypher.internal.runtime.ReadOps;
import org.neo4j.cypher.internal.runtime.columns.ReferenceColumn;
import org.neo4j.cypher.internal.runtime.columns.ValueColumn;

public class ExecutableHashBuilder extends ExecutablePusher
{
    ReadOps readOps;
    ColumnId nodeIdCol;
    Set<Long> nodeIdSet; // I know it does work like this...

    @Override
    void process( Morsel morsel )
    {
        ReferenceColumn nodeRefs = morsel.refCol( nodeIdCol );

        for ( int i = 0; i < morsel.size(); i++ )
        {
            nodeIdSet.add( nodeRefs.get( i ) );
        }
    }
}
