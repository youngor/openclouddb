/*
 * Copyright (c) 2013, OpenCloudDB/MyCAT and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software;Designed and Developed mainly by many Chinese 
 * opensource volunteers. you can redistribute it and/or modify it under the 
 * terms of the GNU General Public License version 2 only, as published by the
 * Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Any questions about this component can be directed to it's project Web address 
 * https://code.google.com/p/opencloudb/.
 *
 */
package com.akiban.sql.parser;

import com.akiban.sql.StandardException;
import com.akiban.sql.types.DataTypeDescriptor;

/**
 * A BaseColumnNode represents a column in a base table.    The parser generates a
 * BaseColumnNode for each column reference.    A column refercence could be a column in
 * a base table, a column in a view (which could expand into a complex
 * expression), or a column in a subquery in the FROM clause.    By the time
 * we get to code generation, all BaseColumnNodes should stand only for columns
 * in base tables.
 *
 */

public class BaseColumnNode extends ValueNode
{
    private String columnName;

    /*
    ** This is the user-specified table name.    It will be null if the
    ** user specifies a column without a table name.    
    */
    private TableName tableName;

    /**
     * Initializer for when you only have the column name.
     *
     * @param columnName The name of the column being referenced
     * @param tableName The qualification for the column
     * @param type DataTypeDescriptor for the column
     */

    public void init(Object columnName,
                     Object tableName,
                     Object type) 
            throws StandardException {
        this.columnName = (String)columnName;
        this.tableName = (TableName)tableName;
        setType((DataTypeDescriptor)type);
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        BaseColumnNode other = (BaseColumnNode)node;
        this.columnName = other.columnName;
        this.tableName = (TableName)getNodeFactory().copyNode(other.tableName, 
                                                              getParserContext());
    }

    /**
     * Convert this object to a String.  See comments in QueryTreeNode.java
     * for how this should be done for tree printing.
     *
     * @return This object as a String
     */

    public String toString() {
        return "columnName: " + columnName + "\n" +
            "tableName: " +
            ( ( tableName != null) ?
              tableName.toString() :
              "null") + "\n" +
            super.toString();
    }

    /**
     * Get the name of this column
     *
     * @return The name of this column
     */

    public String getColumnName()
    {
        return columnName;
    }

    /**
     * Get the user-supplied table name of this column.  This will be null
     * if the user did not supply a name (for example, select a from t).
     * The method will return B for this example, select b.a from t as b
     * The method will return T for this example, select t.a from t
     *
     * @return The user-supplied name of this column.    Null if no user-
     *               supplied name.
     */

    public String getTableName() {
        return ((tableName != null) ? tableName.getTableName() : null);
    }

    /**
     * Get the user-supplied schema name for this column's table. This will be null
     * if the user did not supply a name (for example, select t.a from t).
     * Another example for null return value (for example, select b.a from t as b).
     * But for following query select app.t.a from t, this will return APP
     *
     * @return The schema name for this column's table
     */
    public String getSchemaName() throws StandardException {
        return ((tableName != null) ? tableName.getSchemaName() : null);
    }
                
    /**
     * {@inheritDoc}
     */
    protected boolean isEquivalent(ValueNode o) {
        if (isSameNodeType(o)) {
            BaseColumnNode other = (BaseColumnNode)o;
            return other.tableName.equals(tableName) &&
                other.columnName.equals(columnName);
        } 
        return false;
    }
}