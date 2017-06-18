package org.neo4j.cypher.internal.runtime.executable;

import org.neo4j.cypher.internal.runtime.ColumnId;
import org.neo4j.cypher.internal.runtime.kernel.LabelCursor;
import org.neo4j.cypher.internal.runtime.Morsel;
import org.neo4j.cypher.internal.runtime.columns.FlagColumn;
import org.neo4j.cypher.internal.runtime.columns.LabelColumn;
import org.neo4j.cypher.internal.runtime.kernel.ReadOps;

public class ExecutableHasLabel extends ExecutablePusher
{
    int labelId;
    ColumnId labelCol;
    LabelCursor labels;
    final ReadOps readOps;

    public ExecutableHasLabel( int labelId, ColumnId labelCol, LabelCursor labels, ReadOps readOps )
    {
        this.labelId = labelId;
        this.labelCol = labelCol;
        this.labels = labels;
        this.readOps = readOps;
    }

    @Override
    public void process( Morsel morsel )
    {
        LabelColumn propertyCol = morsel.labelsCol( labelCol );
        FlagColumn result = morsel.tempFlagColumn();
        FlagColumn inUse = morsel.inUse();

        propertyCol.hasLabel( labelId, result.array(), labels, readOps );
        inUse.and( result.array(), inUse.array() );

        next.process( morsel );
    }
}
