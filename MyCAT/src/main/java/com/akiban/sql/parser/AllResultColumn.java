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
 * An AllResultColumn represents a "*" result column in a SELECT
 * statement.    It gets replaced with the appropriate set of columns
 * at bind time.
 *
 */

public class AllResultColumn extends ResultColumn
{
    private TableName tableName;
    private boolean recursive;

    /**
     * This initializer is for use in the parser for a "*".
     * 
     * @param arg TableName Dot expression qualifying "*" or Boolean recursive
     */
    public void init(Object arg) {
        if (arg instanceof Boolean)
            this.recursive = (Boolean)arg;
        else
            this.tableName = (TableName)arg;
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        AllResultColumn other = (AllResultColumn)node;
        this.tableName = (TableName)getNodeFactory().copyNode(other.tableName,
                                                              getParserContext());
        this.recursive = other.recursive;
    }

    /** 
     * Return the full table name qualification for this node
     *
     * @return Full table name qualification as a String
     */
    public String getFullTableName() {
        if (tableName == null) {
            return null;
        }
        else {
            return tableName.getFullTableName();
        }
    }

    public TableName getTableNameObject() {
        return tableName;
    }

    public boolean isRecursive() {
        return recursive;
    }

    /**
     * Convert this object to a String.  See comments in QueryTreeNode.java
     * for how this should be done for tree printing.
     *
     * @return This object as a String
     */
    // TODO: Somewhat of a mess: the superclass has a tableName field of a different type.
    public String toString() {
        return "tableName: " + tableName + "\n" +
            super.toString();
    }
}