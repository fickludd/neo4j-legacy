package org.neo4j.cypher.internal.runtime.columns;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.cypher.internal.runtime.BufferController;
import org.neo4j.values.NumberValues;
import org.neo4j.values.Value;
import org.neo4j.values.Values;

public class ShortColumn extends ValueColumn
{
    final BufferController bufferController;
    final short[] values;
    final Map<Integer, Value> backup;

    public ShortColumn( BufferController bufferController )
    {
        this.bufferController = bufferController;
        this.values = new short[bufferController.maxSize()];
        backup = new HashMap<>();
    }

    @Override
    public void setAt( int offset, short value )
    {
        values[offset] = value;
    }

    @Override
    public void setAt( int offset, long value )
    {
        short asShort = (short) value;
        if ( asShort == value )
        {
            values[offset] = asShort;
        }
        else
        {
            backup.put( offset, Values.longValue( value ) );
        }
    }

    @Override
    public void setAt( int offset, double value )
    {
        short asShort = (short) value;
        if ( asShort == value )
        {
            values[offset] = asShort;
        }
        else
        {
            backup.put( offset, Values.doubleValue( value ) );
        }
    }

    @Override
    public void setAt( int offset, String value )
    {
        backup.put( offset, Values.stringValue( value ) );
    }

    @Override
    public void setAt( int offset, Value value )
    {
        backup.put( offset, value );
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
            result[i] = values[i] < other[i];
        }
    }

    @Override
    public void lt( long[] other, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = values[i] < other[i];
        }
    }

    @Override
    public void lt( double[] other, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = values[i] < other[i];
        }
    }

    @Override
    public void lt( Value[] other, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = NumberValues.gte( other[i], values[i] );
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
            result[i] = values[i] < other[i];
        }
    }

    @Override
    public void gte( long[] other, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = values[i] < other[i];
        }
    }

    @Override
    public void gte( double[] other, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = values[i] < other[i];
        }
    }

    @Override
    public void gte( Value[] other, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = NumberValues.lt( other[i], values[i] );
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
            result[i] = values[i] == other[i];
        }
    }

    @Override
    public void eq( long[] other, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = values[i] == other[i];
        }
    }

    @Override
    public void eq( double[] other, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = values[i] == other[i];
        }
    }

    @Override
    public void eq( String[] other, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = false;
        }
    }

    @Override
    public void eq( Value[] other, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = NumberValues.eq( other[i], values[i] );
        }
    }

    @Override
    public void eq( long number, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = values[i] == number;
        }
    }

    @Override
    public void eq( double number, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = values[i] == number;
        }
    }

    @Override
    public void eq( String string, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = false;
        }
    }

    @Override
    public void eq( Value value, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = NumberValues.eq( value, values[i] );
        }
    }

    @Override
    public void lt( long number, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = values[i] < number;
        }
    }

    @Override
    public void lt( double number, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = values[i] < number;
        }
    }

    @Override
    public void gte( long number, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = values[i] >= number;
        }
    }

    @Override
    public void gte( double number, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = values[i] >= number;
        }
    }
}
