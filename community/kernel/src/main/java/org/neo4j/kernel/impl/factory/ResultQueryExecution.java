package org.neo4j.kernel.impl.factory;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.neo4j.graphdb.ExecutionPlanDescription;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Notification;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.QueryExecutionType;
import org.neo4j.graphdb.QueryStatistics;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Result;
import org.neo4j.kernel.impl.query.QueryExecution;
import org.neo4j.kernel.impl.query.ResultBuffer;
import org.neo4j.values.AnyValue;
import org.neo4j.values.ValueMapper;

public class ResultQueryExecution implements Result
{
    private final QueryExecution queryExecution;
    private final ValueMapper valueMapper;
    private final Buffer resultBuffer;

    private final Row resultRow = new Row();

    public ResultQueryExecution( QueryExecution queryExecution, ValueMapper valueMapper )
    {
        this.queryExecution = queryExecution;
        this.resultBuffer = (Buffer)queryExecution.resultBuffer();
        this.valueMapper = valueMapper;
    }

    @Override
    public QueryExecutionType getQueryExecutionType()
    {
        throw new UnsupportedOperationException( "later" );
    }

    @Override
    public List<String> columns()
    {
        return Arrays.asList(queryExecution.header());
    }

    @Override
    public <T> ResourceIterator<T> columnAs( String name )
    {
        return null;
    }

    @Override
    public boolean hasNext()
    {
        return resultBuffer.hasData() || queryExecution.waitForResult();
    }

    @Override
    public Map<String,Object> next()
    {
        if ( !hasNext() )
        {
            throw new NoSuchElementException();
        }

        Map<String,Object> row = new HashMap<>(resultBuffer.values.length);
        for ( int i = 0; i < queryExecution.header().length; i++ )
        {
            row.put( queryExecution.header()[i], resultBuffer.values[i].map( valueMapper ) );
        }

        resultBuffer.consumeData();
        return row;
    }

    @Override
    public void close()
    {
        queryExecution.terminate();
    }

    @Override
    public QueryStatistics getQueryStatistics()
    {
        throw new UnsupportedOperationException( "later" );
    }

    @Override
    public ExecutionPlanDescription getExecutionPlanDescription()
    {
        throw new UnsupportedOperationException( "later" );
    }

    @Override
    public String resultAsString()
    {
        throw new UnsupportedOperationException( "later" );
    }

    @Override
    public void writeAsStringTo( PrintWriter writer )
    {
        throw new UnsupportedOperationException( "later" );
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException(); // this is actually not supported
    }

    @Override
    public Iterable<Notification> getNotifications()
    {
        throw new UnsupportedOperationException( "later" );
    }

    @Override
    public <VisitationException extends Exception> void accept( ResultVisitor<VisitationException> visitor )
            throws VisitationException
    {
        while ( hasNext() ) {
            visitor.visit( resultRow );

            resultBuffer.consumeData();
        }
    }

    private class Row implements ResultRow
    {
        @Override
        public Node getNode( String key )
        {
            return (Node)get( key );
        }

        @Override
        public Relationship getRelationship( String key )
        {
            return (Relationship)get( key );
        }

        @Override
        public Object get( String key )
        {
            // Betting on low number of columns (e.g. < 20)
            // TODO: branch into map representation or binary search on bigger ones.
            String[] columns = queryExecution.header();
            for ( int i = 0; i < columns.length; i++ )
            {
                if ( columns[i].equals( key ) )
                {
                    return resultBuffer.values[i].map( valueMapper );
                }
            }
            return null;
        }

        @Override
        public String getString( String key )
        {
            return (String)get( key );
        }

        @Override
        public Number getNumber( String key )
        {
            return (Number)get( key );
        }

        @Override
        public Boolean getBoolean( String key )
        {
            return (Boolean)get( key );
        }

        @Override
        public Path getPath( String key )
        {
            return (Path)get( key );
        }
    }

    static class Buffer implements ResultBuffer {

        private boolean hasData;
        AnyValue[] values;

        @Override
        public void setValuesPerResult( int size )
        {
            this.hasData = false;
            this.values = new AnyValue[size];
        }

        @Override
        public long prepareResultStage()
        {
            assert !hasData;
            return 0;
        }

        @Override
        public void writeValueToStage( int columnId, AnyValue value )
        {
            values[columnId] = value;
        }

        @Override
        public boolean commitResultStage()
        {
            hasData = true;
            return false;
        }

        public boolean hasData()
        {
            return hasData;
        }

        void consumeData()
        {
            hasData = false;
        }
    }
}
