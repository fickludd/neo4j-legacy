package org.neo4j.cypher.internal.runtime;

public class Morsel
{

    private int size;

    public long[] refCol( ColumnId nodeIdCol )
    {
        return new long[0];
    }

    public long[] labelsOrRefCol( ColumnId labelIdCol )
    {
        return new long[0];
    }

    public int maxSize()
    {
        return 0;
    }

    public void setSize( int size )
    {
        this.size = size;
    }
}
