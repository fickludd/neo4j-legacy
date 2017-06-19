package org.neo4j.cypher.internal.runtime;

public abstract class BufferController
{
    protected int size;
    protected final int maxSize;

    protected BufferController( int maxSize )
    {
        this.maxSize = maxSize;
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

}
