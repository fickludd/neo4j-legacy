package org.neo4j.cypher.internal.runtime.columns;

import org.neo4j.cypher.internal.runtime.BufferController;

public class FlagColumn
{
    final boolean[] values;
    final BufferController bufferController;

    public FlagColumn( BufferController bufferController )
    {
        this.values = new boolean[bufferController.maxSize()];
        this.bufferController = bufferController;
    }

    public boolean[] array()
    {
        return values;
    }

    public void and( boolean[] other, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = values[i] && other[i];
        }
    }
}
