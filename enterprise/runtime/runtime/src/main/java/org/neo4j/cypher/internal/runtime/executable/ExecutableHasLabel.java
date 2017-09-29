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
import org.neo4j.cypher.internal.runtime.columns.FlagColumn;
import org.neo4j.cypher.internal.runtime.columns.LabelColumn;
import org.neo4j.internal.kernel.api.Read;

/**
 * This executable was built assuming that we would change to a label cursor.
 */
public class ExecutableHasLabel extends ExecutablePusher
{
//    int labelId;
//    ColumnId labelColId;
//    LabelCursor labels;
//    final Read read;
//
//    public ExecutableHasLabel( int labelId, ColumnId labelColId, LabelCursor labels, Read read )
//    {
//        this.labelId = labelId;
//        this.labelColId = labelColId;
//        this.labels = labels;
//        this.read = read;
//    }

    @Override
    public void process( Morsel morsel )
    {
//        LabelColumn labelCol = morsel.labelsCol( labelColId );
//        FlagColumn result = morsel.tempFlagColumn();
//        FlagColumn inUse = morsel.inUse();
//
//        labelCol.hasLabel( labelId, result.array(), labels, read );
//        inUse.and( result.array(), inUse.array() );

        next.process( morsel );
    }
}
