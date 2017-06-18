package org.neo4j.cypher.internal.runtime.kernel;

public interface LabelCursor
{
    boolean next();

    int get();
}
