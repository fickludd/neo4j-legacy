package org.neo4j.cypher.internal.runtime.columns;

import org.neo4j.cypher.internal.runtime.PropertyCursor;
import org.neo4j.cypher.internal.runtime.ValueSink;
import org.neo4j.values.Value;

public abstract class ValueColumn extends ValueSink
{
    public abstract void setAt( int offset, short value );
    public abstract void setAt( int offset, long value );
    public abstract void setAt( int offset, double value );
    public abstract void setAt( int offset, String value );
    public abstract void setAt( int offset, Value value );

    // column-column comparison

    public abstract void eq( ValueColumn other, boolean[] result );
    public abstract void eq( short[] other, boolean[] result );
    public abstract void eq( long[] other, boolean[] result );
    public abstract void eq( double[] other, boolean[] result );
    public abstract void eq( String[] other, boolean[] result );
    public abstract void eq( Value[] other, boolean[] result );

    public abstract void lt( ValueColumn other, boolean[] result );
    public abstract void lt( short[] other, boolean[] result );
    public abstract void lt( long[] other, boolean[] result );
    public abstract void lt( double[] other, boolean[] result );
    public abstract void lt( Value[] other, boolean[] result );

    public abstract void gte( ValueColumn other, boolean[] result );
    public abstract void gte( short[] other, boolean[] result );
    public abstract void gte( long[] other, boolean[] result );
    public abstract void gte( double[] other, boolean[] result );
    public abstract void gte( Value[] other, boolean[] result );

    // column-scalar comparison

    public abstract void eq( long number, boolean[] result );
    public abstract void eq( double number, boolean[] result );
    public abstract void eq( String string, boolean[] result );
    public abstract void eq( Value value, boolean[] result );

    public abstract void lt( long number, boolean[] result );
    public abstract void lt( double number, boolean[] result );

    public abstract void gte( long number, boolean[] result );
    public abstract void gte( double number, boolean[] result );

    // setting value from PropertyCursor

    private int currOffset;
    public final void setValue( int offset, PropertyCursor propertyCursor )
    {
        currOffset = offset;
        propertyCursor.valueTo( this );
        currOffset = -1;
    }

    @Override
    public final void value( short number )
    {
        setAt( currOffset, number );
    }

    @Override
    public final void value( long number )
    {
        setAt( currOffset, number );
    }

    @Override
    public final void value( double number )
    {
        setAt( currOffset, number );
    }

    @Override
    public final void value( String number )
    {
        setAt( currOffset, number );
    }

    @Override
    public final void value( Value number )
    {
        setAt( currOffset, number );
    }
}
