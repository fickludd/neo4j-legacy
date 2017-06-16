package org.neo4j.cypher.internal.runtime;

public class PropertyCursor
{
    public static final PropertyCursor NOOP = null;

    public boolean next()
    {
        throw new UnsupportedOperationException( "needs love" );
    }

    public int keyId()
    {
        throw new UnsupportedOperationException( "needs love" );
    }

    public void valueTo( ValueSink sink )
    {
        throw new UnsupportedOperationException( "needs love" );
    }
}
