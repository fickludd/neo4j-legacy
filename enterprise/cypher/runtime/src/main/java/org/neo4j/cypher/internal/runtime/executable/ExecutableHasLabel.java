package org.neo4j.cypher.internal.runtime.executable;

import org.neo4j.cypher.internal.runtime.ColumnId;
import org.neo4j.cypher.internal.runtime.LabelCursor;
import org.neo4j.cypher.internal.runtime.Morsel;
import org.neo4j.cypher.internal.runtime.columns.FlagColumn;
import org.neo4j.cypher.internal.runtime.columns.LabelColumn;
import org.neo4j.cypher.internal.runtime.columns.ValueColumn;

public class ExecutableHasLabel extends ExecutablePusher
{
    int labelId;
    ColumnId labelCol;
    LabelCursor labels;

    @Override
    void process( Morsel morsel )
    {
        LabelColumn propertyCol = morsel.labelsCol( labelCol );
        FlagColumn result = morsel.tempFlagColumn();
        FlagColumn inUse = morsel.inUse();

        propertyCol.hasLabel( labelId, result.array(), labels );
        inUse.and( result.array(), inUse.array() );

        next.process( morsel );
    }
}
