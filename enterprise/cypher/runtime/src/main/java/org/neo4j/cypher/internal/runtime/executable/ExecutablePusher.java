package org.neo4j.cypher.internal.runtime.executable;

import org.neo4j.cypher.internal.runtime.Morsel;

public abstract class ExecutablePusher
{
    public ExecutablePusher next;

    public abstract void process( Morsel input );
}
