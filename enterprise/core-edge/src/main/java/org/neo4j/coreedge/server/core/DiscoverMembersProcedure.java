/*
 * Copyright (c) 2002-2016 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.coreedge.server.core;

import java.util.Set;
import java.util.stream.Stream;

import org.neo4j.collection.RawIterator;
import org.neo4j.coreedge.discovery.ReadOnlyTopologyService;
import org.neo4j.coreedge.server.AdvertisedSocketAddress;
import org.neo4j.coreedge.server.BoltAddress;
import org.neo4j.helpers.collection.Iterators;
import org.neo4j.kernel.api.exceptions.ProcedureException;
import org.neo4j.kernel.api.proc.CallableProcedure;
import org.neo4j.kernel.api.proc.ProcedureSignature;
import org.neo4j.kernel.impl.logging.LogService;
import org.neo4j.logging.Log;
import org.neo4j.logging.LogProvider;

import static java.lang.Integer.parseInt;
import static java.util.stream.Collectors.toSet;

public class DiscoverMembersProcedure extends CallableProcedure.BasicProcedure
{
    public static final String NAME = "discoverMembers";
    private final ReadOnlyTopologyService discoveryService;
    private final Log log;

    public DiscoverMembersProcedure( ReadOnlyTopologyService discoveryService, LogProvider logProvider )
    {
        super( new ProcedureSignature(
                new ProcedureSignature.ProcedureName( new String[]{"dbms", "cluster"}, NAME ) ) );
        this.discoveryService = discoveryService;
        this.log = logProvider.getLog( getClass() );
    }

    @Override
    public RawIterator<Object[], ProcedureException> apply( Context ctx, Object[] input ) throws ProcedureException
    {
        Set<AdvertisedSocketAddress> addresses = findAddresses().map( BoltAddress::getBoltAddress )
                .limit( noOfAddressesToReturn( input ) ).collect( toSet() );
        log.info( "Discovery members: %s", addresses.stream().collect( toSet() ) );
        return wrapUpAddresses( addresses );
    }

    private Stream<BoltAddress> findAddresses()
    {
        return discoveryService.currentTopology().boltCoreMembers().stream();
    }

    private int noOfAddressesToReturn( Object[] input )
    {
        if ( input.length == 0 )
        {
            return Integer.MAX_VALUE;
        }

        try
        {
            return parseInt( input[0].toString() );
        }
        catch ( NumberFormatException e )
        {
            return Integer.MAX_VALUE;
        }
    }

    private static RawIterator<Object[], ProcedureException> wrapUpAddresses(
            Set<AdvertisedSocketAddress> boltAddresses )
    {
        return Iterators.map( ( l ) -> new Object[]{l.toString()},
                Iterators.asRawIterator( boltAddresses.stream().iterator() ) );
    }
}
