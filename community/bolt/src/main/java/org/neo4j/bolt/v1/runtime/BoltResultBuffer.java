package org.neo4j.bolt.v1.runtime;

import java.io.IOException;

import org.neo4j.bolt.v1.messaging.Neo4jPack;
import org.neo4j.bolt.v1.packstream.PackOutput;
import org.neo4j.kernel.impl.logging.LogService;
import org.neo4j.kernel.impl.query.ResultBuffer;
import org.neo4j.kernel.impl.query.ResultBufferException;
import org.neo4j.logging.Log;
import org.neo4j.values.AnyValue;

import static org.neo4j.bolt.v1.messaging.BoltResponseMessage.RECORD;

public class BoltResultBuffer implements ResultBuffer
{
    private int stageSize;
    private AnyValue[] stage;
    private boolean freeStage;
    private long resultCount;

    private final PackOutput output;
    private final Neo4jPack.Packer packer;
    private final Log log;

    public BoltResultBuffer( Neo4jPack neo4jPack, PackOutput output, LogService logService )
    {
        this.output = output;
        this.packer = neo4jPack.newPacker( output );
        this.log = logService.getInternalLog( getClass() );

        freeStage = true;
    }

    @Override
    public int valuesPerResult()
    {
        return stageSize;
    }

    @Override
    public void setValuesPerResult( int size )
    {
        stageSize = size;
        if ( stage.length < size )
        {
            stage = new AnyValue[size];
        }
    }

    @Override
    public long prepareResultStage() throws ResultBufferException
    {
        if (!freeStage && !commitResultStage())
        {
            return -1;
        }
        freeStage = false;
        return resultCount++;
    }

    @Override
    public void writeValueToStage( int columnId, AnyValue value )
    {
        stage[columnId] = value;
    }

    @Override
    public boolean commitResultStage() throws ResultBufferException
    {
        boolean packingFailed = true;
        output.beginMessage();
        try
        {
            packer.packStructHeader( 1, RECORD.signature() );
            packer.packListHeader( stage.length );
            for ( AnyValue value : stage )
            {
                packer.pack( value );
            }
            packingFailed = false;
            output.messageSucceeded();
            freeStage = true;
            return true;
        }
        catch ( Throwable error )
        {
            // TODO: proper back-pressure?
            // Is there any possibility that we could recover, eg. if the buffer is full we could
            // just return false here, and try to write again on the next prepareResultStage.
            cleanFailedMessage( packingFailed, error );
            throw new ResultBufferException( error );
        }
    }

    private void cleanFailedMessage( boolean packingFailed, Throwable error ) throws ResultBufferException
    {
        if ( packingFailed )
        {
            // packing failed, there might be some half-written data in the output buffer right now
            // notify output about the failure so that it cleans up the buffer
            try
            {
                output.messageFailed();
            }
            catch ( IOException e )
            {
                throw new ResultBufferException( e );
            }
            log.error( "Failed to write full %s message because: %s", RECORD, error.getMessage() );
        }
    }
}
