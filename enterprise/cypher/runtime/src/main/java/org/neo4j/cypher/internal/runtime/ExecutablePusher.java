package org.neo4j.cypher.internal.runtime;

abstract class ExecutablePusher
{
    ExecutablePusher next;

    abstract void process( Morsel input );
}
