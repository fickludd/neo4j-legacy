package org.neo4j.cypher.internal.runtime;

import org.neo4j.values.Value;

public abstract class ValueSink
{
    public abstract void value( short number );
    public abstract void value( long number );
    public abstract void value( double number );
    public abstract void value( String number );
    public abstract void value( Value number );
}
