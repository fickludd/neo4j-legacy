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
package org.neo4j.cypher.internal.runtime.executable;

import org.neo4j.cypher.internal.runtime.ColumnId;
import org.neo4j.cypher.internal.runtime.Morsel;
import org.neo4j.cypher.internal.runtime.columns.ReferenceColumn;
import org.neo4j.cypher.internal.runtime.columns.ValueColumn;
import org.neo4j.internal.kernel.api.PropertyCursor;
import org.neo4j.internal.kernel.api.Read;

public class Executable1PropertyLoad extends ExecutablePusher
{
    Read read;
    PropertyCursor property;
    int propertyKeyId;
    ColumnId propertyColId;
    ColumnId propertyRefCol;

    public Executable1PropertyLoad( Read read, PropertyCursor property, int propertyKeyId, ColumnId propertyColId,
            ColumnId propertyRefCol )
    {
        this.read = read;
        this.property = property;
        this.propertyKeyId = propertyKeyId;
        this.propertyColId = propertyColId;
        this.propertyRefCol = propertyRefCol;
    }

    @Override
    public void process( Morsel morsel )
    {
        ReferenceColumn propertyRefs = morsel.refCol( propertyRefCol );
        ValueColumn valuesColumn = morsel.valueCol( propertyColId );

        for ( int i = 0; i < morsel.size(); i++ )
        {
            read.nodeProperties( propertyRefs.get( i ), property );
            while ( property.next() && property.propertyKey() != propertyKeyId )
            {
            }
            if ( property.propertyKey() == propertyKeyId )
            {
                valuesColumn.setValue( i, property );
            }
        }

        next.process( morsel );
    }
}
