package org.neo4j.cypher.internal.runtime.kernel;

public interface NodeCursor
{
    boolean next();

    long getId();

    long labels();

    long edgeGroupReference();

    long propertyReference();
}
