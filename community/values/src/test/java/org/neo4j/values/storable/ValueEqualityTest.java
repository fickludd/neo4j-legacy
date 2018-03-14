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
package org.neo4j.values.storable;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.function.BiConsumer;

import org.neo4j.values.utils.AnyValueTestUtil;

import static org.neo4j.values.utils.AnyValueTestUtil.assertEqual;
import static org.neo4j.values.utils.AnyValueTestUtil.assertNotEqual;

/**
 * This test was faithfully converted (including personal remarks) from PropertyEqualityTest.
 */
@RunWith( value = Parameterized.class )
public class ValueEqualityTest
{
    @Parameterized.Parameters( name = "{0}" )
    public static Iterable<Test> data()
    {
        return Arrays.asList(
//                // boolean properties
//                shouldMatch( true, true ),
//                shouldMatch( false, false ),
//                shouldNotMatch( true, false ),
//                shouldNotMatch( false, true ),
//                shouldBeIncomparable( true, 0 ),
//                shouldBeIncomparable( false, 0 ),
//                shouldBeIncomparable( true, 1 ),
//                shouldBeIncomparable( false, 1 ),
//                shouldBeIncomparable( false, "false" ),
//                shouldBeIncomparable( true, "true" ),
//
//                //byte properties
//                shouldMatch( (byte) 42, (byte) 42 ),
//                shouldMatch( (byte) 42, (short) 42 ),
//                shouldNotMatch( (byte) 42, 42 + 256 ),
//                shouldMatch( (byte) 43, 43 ),
//                shouldMatch( (byte) 43, 43L ),
//                shouldMatch( (byte) 23, 23.0d ),
//                shouldMatch( (byte) 23, 23.0f ),
//                shouldNotMatch( (byte) 23, 23.5 ),
//                shouldNotMatch( (byte) 23, 23.5f ),
//
//                //short properties
//                shouldMatch( (short) 11, (byte) 11 ),
//                shouldMatch( (short) 42, (short) 42 ),
//                shouldNotMatch( (short) 42, 42 + 65536 ),
//                shouldMatch( (short) 43, 43 ),
//                shouldMatch( (short) 43, 43L ),
//                shouldMatch( (short) 23, 23.0f ),
//                shouldMatch( (short) 23, 23.0d ),
//                shouldNotMatch( (short) 23, 23.5 ),
//                shouldNotMatch( (short) 23, 23.5f ),
//
//                //int properties
//                shouldMatch( 11, (byte) 11 ),
//                shouldMatch( 42, (short) 42 ),
//                shouldNotMatch( 42, 42 + 4294967296L ),
//                shouldMatch( 43, 43 ),
//                shouldMatch( Integer.MAX_VALUE, Integer.MAX_VALUE ),
//                shouldMatch( 43, (long) 43 ),
//                shouldMatch( 23, 23.0 ),
//                shouldNotMatch( 23, 23.5 ),
//                shouldNotMatch( 23, 23.5f ),
//
//                //long properties
//                shouldMatch( 11L, (byte) 11 ),
//                shouldMatch( 42L, (short) 42 ),
//                shouldMatch( 43L, 43 ),
//                shouldMatch( 43L, 43L ),
//                shouldMatch( 87L, 87L ),
//                shouldMatch( Long.MAX_VALUE, Long.MAX_VALUE ),
//                shouldMatch( 23L, 23.0 ),
//                shouldNotMatch( 23L, 23.5 ),
//                shouldNotMatch( 23L, 23.5f ),
//                shouldMatch(9007199254740992L, 9007199254740992D),
//                // shouldMatch(9007199254740993L, 9007199254740992D), // is stupid, m'kay?!
//
//                // floats goddamnit
//                shouldMatch( 11f, (byte) 11 ),
//                shouldMatch( 42f, (short) 42 ),
//                shouldMatch( 43f, 43 ),
//                shouldMatch( 43f, 43L ),
//                shouldMatch( 23f, 23.0 ),
//                shouldNotMatch( 23f, 23.5 ),
//                shouldNotMatch( 23f, 23.5f ),
//                shouldMatch( 3.14f, 3.14f ),
//                shouldNotMatch( 3.14f, 3.14d ),   // Would be nice if they matched, but they don't
//
//                // doubles
//                shouldMatch( 11d, (byte) 11 ),
//                shouldMatch( 42d, (short) 42 ),
//                shouldMatch( 43d, 43 ),
//                shouldMatch( 43d, 43d ),
//                shouldMatch( 23d, 23.0 ),
//                shouldNotMatch( 23d, 23.5 ),
//                shouldNotMatch( 23d, 23.5f ),
//                shouldNotMatch( 3.14d, 3.14f ),   // this really is sheeeet
//                shouldMatch( 3.14d, 3.14d ),
//
//                // strings
//                shouldMatch( "A", "A" ),
//                shouldMatch( 'A', 'A' ),
//                shouldMatch( 'A', "A" ),
//                shouldMatch( "A", 'A' ),
//                shouldNotMatch( "AA", 'A' ),
//                shouldNotMatch( "a", "A" ),
//                shouldNotMatch( "A", "a" ),
//                shouldBeIncomparable( "0", 0 ),
//                shouldBeIncomparable( '0', 0 ),
//
//                // arrays
//                shouldMatch( new int[]{1, 2, 3}, new int[]{1, 2, 3} ),
//                shouldMatch( new int[]{1, 2, 3}, new long[]{1, 2, 3} ),
//                shouldMatch( new int[]{1, 2, 3}, new double[]{1.0, 2.0, 3.0} ),
//                shouldMatch( new String[]{"A", "B", "C"}, new String[]{"A", "B", "C"} ),
//                shouldMatch( new String[]{"A", "B", "C"}, new char[]{'A', 'B', 'C'} ),
//                shouldMatch( new char[]{'A', 'B', 'C'},  new String[]{"A", "B", "C"} ),

                shouldBeIncomparable( false, new boolean[]{false} ),
                shouldBeIncomparable( 1, new int[]{1} ),
                shouldBeIncomparable( "apa", new String[]{"apa"} )
        );
    }

    private Test currentTest;

    public ValueEqualityTest( Test currentTest )
    {
        this.currentTest = currentTest;
    }

    @org.junit.Test
    public void runTest()
    {
        currentTest.checkAssertion();
    }

    private static Test shouldMatch( Object value1, Object value2 )
    {
        return new Test( Values.of( value1 ), Values.of( value2 ), AnyValueTestUtil::assertEqual, "==" );
    }

    private static Test shouldNotMatch( Object value1, Object value2 )
    {
        return new Test( Values.of( value1 ), Values.of( value2 ), AnyValueTestUtil::assertNotEqual, "!=" );
    }

    private static Test shouldBeIncomparable( Object value1, Object value2 )
    {
        return new Test( Values.of( value1 ), Values.of( value2 ), AnyValueTestUtil::assertIncomparable, "incomparable to" );
    }

    private static class Test
    {
        final Value a;
        final Value b;
        final BiConsumer<Value, Value> assertion;
        final String operator;

        private Test( Value a, Value b, BiConsumer<Value,Value> assertion, String operator )
        {
            this.a = a;
            this.b = b;
            this.assertion = assertion;
            this.operator = operator;
        }

        @Override
        public String toString()
        {
            return String.format( "%s %s %s", a, operator, b );
        }

        void checkAssertion()
        {
            assertion.accept( a, b );
        }
    }
}
