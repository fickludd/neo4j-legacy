/*
 * Copyright (c) 2002-2017 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.kernel.impl.api.state;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import org.neo4j.collection.primitive.PrimitiveIntSet;
import org.neo4j.collection.primitive.PrimitiveLongIterator;
import org.neo4j.cursor.Cursor;
import org.neo4j.kernel.api.exceptions.schema.ConstraintValidationException;
import org.neo4j.kernel.api.exceptions.schema.CreateConstraintFailureException;
import org.neo4j.kernel.api.properties.DefinedProperty;
import org.neo4j.kernel.api.properties.Property;
import org.neo4j.kernel.api.schema_new.LabelSchemaDescriptor;
import org.neo4j.kernel.api.schema_new.OrderedPropertyValues;
import org.neo4j.kernel.api.schema_new.SchemaDescriptor;
import org.neo4j.kernel.api.schema_new.constaints.ConstraintDescriptor;
import org.neo4j.kernel.api.schema_new.constaints.IndexBackedConstraintDescriptor;
import org.neo4j.kernel.api.schema_new.index.NewIndexDescriptor;
import org.neo4j.kernel.api.txstate.TransactionState;
import org.neo4j.kernel.impl.api.KernelStatement;
import org.neo4j.kernel.impl.api.RelationshipVisitor;
import org.neo4j.kernel.impl.api.store.RelationshipIterator;
import org.neo4j.storageengine.api.Direction;
import org.neo4j.storageengine.api.NodeItem;
import org.neo4j.storageengine.api.PropertyItem;
import org.neo4j.storageengine.api.RelationshipItem;
import org.neo4j.storageengine.api.StorageProperty;
import org.neo4j.storageengine.api.txstate.NodeState;
import org.neo4j.storageengine.api.txstate.PropertyContainerState;
import org.neo4j.storageengine.api.txstate.ReadableDiffSets;
import org.neo4j.storageengine.api.txstate.ReadableRelationshipDiffSets;
import org.neo4j.storageengine.api.txstate.RelationshipState;
import org.neo4j.storageengine.api.txstate.TxStateVisitor;

public final class LazyTxState implements TransactionState
{
    private final Queue<TxStateChange> changeQueue = new LinkedList<>();
    private final TransactionState delegate;

    public LazyTxState( TransactionState delegate )
    {
        this.delegate = delegate;
    }

    enum ChangeType
    {
        DATA, INDEX
    };

    interface TxStateChange
    {
        void apply();
        ChangeType type();
    }

    private void dataChange( Runnable theChange )
    {
        changeQueue.add( new TxStateChange()
        {
            @Override
            public void apply()
            {
                theChange.run();
            }

            @Override
            public ChangeType type()
            {
                return ChangeType.DATA;
            }
        } );
    }

    private void indexChange( Runnable theChange )
    {
        changeQueue.add( new TxStateChange()
        {
            @Override
            public void apply()
            {
                theChange.run();
            }

            @Override
            public ChangeType type()
            {
                return ChangeType.INDEX;
            }
        } );
    }

    private void applyQueued()
    {
        while ( !changeQueue.isEmpty() )
        {
            changeQueue.poll().apply();
        }
    }

    private void applyQueuedDataChanges()
    {
        while ( !changeQueue.isEmpty() )
        {
            TxStateChange change = changeQueue.poll();
            if ( change.type() == ChangeType.DATA )
            {
                change.apply();
            }
        }
    }

    // READ TX STATE

    @Override
    public void accept( TxStateVisitor visitor ) throws ConstraintValidationException, CreateConstraintFailureException
    {
        applyQueued();
        delegate.accept( visitor );
    }

    @Override
    public void acceptAndDestroy( TxStateVisitor visitor )
            throws ConstraintValidationException, CreateConstraintFailureException
    {
        applyQueuedDataChanges();
        delegate.acceptAndDestroy( visitor );
    }

    @Override
    public boolean hasChanges()
    {
        applyQueued();
        return delegate.hasChanges();
    }

    @Override
    public ReadableDiffSets<Long> nodesWithLabelChanged( int labelId )
    {
        applyQueued();
        return delegate.nodesWithLabelChanged( labelId );
    }

    @Override
    public ReadableDiffSets<Long> addedAndRemovedNodes()
    {
        applyQueued();
        return delegate.addedAndRemovedNodes();
    }

    @Override
    public ReadableRelationshipDiffSets<Long> addedAndRemovedRelationships()
    {
        applyQueued();
        return delegate.addedAndRemovedRelationships();
    }

    @Override
    public Iterable<NodeState> modifiedNodes()
    {
        applyQueued();
        return delegate.modifiedNodes();
    }

    @Override
    public Iterable<RelationshipState> modifiedRelationships()
    {
        applyQueued();
        return delegate.modifiedRelationships();
    }

    @Override
    public boolean relationshipIsAddedInThisTx( long relationshipId )
    {
        applyQueued();
        return delegate.relationshipIsAddedInThisTx( relationshipId );
    }

    @Override
    public boolean relationshipIsDeletedInThisTx( long relationshipId )
    {
        applyQueued();
        return delegate.relationshipIsDeletedInThisTx( relationshipId );
    }

    @Override
    public ReadableDiffSets<Integer> nodeStateLabelDiffSets( long nodeId )
    {
        applyQueued();
        return delegate.nodeStateLabelDiffSets( nodeId );
    }

    @Override
    public Iterator<StorageProperty> augmentGraphProperties( Iterator<StorageProperty> original )
    {
        applyQueued();
        return delegate.augmentGraphProperties( original );
    }

    @Override
    public boolean nodeIsAddedInThisTx( long nodeId )
    {
        applyQueued();
        return delegate.nodeIsAddedInThisTx( nodeId );
    }

    @Override
    public boolean nodeIsDeletedInThisTx( long nodeId )
    {
        applyQueued();
        return delegate.nodeIsDeletedInThisTx( nodeId );
    }

    @Override
    public boolean nodeModifiedInThisTx( long nodeId )
    {
        applyQueued();
        return delegate.nodeModifiedInThisTx( nodeId );
    }

    @Override
    public PrimitiveIntSet nodeRelationshipTypes( long nodeId )
    {
        applyQueued();
        return delegate.nodeRelationshipTypes( nodeId );
    }

    @Override
    public int augmentNodeDegree( long node, int committedDegree, Direction direction )
    {
        applyQueued();
        return delegate.augmentNodeDegree( node, committedDegree, direction );
    }

    @Override
    public int augmentNodeDegree( long node, int committedDegree, Direction direction, int relType )
    {
        applyQueued();
        return delegate.augmentNodeDegree( node, committedDegree, direction, relType );
    }

    @Override
    public PrimitiveLongIterator augmentNodesGetAll( PrimitiveLongIterator committed )
    {
        applyQueued();
        return delegate.augmentNodesGetAll( committed );
    }

    @Override
    public RelationshipIterator augmentRelationshipsGetAll( RelationshipIterator committed )
    {
        applyQueued();
        return delegate.augmentRelationshipsGetAll( committed );
    }

    @Override
    public <EX extends Exception> boolean relationshipVisit( long relId, RelationshipVisitor<EX> visitor ) throws EX
    {
        applyQueued();
        return delegate.relationshipVisit( relId, visitor );
    }

    @Override
    public ReadableDiffSets<Long> indexUpdatesForScan( NewIndexDescriptor index )
    {
        applyQueued();
        return delegate.indexUpdatesForScan( index );
    }

    @Override
    public ReadableDiffSets<Long> indexUpdatesForSeek( NewIndexDescriptor index, OrderedPropertyValues values )
    {
        applyQueued();
        return delegate.indexUpdatesForSeek( index, values );
    }

    @Override
    public ReadableDiffSets<Long> indexUpdatesForRangeSeekByNumber( NewIndexDescriptor index, Number lower,
            boolean includeLower, Number upper, boolean includeUpper )
    {
        applyQueued();
        return delegate.indexUpdatesForRangeSeekByNumber( index, lower, includeLower, upper, includeUpper );
    }

    @Override
    public ReadableDiffSets<Long> indexUpdatesForRangeSeekByString( NewIndexDescriptor index, String lower,
            boolean includeLower, String upper, boolean includeUpper )
    {
        applyQueued();
        return delegate.indexUpdatesForRangeSeekByString( index, lower, includeLower, upper, includeUpper );
    }

    @Override
    public ReadableDiffSets<Long> indexUpdatesForRangeSeekByPrefix( NewIndexDescriptor index, String prefix )
    {
        applyQueued();
        return delegate.indexUpdatesForRangeSeekByPrefix( index, prefix );
    }

    @Override
    public NodeState getNodeState( long id )
    {
        applyQueued();
        return delegate.getNodeState( id );
    }

    @Override
    public RelationshipState getRelationshipState( long id )
    {
        applyQueued();
        return delegate.getRelationshipState( id );
    }

    @Override
    public Cursor<NodeItem> augmentSingleNodeCursor( Cursor<NodeItem> cursor, long nodeId )
    {
        applyQueued();
        return delegate.augmentSingleNodeCursor( cursor, nodeId );
    }

    @Override
    public Cursor<PropertyItem> augmentPropertyCursor( Cursor<PropertyItem> cursor,
            PropertyContainerState propertyContainerState )
    {
        applyQueued();
        return delegate.augmentPropertyCursor( cursor, propertyContainerState );
    }

    @Override
    public Cursor<PropertyItem> augmentSinglePropertyCursor( Cursor<PropertyItem> cursor,
            PropertyContainerState propertyContainerState, int propertyKeyId )
    {
        applyQueued();
        return delegate.augmentSinglePropertyCursor( cursor, propertyContainerState, propertyKeyId );
    }

    @Override
    public PrimitiveIntSet augmentLabels( PrimitiveIntSet cursor, NodeState nodeState )
    {
        applyQueued();
        return delegate.augmentLabels( cursor, nodeState );
    }

    @Override
    public Cursor<RelationshipItem> augmentSingleRelationshipCursor( Cursor<RelationshipItem> cursor,
            long relationshipId )
    {
        applyQueued();
        return delegate.augmentSingleRelationshipCursor( cursor, relationshipId );
    }

    @Override
    public Cursor<RelationshipItem> augmentNodeRelationshipCursor( Cursor<RelationshipItem> cursor, NodeState nodeState,
            Direction direction )
    {
        applyQueued();
        return delegate.augmentNodeRelationshipCursor( cursor, nodeState, direction );
    }

    @Override
    public Cursor<RelationshipItem> augmentNodeRelationshipCursor( Cursor<RelationshipItem> cursor, NodeState nodeState,
            Direction direction, int[] relTypes )
    {
        applyQueued();
        return delegate.augmentNodeRelationshipCursor( cursor, nodeState, direction, relTypes );
    }

    @Override
    public Cursor<RelationshipItem> augmentRelationshipsGetAllCursor( Cursor<RelationshipItem> cursor )
    {
        applyQueued();
        return delegate.augmentRelationshipsGetAllCursor( cursor );
    }

    @Override
    public boolean hasDataChanges()
    {
        applyQueued();
        return delegate.hasDataChanges();
    }

    // WRITE TX STATE

    @Override
    public void relationshipDoCreate( long id, int relationshipTypeId, long startNodeId, long endNodeId )
    {
        dataChange( () -> delegate.relationshipDoCreate( id, relationshipTypeId, startNodeId, endNodeId ) );
    }

    @Override
    public void nodeDoCreate( long id )
    {
        dataChange( () -> delegate.nodeDoCreate( id ) );
    }

    @Override
    public void relationshipDoDelete( long relationshipId, int type, long startNode, long endNode )
    {
        dataChange( () -> delegate.relationshipDoDelete( relationshipId, type, startNode, endNode ) );
    }

    @Override
    public void relationshipDoDeleteAddedInThisTx( long relationshipId )
    {
        dataChange( () -> delegate.relationshipDoDeleteAddedInThisTx( relationshipId ) );
    }

    @Override
    public void nodeDoDelete( long nodeId )
    {
        dataChange( () -> delegate.nodeDoDelete( nodeId ) );
    }

    @Override
    public void nodeDoAddProperty( long nodeId, DefinedProperty newProperty )
    {
        assert false;
    }

    @Override
    public void nodeDoAddProperty( KernelStatement state, IndexTxStateUpdater indexTxStateUpdater, long nodeId,
            DefinedProperty newProperty )
    {
        dataChange( () -> delegate.nodeDoAddProperty( nodeId, newProperty ) );
        indexChange( () -> indexTxStateUpdater.onPropertyAdd( state, nodeId, newProperty ) );
    }

    @Override
    public void nodeDoChangeProperty( long nodeId, DefinedProperty replacedProperty, DefinedProperty newProperty )
    {
        assert false;
    }

    @Override
    public void nodeDoChangeProperty( KernelStatement state, IndexTxStateUpdater indexTxStateUpdater, long nodeId,
            DefinedProperty replacedProperty, DefinedProperty newProperty )
    {
        dataChange( () -> delegate.nodeDoChangeProperty( nodeId, replacedProperty, newProperty ) );
        indexChange( () -> indexTxStateUpdater.onPropertyChange( state, nodeId, replacedProperty, newProperty ) );
    }

    @Override
    public void relationshipDoReplaceProperty( long relationshipId, Property replacedProperty,
            DefinedProperty newProperty )
    {
        dataChange( () -> delegate.relationshipDoReplaceProperty( relationshipId, replacedProperty, newProperty
        ) );
    }

    @Override
    public void graphDoReplaceProperty( Property replacedProperty, DefinedProperty newProperty )
    {
        dataChange( () -> delegate.graphDoReplaceProperty( replacedProperty, newProperty ) );
    }

    @Override
    public void nodeDoRemoveProperty( long nodeId, DefinedProperty removedProperty )
    {
        assert false;
    }

    @Override
    public void nodeDoRemoveProperty( KernelStatement state, IndexTxStateUpdater indexTxStateUpdater, long nodeId,
            DefinedProperty removedProperty )
    {
        dataChange( () -> delegate.nodeDoRemoveProperty( nodeId, removedProperty ) );
        indexChange( () -> indexTxStateUpdater.onPropertyRemove( state, nodeId, removedProperty ) );
    }

    @Override
    public void relationshipDoRemoveProperty( long relationshipId, DefinedProperty removedProperty )
    {
        dataChange( () -> delegate.relationshipDoRemoveProperty( relationshipId, removedProperty ) );
    }

    @Override
    public void graphDoRemoveProperty( DefinedProperty removedProperty )
    {
        dataChange( () -> delegate.graphDoRemoveProperty( removedProperty ) );
    }

    @Override
    public void nodeDoAddLabel( int labelId, long nodeId )
    {
        assert false;
    }

    @Override
    public void nodeDoAddLabel( KernelStatement state, IndexTxStateUpdater indexTxStateUpdater, int labelId, long nodeId )
    {
        dataChange( () -> delegate.nodeDoAddLabel( labelId, nodeId ) );
        indexChange( () -> indexTxStateUpdater.onLabelChange( state, labelId, nodeId,
                IndexTxStateUpdater.LabelChangeType.ADDED_LABEL ) );
    }

    @Override
    public void nodeDoRemoveLabel( int labelId, long nodeId )
    {
        dataChange( () -> delegate.nodeDoRemoveLabel( labelId, nodeId ) );
    }

    @Override
    public void nodeDoRemoveLabel( KernelStatement state, IndexTxStateUpdater indexTxStateUpdater, int labelId, long nodeId )
    {
        dataChange( () -> delegate.nodeDoRemoveLabel( labelId, nodeId ) );
        indexChange( () -> indexTxStateUpdater.onLabelChange( state, labelId, nodeId,
                IndexTxStateUpdater.LabelChangeType.REMOVED_LABEL ) );
    }

    @Override
    public void labelDoCreateForName( String labelName, int id )
    {
        dataChange( () -> delegate.labelDoCreateForName( labelName, id ) );
    }

    @Override
    public void propertyKeyDoCreateForName( String propertyKeyName, int id )
    {
        dataChange( () -> delegate.propertyKeyDoCreateForName( propertyKeyName, id ) );
    }

    @Override
    public void relationshipTypeDoCreateForName( String relationshipTypeName, int id )
    {
        dataChange( () -> delegate.relationshipTypeDoCreateForName( relationshipTypeName, id ) );
    }

    @Override
    public void indexDoUpdateEntry( LabelSchemaDescriptor descriptor, long nodeId, OrderedPropertyValues before,
            OrderedPropertyValues after )
    {
        dataChange( () -> delegate.indexDoUpdateEntry( descriptor, nodeId, before, after ) );
    }

    // SCHEMA TX READ

    @Override
    public ReadableDiffSets<NewIndexDescriptor> indexDiffSetsByLabel( int labelId, NewIndexDescriptor.Filter indexType )
    {
        return delegate.indexDiffSetsByLabel( labelId, indexType );
    }

    @Override
    public ReadableDiffSets<NewIndexDescriptor> indexChanges( NewIndexDescriptor.Filter indexType )
    {
        return delegate.indexChanges( indexType );
    }

    @Override
    public Iterable<NewIndexDescriptor> constraintIndexesCreatedInTx()
    {
        return delegate.constraintIndexesCreatedInTx();
    }

    @Override
    public ReadableDiffSets<ConstraintDescriptor> constraintsChanges()
    {
        return delegate.constraintsChanges();
    }

    @Override
    public ReadableDiffSets<ConstraintDescriptor> constraintsChangesForLabel( int labelId )
    {
        return delegate.constraintsChangesForLabel( labelId );
    }

    @Override
    public ReadableDiffSets<ConstraintDescriptor> constraintsChangesForSchema( SchemaDescriptor descriptor )
    {
        return delegate.constraintsChangesForSchema( descriptor );
    }

    @Override
    public ReadableDiffSets<ConstraintDescriptor> constraintsChangesForRelationshipType( int relTypeId )
    {
        return delegate.constraintsChangesForRelationshipType( relTypeId );
    }

    @Override
    public Long indexCreatedForConstraint( ConstraintDescriptor constraint )
    {
        return delegate.indexCreatedForConstraint( constraint );
    }

    // SCHEMA TX WRITE

    @Override
    public void indexRuleDoAdd( NewIndexDescriptor descriptor )
    {
        delegate.indexRuleDoAdd( descriptor );
    }

    @Override
    public void indexDoDrop( NewIndexDescriptor descriptor )
    {
        delegate.indexDoDrop( descriptor );
    }

    @Override
    public boolean indexDoUnRemove( NewIndexDescriptor constraint )
    {
        return delegate.indexDoUnRemove( constraint );
    }

    @Override
    public void constraintDoAdd( ConstraintDescriptor constraint )
    {
        delegate.constraintDoAdd( constraint );
    }

    @Override
    public void constraintDoAdd( IndexBackedConstraintDescriptor constraint, long indexId )
    {
        delegate.constraintDoAdd( constraint, indexId );
    }

    @Override
    public void constraintDoDrop( ConstraintDescriptor constraint )
    {
        delegate.constraintDoDrop( constraint );
    }

    @Override
    public boolean constraintDoUnRemove( ConstraintDescriptor constraint )
    {
        return delegate.constraintDoUnRemove( constraint );
    }
}
