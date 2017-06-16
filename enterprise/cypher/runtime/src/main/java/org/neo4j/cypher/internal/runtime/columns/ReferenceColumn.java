package org.neo4j.cypher.internal.runtime.columns;

public class ReferenceColumn
{
    final long[] references;

    public ReferenceColumn( long[] references )
    {
        this.references = references;
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
