package org.neo4j.cypher.internal.runtime;

import org.neo4j.cypher.internal.runtime.kernel.NodeCursor;

public class TestNodeCursor implements NodeCursor
{
    int offset = -1;
    Node[] nodes;

    public TestNodeCursor setNodes( Node[] nodes )
    {
        this.nodes = nodes;
        offset = -1;
        return this;
    }

    @Override
    public boolean next()
    {
        offset++;
        return offset < nodes.length;
    }

    @Override
    public long getId()
    {
        return nodes[offset].id;
    }

    @Override
    public long labels()
    {
        return nodes[offset].id;
    }

    @Override
    public long edgeGroupReference()
    {
        return -1;
    }

    @Override
    public long propertyReference()
    {
        return nodes[offset].id;
    }
}
