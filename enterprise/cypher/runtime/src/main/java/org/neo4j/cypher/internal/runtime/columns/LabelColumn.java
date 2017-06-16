package org.neo4j.cypher.internal.runtime.columns;

import org.neo4j.cypher.internal.runtime.LabelCursor;
import org.neo4j.cypher.internal.runtime.ReadOps;

public class LabelColumn
{
    ReadOps readOps;
    final long[] references;

    public LabelColumn( long[] references )
    {
        this.references = references;
    }

    public void setAt( int offset, long reference )
    {
        references[offset] = reference;
    }

    public void hasLabel( int labelId, boolean[] result, LabelCursor labels )
    {
        for ( int i = 0; i < references.length; i++ )
        {
            long field = references[i];
            result[i] = isInline( field ) ?
                        inlineLabelsContain( field, labelId ) :
                        labelRefContainsLabel( labelId, field, labels );
        }
    }

    private boolean labelRefContainsLabel( int labelId, long reference, LabelCursor labels )
    {
        readOps.readLabels( reference, labels );
        while ( labels.next() )
        {
            if ( labels.get() == labelId )
            {
                return true;
            }
        }
        return false;
    }

    private static boolean isInline( long field )
    {
        return true;
    }

    private static boolean inlineLabelsContain( long field, int labelId )
    {
        return field == labelId;
    }
}
