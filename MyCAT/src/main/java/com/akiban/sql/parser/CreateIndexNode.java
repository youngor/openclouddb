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

import com.akiban.sql.parser.JoinNode.JoinType;

import com.akiban.sql.StandardException;

import java.util.Properties;

/**
 * A CreateIndexNode is the root of a QueryTree that represents a CREATE INDEX
 * statement.
 *
 */

public class CreateIndexNode extends DDLStatementNode implements IndexDefinition
{
    boolean unique;
    String indexType;
    TableName indexName;
    TableName tableName;
    IndexColumnList columnList;
    JoinType joinType;
    Properties properties;
    ExistenceCheck existenceCheck;
    StorageLocation storageLocation;
    
    /**
     * Initializer for a CreateIndexNode
     *
     * @param unique True means it's a unique index
     * @param indexType The type of index
     * @param indexName The name of the index
     * @param tableName The name of the table the index will be on
     * @param columnList A list of columns, in the order they
     *                   appear in the index.
     * @param properties The optional properties list associated with the index.
     *
     * @exception StandardException Thrown on error
     */
    public void init(Object unique,
                     Object indexType,
                     Object indexName,
                     Object tableName,
                     Object columnList,
                     Object joinType,
                     Object properties,
                     Object existenceCheck,
                     Object storageLocation) 
            throws StandardException {
        initAndCheck(indexName);
        this.unique = ((Boolean)unique).booleanValue();
        this.indexType = (String)indexType;
        this.indexName = (TableName)indexName;
        this.tableName = (TableName)tableName;
        this.columnList = (IndexColumnList)columnList;
        this.joinType = (JoinType)joinType;
        this.properties = (Properties)properties;
        this.existenceCheck = (ExistenceCheck)existenceCheck;
        this.storageLocation = (StorageLocation) storageLocation;
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        CreateIndexNode other = (CreateIndexNode)node;
        this.unique = other.unique;
        this.indexType = other.indexType;
        this.indexName = (TableName)
            getNodeFactory().copyNode(other.indexName, getParserContext());
        this.tableName = (TableName)
            getNodeFactory().copyNode(other.tableName, getParserContext());
        this.columnList = (IndexColumnList)
            getNodeFactory().copyNode(other.columnList, getParserContext());
        this.joinType = other.joinType;
        this.properties = other.properties; // TODO: Clone?
        this.existenceCheck = other.existenceCheck;
        this.storageLocation = other.storageLocation;
    }

    /**
     * Convert this object to a String.  See comments in QueryTreeNode.java
     * for how this should be done for tree printing.
     *
     * @return This object as a String
     */

    public String toString() {
        return super.toString() +
            "unique: " + unique + "\n" +
            "indexType: " + indexType + "\n" +
            "indexName: " + indexName + "\n" +
            "tableName: " + tableName + "\n" +
            "joinType: " + joinType + "\n" +
            "properties: " + properties + "\n" +
            "existenceCheck: " + existenceCheck + "\n" +
            "storageLocation: " + storageLocation + "\n";
    }

    public void printSubNodes(int depth) {
        if (columnList != null) {
            columnList.treePrint(depth+1);
        }
    }
    public String statementToString() {
        return "CREATE INDEX";
    }

    public boolean getUniqueness() { 
        return unique; 
    }
    public String getIndexType() { 
        return indexType;
    }
    public TableName getIndexName() { 
        return indexName; 
    }
    public IndexColumnList getColumnList() {
        return columnList;
    }
    public IndexColumnList getIndexColumnList() {
        return columnList;
    }
    public JoinType getJoinType() {
        return joinType;
    }
    public Properties getProperties() { 
        return properties; 
    }
    public TableName getIndexTableName() {
        return tableName; 
    }

    public ExistenceCheck getExistenceCheck()
    {
        return existenceCheck;
    }
    
    public StorageLocation getStorageLocation()
    {
        return storageLocation;
    }
}