package org.neo4j.cypher.internal.runtime.executable;

import java.util.Arrays;

import org.neo4j.cypher.internal.runtime.ColumnId;
import org.neo4j.cypher.internal.runtime.Morsel;
import org.neo4j.cypher.internal.runtime.kernel.PropertyCursor;
import org.neo4j.cypher.internal.runtime.kernel.ReadOps;
import org.neo4j.cypher.internal.runtime.columns.ReferenceColumn;
import org.neo4j.cypher.internal.runtime.columns.ValueColumn;

public class ExecutableNPropertyLoad extends ExecutablePusher
{
    ReadOps readOps;
    PropertyCursor property;
    int[] propertyKeyIds; // must be sorted
    ColumnId[] propertyColIds;
    ColumnId propertyRefCol;

    public ExecutableNPropertyLoad( ReadOps readOps, PropertyCursor property, int[] propertyKeyIds,
            ColumnId[] propertyColIds, ColumnId propertyRefCol )
    {
        this.readOps = readOps;
        this.property = property;
        this.propertyKeyIds = propertyKeyIds;
        this.propertyColIds = propertyColIds;
        this.propertyRefCol = propertyRefCol;
    }

    @Override
    public void process( Morsel morsel )
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
