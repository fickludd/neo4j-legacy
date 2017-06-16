package org.neo4j.cypher.internal.runtime.columns;

public class FlagColumn
{
    final boolean[] values;

    public FlagColumn( boolean[] values )
    {
        this.values = values;
    }

    public boolean[] array()
    {
        return new boolean[0];
    }

    public void and( boolean[] other, boolean[] result )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = values[i] && other[i];
        }
    }
}
