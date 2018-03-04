/*
 * Copyright (c) 2002-2018 "Neo Technology,"
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
package org.neo4j.kernel.impl.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.values.AnyValueWriter;
import org.neo4j.values.virtual.NodeValue;

/**
 * This is a special snowflake node which is used to get the result of the db.schema() out through
 * the runtime to the user. Once we allow multi-graphs or att least virtual nodes, this should no
 * longer be needed.
 */
public class DBSchemaProcedureNode extends NodeValue implements Node
{
    private final HashMap<String,Object> propertyMap = new HashMap<>();

    private static AtomicLong MIN_ID = new AtomicLong( -1 );
    private final long id;
    private final Label label;

    public DBSchemaProcedureNode( final String label, Map<String,Object> properties )
    {
        this.id = MIN_ID.getAndDecrement();
        this.label = Label.label( label );
        propertyMap.putAll( properties );
        propertyMap.put( "name", label );
    }

    @Override
    public long getId()
    {
        return id;
    }

    @Override
    public Map<String,Object> getAllProperties()
    {
        return propertyMap;
    }

    @Override
    public Iterable<Label> getLabels()
    {
        return Collections.singletonList( label );
    }

    @Override
    public void delete()
    {

    }

    @Override
    public Iterable<Relationship> getRelationships()
    {
        return null;
    }

    @Override
    public boolean hasRelationship()
    {
        return false;
    }

    @Override
    public Iterable<Relationship> getRelationships( RelationshipType... types )
    {
        return null;
    }

    @Override
    public Iterable<Relationship> getRelationships( Direction direction, RelationshipType... types )
    {
        return null;
    }

    @Override
    public Iterable<Relationship> getRelationships( RelationshipType type, Direction direction )
    {
        return null;
    }

    @Override
    public Iterable<Relationship> getRelationships( Direction direction )
    {
        return null;
    }

    @Override
    public boolean hasRelationship( RelationshipType... types )
    {
        return false;
    }

    @Override
    public boolean hasRelationship( Direction direction, RelationshipType... types )
    {
        return false;
    }

    @Override
    public boolean hasRelationship( RelationshipType type, Direction direction )
    {
        return false;
    }

    @Override
    public boolean hasRelationship( Direction direction )
    {
        return false;
    }

    @Override
    public Relationship getSingleRelationship( RelationshipType type, Direction dir )
    {
        return null;
    }

    @Override
    public Relationship createRelationshipTo( Node otherNode, RelationshipType type )
    {
        return null;
    }

    @Override
    public Iterable<RelationshipType> getRelationshipTypes()
    {
        return null;
    }

    @Override
    public int getDegree()
    {
        return 0;
    }

    @Override
    public int getDegree( RelationshipType type )
    {
        return 0;
    }

    @Override
    public int getDegree( RelationshipType type, Direction direction )
    {
        return 0;
    }

    @Override
    public int getDegree( Direction direction )
    {
        return 0;
    }

    @Override
    public void addLabel( Label label )
    {

    }

    @Override
    public void removeLabel( Label label )
    {

    }

    @Override
    public boolean hasLabel( Label label )
    {
        return false;
    }

    @Override
    public GraphDatabaseService getGraphDatabase()
    {
        return null;
    }

    @Override
    public boolean hasProperty( String key )
    {
        return false;
    }

    @Override
    public Object getProperty( String key )
    {
        return null;
    }

    @Override
    public Object getProperty( String key, Object defaultValue )
    {
        return null;
    }

    @Override
    public void setProperty( String key, Object value )
    {

    }

    @Override
    public Object removeProperty( String key )
    {
        return null;
    }

    @Override
    public Iterable<String> getPropertyKeys()
    {
        return null;
    }

    @Override
    public Map<String,Object> getProperties( String... keys )
    {
        return null;
    }

    @Override
    public String toString()
    {
        return String.format( "DBSchemaProcedureNode[%s]", id );
    }

    // VirtualNodeValue

    @Override
    public long id()
    {
        return id;
    }

    @Override
    public <E extends Exception> void writeTo( AnyValueWriter<E> writer ) throws E
    {
        writer.writeNodeReference( id );
    }
}

