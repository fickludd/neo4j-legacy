package org.neo4j.cypher.internal.runtime;

import org.neo4j.cypher.internal.runtime.columns.LabelColumn;
import org.neo4j.cypher.internal.runtime.columns.ReferenceColumn;
import org.neo4j.cypher.internal.runtime.columns.ValueColumn;
import org.neo4j.cypher.internal.runtime.columns.FlagColumn;

public class Morsel
{
    private int size;

    public int maxSize()
    {
        throw new UnsupportedOperationException("needs love");
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
        throw new UnsupportedOperationException("needs love");
    }

    public void setValue( ColumnId propertyCol, PropertyCursor property )
    {
        throw new UnsupportedOperationException("needs love");
    }

    public ValueColumn column( ColumnId propertyCol )
    {
        throw new UnsupportedOperationException("needs love");
    }

    public FlagColumn tempFlagColumn()
    {
        throw new UnsupportedOperationException("needs love");
    }

    public ReferenceColumn refCol( ColumnId nodeIdCol )
    {
        throw new UnsupportedOperationException("needs love");
    }

    public LabelColumn labelsCol( ColumnId labelIdCol )
    {
        throw new UnsupportedOperationException("needs love");
    }

    public ValueColumn[] columns( ColumnId[] propertyColIds )
    {
        throw new UnsupportedOperationException("needs love");
    }
}
