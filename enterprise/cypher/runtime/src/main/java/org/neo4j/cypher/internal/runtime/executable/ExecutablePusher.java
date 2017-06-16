package org.neo4j.cypher.internal.runtime.executable;

import org.neo4j.cypher.internal.runtime.Morsel;

abstract class ExecutablePusher
{
    ExecutablePusher next;

    abstract void process( Morsel input );
}
