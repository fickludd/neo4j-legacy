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
package org.neo4j.kernel.api.impl.schema;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.kernel.api.impl.index.storage.DirectoryFactory;
import org.neo4j.kernel.api.index.IndexAccessor;
import org.neo4j.kernel.api.index.IndexProviderCompatibilityTestSuite;
import org.neo4j.kernel.api.schema_new.index.NewIndexDescriptor;
import org.neo4j.kernel.api.schema_new.index.NewIndexDescriptorFactory;
import org.neo4j.kernel.configuration.Config;
import org.neo4j.kernel.configuration.Settings;
import org.neo4j.kernel.impl.api.index.IndexUpdateMode;
import org.neo4j.kernel.impl.api.index.sampling.IndexSamplingConfig;
import org.neo4j.kernel.impl.factory.OperationalMode;
import org.neo4j.logging.NullLogProvider;

import static org.neo4j.helpers.collection.MapUtil.stringMap;

public class LuceneSchemaIndexProviderTest extends IndexProviderCompatibilityTestSuite
{
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static final NewIndexDescriptor descriptor = NewIndexDescriptorFactory.forLabel( 1, 1 );

    @Override
    protected LuceneSchemaIndexProvider createIndexProvider()
    {
        return getLuceneSchemaIndexProvider( Config.defaults(), new DirectoryFactory.InMemoryDirectoryFactory() );
    }

    @Test
    public void shouldFailToInvokePopulatorInReadOnlyMode() throws Exception
    {
        Config readOnlyConfig = Config.embeddedDefaults( stringMap( GraphDatabaseSettings.read_only.name(), Settings.TRUE ) );
        LuceneSchemaIndexProvider readOnlyIndexProvider = getLuceneSchemaIndexProvider( readOnlyConfig,
                new DirectoryFactory.InMemoryDirectoryFactory() );
        expectedException.expect( UnsupportedOperationException.class );

        readOnlyIndexProvider.getPopulator( 1L, descriptor, new IndexSamplingConfig(
                readOnlyConfig ) );
    }

    @Test
    public void shouldCreateReadOnlyAccessorInReadOnlyMode() throws Exception
    {
        DirectoryFactory directoryFactory = DirectoryFactory.PERSISTENT;
        createEmptySchemaIndex( directoryFactory );

        Config readOnlyConfig = Config.embeddedDefaults( stringMap( GraphDatabaseSettings.read_only.name(), Settings.TRUE ) );
        LuceneSchemaIndexProvider readOnlyIndexProvider = getLuceneSchemaIndexProvider( readOnlyConfig,
                directoryFactory );
        IndexAccessor onlineAccessor = getIndexAccessor( readOnlyConfig, readOnlyIndexProvider );

        expectedException.expect( UnsupportedOperationException.class );
        onlineAccessor.drop();
    }

    @Test
    public void indexUpdateNotAllowedInReadOnlyMode() throws Exception
    {
        Config readOnlyConfig = Config.embeddedDefaults( stringMap( GraphDatabaseSettings.read_only.name(), Settings.TRUE ) );
        LuceneSchemaIndexProvider readOnlyIndexProvider = getLuceneSchemaIndexProvider( readOnlyConfig,
                new DirectoryFactory.InMemoryDirectoryFactory() );

        expectedException.expect( UnsupportedOperationException.class );
        getIndexAccessor( readOnlyConfig, readOnlyIndexProvider ).newUpdater( IndexUpdateMode.ONLINE);
    }

    private void createEmptySchemaIndex( DirectoryFactory directoryFactory ) throws IOException
    {
        Config config = Config.defaults();
        LuceneSchemaIndexProvider indexProvider = getLuceneSchemaIndexProvider( config, directoryFactory );
        IndexAccessor onlineAccessor = getIndexAccessor( config, indexProvider );
        onlineAccessor.flush();
        onlineAccessor.close();
    }

    private IndexAccessor getIndexAccessor( Config readOnlyConfig, LuceneSchemaIndexProvider indexProvider )
            throws IOException
    {
        return indexProvider.getOnlineAccessor( 1L, descriptor, new IndexSamplingConfig( readOnlyConfig ) );
    }

    private LuceneSchemaIndexProvider getLuceneSchemaIndexProvider( Config config, DirectoryFactory directoryFactory )
    {
        return new LuceneSchemaIndexProvider( fs, directoryFactory, graphDbDir, NullLogProvider.getInstance(), config, OperationalMode.single );
    }
}
