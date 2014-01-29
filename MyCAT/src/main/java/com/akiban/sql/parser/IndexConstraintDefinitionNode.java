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
import com.akiban.sql.parser.JoinNode.JoinType;

public class IndexConstraintDefinitionNode extends ConstraintDefinitionNode implements IndexDefinition
{
    private String indexName;
    private IndexColumnList indexColumnList;
    private JoinType joinType;
    private StorageLocation location;
    
    @Override
    public void init(Object tableName,
                     Object indexColumnList,
                     Object indexName,
                     Object joinType,
                     Object location)
    {
        super.init(tableName,
                   ConstraintType.INDEX,
                   null, // column list? don't need. Use indexColumnList instead
                   null, // properties - none
                   null, // constrainText  - none
                   null, // conditionCheck  - none
                   StatementType.UNKNOWN, // behaviour? 
                   ConstraintType.INDEX);
        
        this.indexName = (String) indexName;
        this.indexColumnList = (IndexColumnList) indexColumnList;
        this.joinType = (JoinType) joinType;
        this.location = (StorageLocation) location;
    }
    
    public String getIndexName()
    {
        return indexName;
    }
    
    public IndexColumnList getIndexColumnList()
    {
        return indexColumnList;
    }

    public JoinType getJoinType()
    {
        return joinType;
    }
    
    public StorageLocation getLocation()
    {
        return location;
    }
    
    // This is used for the non-unique "INDEX" defintions only
    public boolean getUniqueness() 
    {
        return false;
    }
    
    public TableName getObjectName()
    {
        return constraintName;
    }
    
    @Override
    public void copyFrom(QueryTreeNode node) throws StandardException
    {
        super.copyFrom(node);
        
        IndexConstraintDefinitionNode other = (IndexConstraintDefinitionNode) node;
        this.indexName = other.indexName;
        this.indexColumnList = other.indexColumnList;
        this.joinType = other.joinType;
        this.location = other.location;
    }
    
    @Override
    public String toString()
    {
        return super.toString()
                + "\nindexName: " + indexName
                + "\njoinType: " + joinType
                + "\nlocation: " + location
                ;
    }

    @Override
    public void printSubNodes(int depth) {
        super.printSubNodes(depth);
        if (indexColumnList != null) {
            printLabel(depth, "indexColumnList: ");
            indexColumnList.treePrint(depth + 1);
        }
    }
    
}