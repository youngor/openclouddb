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

/**
 * A ColumnReference represents a column in the query tree.  The parser generates a
 * ColumnReference for each column reference.    A column refercence could be a column in
 * a base table, a column in a view (which could expand into a complex
 * expression), or a column in a subquery in the FROM clause.
 *
 */

public class ColumnReference extends ValueNode
{
    private String columnName;

    /*
    ** This is the user-specified table name.    It will be null if the
    ** user specifies a column without a table name.    Leave it null even
    ** when the column is bound as it is only used in binding.
    */
    private TableName tableName;

    /**
     * Initializer.
     * This one is called by the parser where we could
     * be dealing with delimited identifiers.
     *
     * @param columnName The name of the column being referenced
     * @param tableName The qualification for the column
     * @param tokBeginOffset begin position of token for the column name 
     *              identifier from parser.
     * @param tokEndOffsetend position of token for the column name 
     *              identifier from parser.
     */

    public void init(Object columnName, 
                     Object tableName,
                     Object tokBeginOffset,
                     Object tokEndOffset) {
        this.columnName = (String)columnName;
        this.tableName = (TableName)tableName;
        this.setBeginOffset(((Integer)tokBeginOffset).intValue());
        this.setEndOffset(((Integer)tokEndOffset).intValue());
    }

    /**
     * Initializer.
     *
     * @param columnName The name of the column being referenced
     * @param tableName The qualification for the column
     */

    public void init(Object columnName, Object tableName) {
        this.columnName = (String)columnName;
        this.tableName = (TableName)tableName;
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        ColumnReference other = (ColumnReference)node;
        this.columnName = other.columnName;
        this.tableName = (TableName)
            getNodeFactory().copyNode(other.tableName, getParserContext());
    }

    /**
     * Convert this object to a String.  See comments in QueryTreeNode.java
     * for how this should be done for tree printing.
     *
     * @return This object as a String
     */

    public String toString() {
        return "columnName: " + columnName + "\n" +
            "tableName: " + ( ( tableName != null) ?
                              tableName.toString() :
                              "null") + "\n" +
            super.toString();
    }

    /**
     * Prints the sub-nodes of this object.  See QueryTreeNode.java for
     * how tree printing is supposed to work.
     *
     * @param depth The depth of this node in the tree
     */

    public void printSubNodes(int depth) {
        super.printSubNodes(depth);
    }

    /**
     * Get the column name for purposes of error
     * messages or debugging. This returns the column
     * name as used in the SQL statement. Thus if it was qualified
     * with a table, alias name that will be included.
     *
     * @return The column name in the form [[schema.]table.]column
     */

    public String getSQLColumnName() {
        if (tableName == null)
            return columnName;

        return tableName.toString() + "." + columnName;
    }

    /**
     * Get the name of this column
     *
     * @return The name of this column
     */

    public String getColumnName() {
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
       Return the table name as the node it is.
       @return the column's table name.
    */
    public TableName getTableNameNode() {
        return tableName;
    }

    public void setTableNameNode(TableName tableName) {
        this.tableName = tableName;
    }

    /**
     * Get the user-supplied schema name of this column.    This will be null
     * if the user did not supply a name (for example, select t.a from t).
     * Another example for null return value (for example, select b.a from t as b).
     * But for following query select app.t.a from t, this will return APP
     * Code generation of aggregate functions relies on this method
     *
     * @return The user-supplied schema name of this column.    Null if no user-
     *               supplied name.
     */

    public String getSchemaName() {
        return ((tableName != null) ? tableName.getSchemaName() : null);
    }

    protected boolean isEquivalent(ValueNode o) throws StandardException {
        if (!isSameNodeType(o)) {
            return false;
        }
        ColumnReference other = (ColumnReference)o;
        if (!columnName.equals(other.getColumnName())) {
            return false;
        }
        if (tableName == null)
            return other.tableName == null;
        else
            return tableName.equals(other.tableName);
    }

}