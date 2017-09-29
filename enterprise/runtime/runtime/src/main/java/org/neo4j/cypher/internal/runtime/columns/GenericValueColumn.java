/*
 * Copyright (c) 2002-2017 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.cypher.internal.runtime.columns;

import org.neo4j.cypher.internal.runtime.BufferController;
import org.neo4j.values.AnyValue;
import org.neo4j.values.AnyValues;
import org.neo4j.values.storable.TextArray;
import org.neo4j.values.storable.TextValue;
import org.neo4j.values.storable.Value;
import org.neo4j.values.storable.Values;
import org.neo4j.values.virtual.MapValue;

public class GenericValueColumn extends ValueColumn
{
    final AnyValue[] values;
    final BufferController bufferController;

    public GenericValueColumn( BufferController bufferController )
    {
        this.values = new AnyValue[bufferController.maxSize()];
        this.bufferController = bufferController;
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
    public void setAt( int offset, AnyValue value )
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
            result[i] = AnyValues.lt( values[i], other[i] );
        }
    }

    @Override
    public void lt( long[] other, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = AnyValues.lt( values[i], other[i] );
        }
    }

    @Override
    public void lt( double[] other, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = AnyValues.lt( values[i], other[i] );
        }
    }

    @Override
    public void lt( AnyValue[] other, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = AnyValues.COMPARATOR.compare( values[i], other[i] ) < 0;
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
            result[i] = AnyValues.gte( values[i], other[i] );
        }
    }

    @Override
    public void gte( long[] other, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = AnyValues.gte( values[i], other[i] );
        }
    }

    @Override
    public void gte( double[] other, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = AnyValues.gte( values[i], other[i] );
        }
    }

    @Override
    public void gte( AnyValue[] other, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = AnyValues.COMPARATOR.compare( values[i], other[i] ) >= 0;
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
            result[i] = AnyValues.eq( values[i], other[i] );
        }
    }

    @Override
    public void eq( long[] other, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = AnyValues.eq( values[i], other[i] );
        }
    }

    @Override
    public void eq( double[] other, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = AnyValues.eq( values[i], other[i] );
        }
    }

    @Override
    public void eq( String[] other, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            AnyValue value = values[i];
            result[i] = (value instanceof TextValue) && ((TextValue)value).equals( other[i] );
        }
    }

    @Override
    public void eq( AnyValue[] other, boolean[] result )
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
            AnyValue value = values[i];
            result[i] = (value instanceof TextValue) && ((TextValue)value).equals( string );
        }
    }

    @Override
    public void eq( AnyValue value, boolean[] result )
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
            result[i] = AnyValues.lt( values[i], number );
        }
    }

    @Override
    public void lt( double number, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = AnyValues.lt( values[i], number );
        }
    }

    @Override
    public void gte( long number, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = AnyValues.gte( values[i], number );
        }
    }

    @Override
    public void gte( double number, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = AnyValues.gte( values[i], number );
        }
    }

    @Override
    public <EXCEPTION extends Exception> void getValueAt( int sourceOffset, int sinkOffset, OffsettingValueWriter sink )
            throws EXCEPTION
    {
        sink.setAt( sinkOffset, values[sourceOffset] );
    }
}
