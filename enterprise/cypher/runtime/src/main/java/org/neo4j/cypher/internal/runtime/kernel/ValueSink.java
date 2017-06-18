package org.neo4j.cypher.internal.runtime.kernel;

import org.neo4j.values.Value;

public interface ValueSink
{
    void value( short number );
    void value( long number );
    void value( double number );
    void value( String number );
    void value( Value number );
}
