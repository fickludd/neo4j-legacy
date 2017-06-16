package org.neo4j.cypher.internal.runtime;

import java.util.Arrays;

import org.neo4j.cypher.internal.runtime.columns.ReferenceColumn;
import org.neo4j.cypher.internal.runtime.columns.ValueColumn;

public class ExecutableNPropertyLoad extends ExecutablePusher
{
    ReadOps readOps;
    PropertyCursor property;
    int[] propertyKeyIds; // must be sorted
    ColumnId[] propertyColIds;
    ColumnId propertyRefCol;

    @Override
    void process( Morsel morsel )
    {
        ReferenceColumn propertyRefs = morsel.refCol( propertyRefCol );
        ValueColumn[] valueColumns = morsel.columns( propertyColIds );

        for ( int i = 0; i < morsel.size(); i++ )
        {
            readOps.properties( propertyRefs.get( i ), property );
            while ( property.next() )
            {
                int colIndex = Arrays.binarySearch( propertyKeyIds, property.keyId() );
                if ( colIndex >= 0 )
                {
                    valueColumns[colIndex].setValue( i, property );
                }
            }
        }

        next.process( morsel );
    }
}
