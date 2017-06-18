package org.neo4j.cypher.internal.runtime.columns;

import org.neo4j.values.NumberValues;
import org.neo4j.values.Value;
import org.neo4j.values.Values;

public class GenericValueColumn extends ValueColumn
{
    final Value[] values;

    public GenericValueColumn( Value[] values )
    {
        this.values = values;
    }

    @Override
    public void setAt( int offset, short value )
    {
        values[offset] = Values.shortValue( value );
    }

    @Override
    public void setAt( int offset, long value )
    {
        values[offset] = Values.longValue( value );
    }

    @Override
    public void setAt( int offset, double value )
    {
        values[offset] = Values.doubleValue( value );
    }

    @Override
    public void setAt( int offset, String value )
    {
        values[offset] = Values.stringValue( value );
    }

    @Override
    public void setAt( int offset, Value value )
    {
        values[offset] = value;
    }

    @Override
    public void lt( ValueColumn other, boolean[] result )
    {
        other.gte( values, result );
    }

    @Override
    public void lt( short[] other, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = NumberValues.lt( values[i], other[i] );
        }
    }

    @Override
    public void lt( long[] other, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = NumberValues.lt( values[i], other[i] );
        }
    }

    @Override
    public void lt( double[] other, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = NumberValues.lt( values[i], other[i] );
        }
    }

    @Override
    public void lt( Value[] other, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = Values.VALUE_COMPARATOR.compare( values[i], other[i] ) < 0;
        }
    }

    @Override
    public void gte( ValueColumn other, boolean[] result )
    {
        other.lt( values, result );
    }

    @Override
    public void gte( short[] other, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = NumberValues.gte( values[i], other[i] );
        }
    }

    @Override
    public void gte( long[] other, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = NumberValues.gte( values[i], other[i] );
        }
    }

    @Override
    public void gte( double[] other, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = NumberValues.gte( values[i], other[i] );
        }
    }

    @Override
    public void gte( Value[] other, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = Values.VALUE_COMPARATOR.compare( values[i], other[i] ) >= 0;
        }
    }

    @Override
    public void eq( ValueColumn other, boolean[] result )
    {
        other.eq( values, result );
    }

    @Override
    public void eq( short[] other, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = NumberValues.eq( values[i], other[i] );
        }
    }

    @Override
    public void eq( long[] other, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = NumberValues.eq( values[i], other[i] );
        }
    }

    @Override
    public void eq( double[] other, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = NumberValues.eq( values[i], other[i] );
        }
    }

    @Override
    public void eq( String[] other, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = values[i].equals( other[i] );
        }
    }

    @Override
    public void eq( Value[] other, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = values[i].equals( other[i] );
        }
    }

    @Override
    public void eq( long number, boolean[] result )
    {
        Value numberValue = Values.longValue( number );
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = values[i].equals( numberValue );
        }
    }

    @Override
    public void eq( double number, boolean[] result )
    {
        Value numberValue = Values.doubleValue( number );
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = values[i].equals( numberValue );
        }
    }

    @Override
    public void eq( String string, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = values[i].equals( string );
        }
    }

    @Override
    public void eq( Value value, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = values[i].equals( value );
        }
    }

    @Override
    public void lt( long number, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = NumberValues.lt( values[i], number );
        }
    }

    @Override
    public void lt( double number, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = NumberValues.lt( values[i], number );
        }
    }

    @Override
    public void gte( long number, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = NumberValues.gte( values[i], number );
        }
    }

    @Override
    public void gte( double number, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = NumberValues.gte( values[i], number );
        }
    }
}
