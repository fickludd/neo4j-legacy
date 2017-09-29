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

import org.neo4j.internal.kernel.api.PropertyCursor;
import org.neo4j.values.AnyValue;
import org.neo4j.values.AnyValueWriter;
import org.neo4j.values.storable.TextArray;
import org.neo4j.values.storable.TextValue;
import org.neo4j.values.virtual.CoordinateReferenceSystem;
import org.neo4j.values.virtual.EdgeValue;
import org.neo4j.values.virtual.MapValue;
import org.neo4j.values.virtual.NodeValue;

public abstract class OffsettingValueWriter implements AnyValueWriter
{
    public abstract void setAt( int offset, short value );
    public abstract void setAt( int offset, long value );
    public abstract void setAt( int offset, double value );
    public abstract void setAt( int offset, String value );
    public abstract void setAt( int offset, AnyValue value );
    // setting value from PropertyCursor

    private int currOffset;
    public final void setValue( int offset, PropertyCursor propertyCursor )
    {
        currOffset = offset;
        propertyCursor.writeTo( this );
        currOffset = -1;
    }

    @Override
    public void writeNull() throws Exception
    {
    }

    @Override
    public void writeBoolean( boolean value ) throws Exception
    {
        throw new UnsupportedOperationException( "not implemented" );
    }

    @Override
    public void writeInteger( byte value ) throws Exception
    {
        throw new UnsupportedOperationException( "not implemented" );
    }

    @Override
    public void writeInteger( short value ) throws Exception
    {
        setAt( currOffset, value );
        currOffset = -1;
    }

    @Override
    public void writeInteger( int value ) throws Exception
    {
        throw new UnsupportedOperationException( "not implemented" );
    }

    @Override
    public void writeInteger( long value ) throws Exception
    {
        setAt( currOffset, value );
    }

    @Override
    public void writeFloatingPoint( float value ) throws Exception
    {
        throw new UnsupportedOperationException( "not implemented" );
    }

    @Override
    public void writeFloatingPoint( double value ) throws Exception
    {
        setAt( currOffset, value );
    }

    @Override
    public void writeString( String value ) throws Exception
    {
        setAt( currOffset, value );
    }

    @Override
    public void writeString( char value ) throws Exception
    {
        throw new UnsupportedOperationException( "not implemented" );
    }

    @Override
    public void writeString( char[] value, int offset, int length ) throws Exception
    {
        throw new UnsupportedOperationException( "not implemented" );
    }

    @Override
    public void beginArray( int size, ArrayType arrayType ) throws Exception
    {
        throw new UnsupportedOperationException( "not implemented" );
    }

    @Override
    public void endArray() throws Exception
    {
        throw new UnsupportedOperationException( "not implemented" );
    }

    @Override
    public void writeByteArray( byte[] value ) throws Exception
    {
        throw new UnsupportedOperationException( "not implemented" );
    }

    @Override
    public void writeNodeReference( long nodeId ) throws Exception
    {
        throw new UnsupportedOperationException( "not implemented" );
    }

    @Override
    public void writeEdgeReference( long edgeId ) throws Exception
    {
        throw new UnsupportedOperationException( "not implemented" );
    }

    @Override
    public void beginMap( int size ) throws Exception
    {
        throw new UnsupportedOperationException( "not implemented" );
    }

    @Override
    public void endMap() throws Exception
    {
        throw new UnsupportedOperationException( "not implemented" );
    }

    @Override
    public void beginList( int size ) throws Exception
    {
        throw new UnsupportedOperationException( "not implemented" );
    }

    @Override
    public void endList() throws Exception
    {
        throw new UnsupportedOperationException( "not implemented" );
    }

    @Override
    public void writePath( NodeValue[] nodes, EdgeValue[] edges ) throws Exception
    {
        throw new UnsupportedOperationException( "not implemented" );
    }

    @Override
    public void beginPoint( CoordinateReferenceSystem coordinateReferenceSystem ) throws Exception
    {
        throw new UnsupportedOperationException( "not implemented" );
    }

    @Override
    public void endPoint() throws Exception
    {
        throw new UnsupportedOperationException( "not implemented" );
    }

    @Override
    public void writeNode( long nodeId, TextArray labels, MapValue properties ) throws Exception
    {
        throw new UnsupportedOperationException( "not implemented" );
    }

    @Override
    public void writeEdge( long edgeId, long startNodeId, long endNodeId, TextValue type, MapValue properties )
            throws Exception
    {
        throw new UnsupportedOperationException( "not implemented" );
    }
}
