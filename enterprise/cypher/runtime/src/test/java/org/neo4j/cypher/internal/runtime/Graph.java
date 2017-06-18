package org.neo4j.cypher.internal.runtime;

import org.neo4j.cypher.internal.runtime.kernel.LabelCursor;
import org.neo4j.cypher.internal.runtime.kernel.PropertyCursor;
import org.neo4j.cypher.internal.runtime.kernel.ReadOps;

public class Graph implements ReadOps
{
    final Node[] nodes;

    public Graph( Node... nodes )
    {
        this.nodes = nodes;
    }

    @Override
    public void properties( long propertyRef, PropertyCursor property )
    {
        ((TestPropertyCursor)property).setProperties( nodes[(int)propertyRef].properties );
    }

    @Override
    public void readLabels( long labelRef, LabelCursor labels )
    {
        ((TestLabelCursor)labels).setLabels( nodes[(int)labelRef].labels );
    }
}
