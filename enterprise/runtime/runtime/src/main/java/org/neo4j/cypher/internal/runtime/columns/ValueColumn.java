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

import org.neo4j.values.AnyValue;

public abstract class ValueColumn extends OffsettingValueWriter
{
    // column-column comparison

    public abstract void eq( ValueColumn other, boolean[] result );
    public abstract void eq( short[] other, boolean[] result );
    public abstract void eq( long[] other, boolean[] result );
    public abstract void eq( double[] other, boolean[] result );
    public abstract void eq( String[] other, boolean[] result );
    public abstract void eq( AnyValue[] other, boolean[] result );

    public abstract void lt( ValueColumn other, boolean[] result );
    public abstract void lt( short[] other, boolean[] result );
    public abstract void lt( long[] other, boolean[] result );
    public abstract void lt( double[] other, boolean[] result );
    public abstract void lt( AnyValue[] other, boolean[] result );

    public abstract void gte( ValueColumn other, boolean[] result );
    public abstract void gte( short[] other, boolean[] result );
    public abstract void gte( long[] other, boolean[] result );
    public abstract void gte( double[] other, boolean[] result );
    public abstract void gte( AnyValue[] other, boolean[] result );

    // column-scalar comparison

    public abstract void eq( long number, boolean[] result );
    public abstract void eq( double number, boolean[] result );
    public abstract void eq( String string, boolean[] result );
    public abstract void eq( AnyValue value, boolean[] result );

    public abstract void lt( long number, boolean[] result );
    public abstract void lt( double number, boolean[] result );

    public abstract void gte( long number, boolean[] result );
    public abstract void gte( double number, boolean[] result );

    public abstract <EXCEPTION extends Exception> void getValueAt(
            int sourceOffset,
            int sinkOffset,
            OffsettingValueWriter sink ) throws EXCEPTION;
}
