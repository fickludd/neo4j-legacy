package org.neo4j.cypher.internal.runtime;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.neo4j.cypher.internal.runtime.columns.FlagColumn;
import org.neo4j.cypher.internal.runtime.columns.GenericValueColumn;
import org.neo4j.cypher.internal.runtime.columns.LabelColumn;
import org.neo4j.cypher.internal.runtime.columns.ReferenceColumn;
import org.neo4j.cypher.internal.runtime.columns.ValueColumn;
import org.neo4j.values.Value;

public class Morsel
{
    private int size;
    private final int maxSize;
    private final FlagColumn inUse;
    private final FlagColumn tempFlag;
    private final Map<ColumnId, ReferenceColumn> refCols;
    private final Map<ColumnId, LabelColumn> labelCols;
    private final Map<ColumnId, ValueColumn> valueCols;

    public Morsel( int maxSize )
    {
        this.maxSize = maxSize;
        inUse = new FlagColumn( new boolean[maxSize] );
        tempFlag = new FlagColumn( new boolean[maxSize] );
        refCols = new HashMap<>();
        valueCols = new HashMap<>();
        labelCols = new HashMap<>();
    }

    public int maxSize()
    {
        return maxSize;
    }

    public void setSize( int size )
    {
        this.size = size;
    }

    public int size()
    {
        return size;
    }

    public FlagColumn inUse()
    {
        return inUse;
    }

    public ValueColumn column( ColumnId propertyCol )
    {
        return valueCols.computeIfAbsent( propertyCol,
                x -> new GenericValueColumn( new Value[maxSize] ) );
    }

    public FlagColumn tempFlagColumn()
    {
        return tempFlag;
    }

    public ReferenceColumn refCol( ColumnId nodeIdCol )
    {
        return refCols.computeIfAbsent( nodeIdCol,
                x -> new ReferenceColumn( new long[maxSize] ) );
    }

    public LabelColumn labelsCol( ColumnId labelIdCol )
    {
        return labelCols.computeIfAbsent( labelIdCol,
                x -> new LabelColumn( new long[maxSize] ) );
    }

    public ValueColumn[] columns( ColumnId[] propertyColIds )
    {
        return Arrays.stream( propertyColIds )
                .map( valueCols::get )
                .toArray( ValueColumn[]::new );
    }
}
