package org.neo4j.cypher.internal.runtime;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.cypher.internal.runtime.executable.ExecutablePusher;

public class Collector extends ExecutablePusher
{
    List<Morsel> received = new ArrayList<>();

    @Override
    public void process( Morsel input )
    {
        received.add( input );
    }
}
