package org.neo4j.cypher.internal.runtime.kernel;

public interface ReadOps
{
    void properties( long propertyRef, PropertyCursor property );

    void readLabels( long labelRef, LabelCursor labels );
}
