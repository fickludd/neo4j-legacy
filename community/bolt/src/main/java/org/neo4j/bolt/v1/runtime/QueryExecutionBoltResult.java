package org.neo4j.bolt.v1.runtime;

import org.neo4j.bolt.v1.runtime.spi.BoltResult;
import org.neo4j.kernel.impl.query.QueryExecution;

public class QueryExecutionBoltResult extends BoltResult
{
    private final QueryExecution queryExecution;

    public QueryExecutionBoltResult( QueryExecution queryExecution )
    {
        this.queryExecution = queryExecution;
    }

    @Override
    public String[] fieldNames()
    {
        return queryExecution.header();
    }

    @Override
    public void accept( Visitor visitor ) throws Exception
    {
        while (queryExecution.waitForResult())
        {
        }
    }

    @Override
    public void close()
    {

    }
}
