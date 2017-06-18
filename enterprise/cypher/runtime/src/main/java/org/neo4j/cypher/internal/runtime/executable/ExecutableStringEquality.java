package org.neo4j.cypher.internal.runtime.executable;

import org.neo4j.cypher.internal.runtime.ColumnId;
import org.neo4j.cypher.internal.runtime.Morsel;
import org.neo4j.cypher.internal.runtime.columns.ValueColumn;
import org.neo4j.cypher.internal.runtime.columns.FlagColumn;

public class ExecutableStringEquality extends ExecutablePusher
{
    String predicate;
    ColumnId propertyColId;

    public ExecutableStringEquality( String predicate, ColumnId propertyColId )
    {
        this.predicate = predicate;
        this.propertyColId = propertyColId;
    }

    @Override
    public void process( Morsel morsel )
    {
        ValueColumn propertyCol = morsel.column( propertyColId );
        FlagColumn result = morsel.tempFlagColumn();
        FlagColumn inUse = morsel.inUse();

        propertyCol.eq( predicate, result.array() );
        inUse.and( result.array(), inUse.array() );

        next.process( morsel );
    }
}
