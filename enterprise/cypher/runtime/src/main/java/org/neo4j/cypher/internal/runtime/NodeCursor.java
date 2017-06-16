package org.neo4j.cypher.internal.runtime;

public class NodeCursor
{
    public boolean next()
    {
        return false;
    }

    public long getId()
    {
        return 0;
    }

    public long labels()
    {
        return 0;
    }

    public long edgeGroupReference()
    {
        return 0;
    }

    public long propertyReference()
    {
        return 0;
    }
}
