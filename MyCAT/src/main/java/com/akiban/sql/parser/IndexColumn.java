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
 * An IndexColumn is the element of an index definition.
 */
public class IndexColumn extends QueryTreeNode
{
    private TableName tableName;
    private String columnName;
    private boolean ascending = true;

    /**
     * Initializer.
     *
     * @param columnName Name of the column
     * @param ascending Whether index is ascending
     */
    public void init(Object columnName,
                     Object ascending) {
        this.tableName = null;
        this.columnName = (String)columnName;
        this.ascending = ((Boolean)ascending).booleanValue();
    }

    /**
     * Initializer.
     *
     * @param tableName Table holding indexed column
     * @param columnName Name of the column
     * @param ascending Whether index is ascending
     */
    public void init(Object tableName,
                     Object columnName,
                     Object ascending) {
        this.tableName = (TableName)tableName;
        this.columnName = (String)columnName;
        this.ascending = ((Boolean)ascending).booleanValue();
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        IndexColumn other = (IndexColumn)node;
        this.tableName = (TableName)getNodeFactory().copyNode(other.tableName, 
                                                              getParserContext());
        this.columnName = other.columnName;
        this.ascending = other.ascending;
    }

    /**
     * Convert this object to a String.  See comments in QueryTreeNode.java
     * for how this should be done for tree printing.
     *
     * @return This object as a String
     */
    public String toString() {
        return "columnName: " + columnName + "\n" +
            "tableName: " + ((tableName != null) ? tableName.toString() : "null") + "\n" +
            (ascending ? "ascending" : "descending") + "\n" +
            super.toString();
    }

    public TableName getTableName() {
        return tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public boolean isPartOfGroupIndex() {
        return tableName != null;
    }

    /**
     * @return true if ascending, false if descending
     */
    public boolean isAscending() {
        return ascending;
    }

}