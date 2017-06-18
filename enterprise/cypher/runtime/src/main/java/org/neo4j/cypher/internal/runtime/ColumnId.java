package org.neo4j.cypher.internal.runtime;

public class ColumnId
{
    public static final ColumnId NO_COLUMN = null;

    final String name;

    public ColumnId( String name )
    {
        this.name = name;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        ColumnId columnId = (ColumnId) o;

        return name != null ? name.equals( columnId.name ) : columnId.name == null;
    }

    @Override
    public int hashCode()
    {
        return name != null ? name.hashCode() : 0;
    }
}
