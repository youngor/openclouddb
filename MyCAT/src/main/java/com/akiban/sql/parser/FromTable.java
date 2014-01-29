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

import java.util.Properties;

/**
 * A FromTable represents a table in the FROM clause of a DML statement.
 * It can be either a base table, a subquery or a project restrict.
 *
 * @see FromBaseTable
 * @see FromSubquery
 * @see ProjectRestrictNode
 *
 */
public abstract class FromTable extends ResultSetNode
{
    protected Properties tableProperties;
    protected String correlationName;
    private TableName corrTableName;

    /** the original unbound table name */
    // TODO: Still need these two separate names?
    protected TableName origTableName;

    /**
     * Initializer for a table in a FROM list.
     *
     * @param correlationName The correlation name
     * @param tableProperties Properties list associated with the table
     */
    public void init(Object correlationName, Object tableProperties) {
        this.correlationName = (String)correlationName;
        this.tableProperties = (Properties)tableProperties;
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        FromTable other = (FromTable)node;
        this.tableProperties = other.tableProperties; // TODO: Clone?
        this.correlationName = other.correlationName;
        this.corrTableName = (TableName)getNodeFactory().copyNode(other.corrTableName,
                                                                  getParserContext());
        this.origTableName = (TableName)getNodeFactory().copyNode(other.origTableName,
                                                                  getParserContext());
    }

    /**
     * Get this table's correlation name, if any.
     */
    public String getCorrelationName() { 
        return correlationName; 
    }

    /**
     * Set this table's correlation name.
     */
    public void setCorrelationName(String correlationName) { 
        this.correlationName = correlationName; 
    }

    /**
     * Convert this object to a String.  See comments in QueryTreeNode.java
     * for how this should be done for tree printing.
     *
     * @return This object as a String
     */

    public String toString() {
        return "correlation Name: " + correlationName + "\n" +
            (corrTableName != null ?
             corrTableName.toString() : "null") + "\n" +
            super.toString();
    }

    /**
     * Return a TableName node representing this FromTable.
     * Expect this to be overridden (and used) by subclasses
     * that may set correlationName to null.
     *
     * @return a TableName node representing this FromTable.
     * @exception StandardException Thrown on error
     */
    public TableName getTableName() throws StandardException {
        if (correlationName == null) return null;

        if (corrTableName == null) {
            corrTableName = makeTableName(null, correlationName);
        }
        return corrTableName;
    }

    public String getExposedName() throws StandardException {
        return null;
    }

    /**
     * Sets the original or unbound table name for this FromTable.  
     * 
     * @param tableName the unbound table name
     *
     */
    public void setOrigTableName(TableName tableName) {
        this.origTableName = tableName;
    }

    /**
     * Gets the original or unbound table name for this FromTable.  
     * The tableName field can be changed due to synonym resolution.
     * Use this method to retrieve the actual unbound tablename.
     * 
     * @return TableName the original or unbound tablename
     *
     */
    public TableName getOrigTableName() {
        return this.origTableName;
    }

}