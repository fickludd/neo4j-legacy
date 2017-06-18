package org.neo4j.cypher.internal.runtime;

import org.neo4j.cypher.internal.runtime.kernel.LabelCursor;

public class TestLabelCursor implements LabelCursor
{
    int offset = -1;
    int[] labels;

    public void setLabels( int[] labels )
    {
        this.labels = labels;
        offset = -1;
    }

    @Override
    public boolean next()
    {
        offset++;
        return offset < labels.length;
    }

    @Override
    public int get()
    {
        return labels[offset];
    }
}
