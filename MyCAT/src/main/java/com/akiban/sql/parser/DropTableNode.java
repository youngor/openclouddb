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
 * A DropTableNode is the root of a QueryTree that represents a DROP TABLE
 * statement.
 *
 */

public class DropTableNode extends DDLStatementNode
{
    private int dropBehavior;
    private ExistenceCheck existenceCheck;

    /**
     * Intializer for a DropTableNode
     *
     * @param dropObjectName The name of the object being dropped
     * @param dropBehavior Drop behavior (RESTRICT | CASCADE)
     *
     */

    public void init(Object dropObjectName, Object dropBehavior, Object ec)
            throws StandardException {
        initAndCheck(dropObjectName);
        this.dropBehavior = ((Integer)dropBehavior).intValue();
        this.existenceCheck = (ExistenceCheck)ec;
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        DropTableNode other = (DropTableNode)node;
        this.dropBehavior = other.dropBehavior;
        this.existenceCheck = other.existenceCheck;
    }

    public int getDropBehavior() {
        return dropBehavior;
    }
    
    public ExistenceCheck getExistenceCheck()
    {
        return existenceCheck;
    }

    /**
     * Convert this object to a String.  See comments in QueryTreeNode.java
     * for how this should be done for tree printing.
     *
     * @return This object as a String
     */
    public String toString() {
        return super.toString() +
            "dropBehavior: " + dropBehavior + "\n"
           + "existenceCheck: " + existenceCheck + "\n";
    }

    public String statementToString() {
        return "DROP TABLE";
    }
}