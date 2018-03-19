package org.neo4j.cypher.internal.runtime;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.neo4j.cypher.internal.runtime.executable.ExecutablePusher;

@SuppressWarnings( "ALL" )
public class Pipeline
{
    final ExecutablePusher[] operators;
    final Queue<Morsel> resultBuffer;
    final List<Dependency> dependencies;

    public Pipeline( ExecutablePusher... operators )
    {
        this.operators = operators;
        for ( int i = 0; i < operators.length - 1; i++ )
        {
            operators[i].next = operators[i+1];
        }
        resultBuffer = new LinkedList<>();
        dependencies = new ArrayList<>();
    }

    public void dependOnce( Pipeline p )
    {
        dependencies.add( new ONCE(p) );
    }

    public void dependAll( Pipeline p )
    {
        dependencies.add( new ALL(p) );
    }

    public boolean isDone()
    {
        throw new UnsupportedOperationException( "not implemented" );
    }

    public boolean isStreaming()
    {
        throw new UnsupportedOperationException( "not implemented" );
    }

    public PieceOfWork nextPieceOfWork()
    {
        boolean canCompute = true;
        for ( Dependency d : dependencies )
        {
            if ( !d.isFullfilled() )
            {
                PieceOfWork next = d.nextPieceOfWork();
                if ( next != null )
                {
                    return next;
                }
                else
                {
                    canCompute = false;
                }
            }
        }
        if ( canCompute )
        {

        }
    }

    interface Dependency {
        public boolean isFullfilled();
        public PieceOfWork nextPieceOfWork();
    }

    public static class ONCE implements Dependency {
        final Pipeline p;
        public ONCE( Pipeline p )
        {
            this.p = p;
        }

        @Override
        public boolean isFullfilled()
        {
            return !p.resultBuffer.isEmpty();
        }

        @Override
        public PieceOfWork nextPieceOfWork()
        {
            return p.nextPieceOfWork();
        }
    }

    public static class ALL implements Dependency {
        final Pipeline p;
        public ALL( Pipeline p )
        {
            this.p = p;
        }

        @Override
        public boolean isFullfilled()
        {
            return p.isDone();
        }

        @Override
        public PieceOfWork nextPieceOfWork()
        {
            return p.nextPieceOfWork();
        }
    }
}
