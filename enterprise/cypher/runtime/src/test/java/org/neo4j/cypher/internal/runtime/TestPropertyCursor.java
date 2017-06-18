package org.neo4j.cypher.internal.runtime;

import java.util.Map;

import org.neo4j.cypher.internal.runtime.kernel.PropertyCursor;
import org.neo4j.cypher.internal.runtime.kernel.ValueSink;
import org.neo4j.values.Value;

public class TestPropertyCursor implements PropertyCursor
{
    int offset = -1;
    Integer[] keys;
    Map<Integer,Value> properties;

    public void setProperties( Map<Integer,Value> properties )
    {
        this.properties = properties;
        keys = properties.keySet().toArray( new Integer[0] );
        offset = -1;
    }

    @Override
    public boolean next()
    {
        offset++;
        return offset < keys.length;
    }

    @Override
    public int keyId()
    {
        return keys[offset];
    }

    @Override
    public void valueTo( ValueSink sink )
    {
        Value v = properties.get( keys[offset] );
        Object o = v.asObject();
        if ( o instanceof Short )
        {
            sink.value( (Short)o );
        }
        else if ( o instanceof Long )
        {
            sink.value( (Long)o );
        }
        else if ( o instanceof Double )
        {
            sink.value( (Double)o );
        }
        else if ( o instanceof String )
        {
            sink.value( (String)o );
        }
        else
        {
            sink.value( v );
        }
    }
}
