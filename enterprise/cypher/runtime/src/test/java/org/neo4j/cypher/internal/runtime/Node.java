package org.neo4j.cypher.internal.runtime;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.values.Value;
import org.neo4j.values.Values;

public class Node
{
    public final long id;
    public final int[] labels;
    public final Map<Integer,Value> properties;

    public Node( long id, int... labels )
    {
        this.id = id;
        this.labels = labels;
        this.properties = new HashMap<>();
    }

    public Node prop( int key, Object value )
    {
        properties.put( key, Values.of( value ) );
        return this;
    }
}
