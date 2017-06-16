package org.neo4j.cypher.internal.runtime.columns;

public class LabelColumn
{
    final long[] references;

    public LabelColumn( long[] references )
    {
        this.references = references;
    }

    public void setAt( int offset, long reference )
    {
        references[offset] = reference;
    }
}
