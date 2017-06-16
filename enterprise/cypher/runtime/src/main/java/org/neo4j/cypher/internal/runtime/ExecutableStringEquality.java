package org.neo4j.cypher.internal.runtime;

import org.neo4j.cypher.internal.runtime.columns.ValueColumn;
import org.neo4j.cypher.internal.runtime.columns.FlagColumn;

public class ExecutableStringEquality extends ExecutablePusher
{
    String predicate;
    ColumnId propertyColId;

    @Override
    void process( Morsel morsel )
    {
        ValueColumn propertyCol = morsel.column( propertyColId );
        FlagColumn result = morsel.tempFlagColumn();
        FlagColumn inUse = morsel.inUse();

        propertyCol.eq( predicate, result.array() );
        inUse.and( result.array(), inUse.array() );

        next.process( morsel );
    }
}
