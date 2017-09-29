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
package org.neo4j.cypher.internal.runtime;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.neo4j.cypher.internal.runtime.columns.FlagColumn;
import org.neo4j.cypher.internal.runtime.columns.GenericValueColumn;
import org.neo4j.cypher.internal.runtime.columns.LabelColumn;
import org.neo4j.cypher.internal.runtime.columns.ReferenceColumn;
import org.neo4j.cypher.internal.runtime.columns.ValueColumn;

public class Morsel extends BufferController
{
    private final FlagColumn inUse;
    private final FlagColumn tempFlag;
    private final Map<ColumnId, ReferenceColumn> refCols;
    private final Map<ColumnId, LabelColumn> labelCols;
    private final Map<ColumnId, ValueColumn> valueCols;

    public Morsel( int maxSize )
    {
        super( maxSize );
        inUse = new FlagColumn( this );
        tempFlag = new FlagColumn( this );
        refCols = new HashMap<>();
        valueCols = new HashMap<>();
        labelCols = new HashMap<>();
    }

    public FlagColumn inUse()
    {
        return inUse;
    }

    public ValueColumn valueCol( ColumnId propertyCol )
    {
        return valueCols.computeIfAbsent( propertyCol,
                x -> new GenericValueColumn( this ) );
    }

    public FlagColumn tempFlagColumn()
    {
        return tempFlag;
    }

    public ReferenceColumn refCol( ColumnId nodeIdCol )
    {
        return refCols.computeIfAbsent( nodeIdCol,
                x -> new ReferenceColumn( this ) );
    }

//    public LabelColumn labelsCol( ColumnId labelIdCol )
//    {
//        return labelCols.computeIfAbsent( labelIdCol,
//                x -> new LabelColumn( this ) );
//    }

    public ValueColumn[] columns( ColumnId[] propertyColIds )
    {
        return Arrays.stream( propertyColIds )
                .map( valueCols::get )
                .toArray( ValueColumn[]::new );
    }

    public void copyColumn( Morsel source )
    {
        for ( ColumnId columnId : source.refCols.keySet() )
        {
            refCols.put( columnId, new ReferenceColumn( this ) );
        }
//        for ( ColumnId columnId : source.labelCols.keySet() )
//        {
//            labelCols.put( columnId, new LabelColumn( this ) );
//        }
        for ( ColumnId columnId : source.valueCols.keySet() )
        {
            valueCols.put( columnId, new GenericValueColumn( this ) );
        }
    }

    public void copyRowFromTo( Morsel source, int sourceIndex, int resultIndex )
    {
        for ( ColumnId col : refCols.keySet() )
        {
            long value = source.refCol( col ).get( sourceIndex );
            refCols.get( col ).setAt( resultIndex, value );
        }
//        for ( ColumnId col : labelCols.keySet() )
//        {
//            long value = source.labelsCol( col ).get( sourceIndex );
//            labelCols.get( col ).setAt( resultIndex, value );
//        }
        for ( ColumnId col : valueCols.keySet() )
        {
            source.valueCol( col ).getValueAt( sourceIndex, resultIndex, valueCols.get( col ) );
        }
    }
}
