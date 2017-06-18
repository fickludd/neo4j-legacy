package org.neo4j.cypher.internal.runtime.executable;

import org.neo4j.cypher.internal.runtime.ColumnId;
import org.neo4j.cypher.internal.runtime.Morsel;
import org.neo4j.cypher.internal.runtime.kernel.PropertyCursor;
import org.neo4j.cypher.internal.runtime.kernel.ReadOps;
import org.neo4j.cypher.internal.runtime.columns.ReferenceColumn;
import org.neo4j.cypher.internal.runtime.columns.ValueColumn;

public class Executable1PropertyLoad extends ExecutablePusher
{
    ReadOps readOps;
    PropertyCursor property;
    int propertyKeyId;
    ColumnId propertyColId;
    ColumnId propertyRefCol;

    public Executable1PropertyLoad( ReadOps readOps, PropertyCursor property, int propertyKeyId, ColumnId propertyColId,
            ColumnId propertyRefCol )
    {
        this.readOps = readOps;
        this.property = property;
        this.propertyKeyId = propertyKeyId;
        this.propertyColId = propertyColId;
        this.propertyRefCol = propertyRefCol;
    }

    @Override
    public void process( Morsel morsel )
    {
        ReferenceColumn propertyRefs = morsel.refCol( propertyRefCol );
        ValueColumn valuesColumn = morsel.column( propertyColId );

        for ( int i = 0; i < morsel.size(); i++ )
        {
            readOps.properties( propertyRefs.get( i ), property );
            while ( property.next() )
            {
                valuesColumn.setValue( i,
                        property.keyId() != propertyKeyId ? PropertyCursor.NOOP : property );
            }
        }

        next.process( morsel );
    }
}
