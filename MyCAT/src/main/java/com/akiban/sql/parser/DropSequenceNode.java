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
 * A DropSequenceNode    represents a DROP SEQUENCE statement.
 */

public class DropSequenceNode extends DDLStatementNode 
{
    private TableName dropItem;
    private ExistenceCheck existenceCheck;
    private int dropBehavior;
    /**
     * Initializer for a DropSequenceNode
     *
     * @param dropSequenceName The name of the sequence being dropped
     * @throws StandardException
     */
    public void init(Object dropSequenceName, Object dropBehavior, Object ec) throws StandardException {
        dropItem = (TableName)dropSequenceName;
        initAndCheck(dropItem);
        this.dropBehavior = ((Integer)dropBehavior).intValue();
        this.existenceCheck = (ExistenceCheck)ec;
    }

    public int getDropBehavior() {
        return dropBehavior;
    }
    
    public ExistenceCheck getExistenceCheck()
    {
        return existenceCheck;
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        DropSequenceNode other = (DropSequenceNode)node;
        this.dropBehavior = other.dropBehavior;
        this.existenceCheck = other.existenceCheck;
        this.dropItem = (TableName)getNodeFactory().copyNode(other.dropItem,
                                                             getParserContext());
    }

    public String statementToString() {
        return "DROP SEQUENCE ".concat(dropItem.getTableName());
    }

    public String toString() {
        return super.toString() + 
                "dropBehavior: " + dropBehavior + "\n"
                + "existenceCheck: " + existenceCheck + "\n";
    }
}