package org.neo4j.cypher.internal.runtime.kernel;

public interface PropertyCursor
{
    PropertyCursor NOOP = new PropertyCursor()
    {
        @Override
        public boolean next()
        {
            return false;
        }

        @Override
        public int keyId()
        {
            return 0;
        }

        @Override
        public void valueTo( ValueSink sink )
        {
        }
    };

    boolean next();

    int keyId();

    void valueTo( ValueSink sink );
}
