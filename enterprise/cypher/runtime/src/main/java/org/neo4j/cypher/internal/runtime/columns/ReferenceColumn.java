package org.neo4j.cypher.internal.runtime.columns;

import org.neo4j.cypher.internal.runtime.BufferController;

public class ReferenceColumn
{
    final long[] references;
    final BufferController bufferController;

    public ReferenceColumn( BufferController bufferController )
    {
        this.references = new long[bufferController.maxSize()];
        this.bufferController = bufferController;
    }

    public void setAt( int offset, long reference )
    {
        references[offset] = reference;
    }

    public long get( int offset )
    {
        return references[offset];
    }
}
