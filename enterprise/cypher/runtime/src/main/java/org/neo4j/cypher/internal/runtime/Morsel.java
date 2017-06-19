package org.neo4j.cypher.internal.runtime;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.neo4j.cypher.internal.runtime.columns.FlagColumn;
import org.neo4j.cypher.internal.runtime.columns.GenericValueColumn;
import org.neo4j.cypher.internal.runtime.columns.LabelColumn;
import org.neo4j.cypher.internal.runtime.columns.ReferenceColumn;
import org.neo4j.cypher.internal.runtime.columns.ValueColumn;

public class Morsel extends BufferController
{
    private final FlagColumn inUse;
    private final FlagColumn tempFlag;
    private final Map<ColumnId, ReferenceColumn> refCols;
    private final Map<ColumnId, LabelColumn> labelCols;
    private final Map<ColumnId, ValueColumn> valueCols;

    public Morsel( int maxSize )
    {
        super( maxSize );
        inUse = new FlagColumn( this );
        tempFlag = new FlagColumn( this );
        refCols = new HashMap<>();
        valueCols = new HashMap<>();
        labelCols = new HashMap<>();
    }

    public FlagColumn inUse()
    {
        return inUse;
    }

    public ValueColumn column( ColumnId propertyCol )
    {
        return valueCols.computeIfAbsent( propertyCol,
                x -> new GenericValueColumn( this ) );
    }

    public FlagColumn tempFlagColumn()
    {
        return tempFlag;
    }

    public ReferenceColumn refCol( ColumnId nodeIdCol )
    {
        return refCols.computeIfAbsent( nodeIdCol,
                x -> new ReferenceColumn( this ) );
    }

    public LabelColumn labelsCol( ColumnId labelIdCol )
    {
        return labelCols.computeIfAbsent( labelIdCol,
                x -> new LabelColumn( this ) );
    }

    public ValueColumn[] columns( ColumnId[] propertyColIds )
    {
        return Arrays.stream( propertyColIds )
                .map( valueCols::get )
                .toArray( ValueColumn[]::new );
    }
}
