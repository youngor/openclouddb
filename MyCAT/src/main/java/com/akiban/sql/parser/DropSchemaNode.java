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
 * A DropSchemaNode is the root of a QueryTree that represents 
 * a DROP SCHEMA statement.
 *
 */

public class DropSchemaNode extends DDLStatementNode
{
    private int dropBehavior;
    private String schemaName;
    private ExistenceCheck existenceCheck;

    /**
     * Initializer for a DropSchemaNode
     *
     * @param schemaName The name of the object being dropped
     * @param dropBehavior Drop behavior (RESTRICT | CASCADE)
     *
     */
    public void init(Object schemaName, Object dropBehavior, Object ec) throws StandardException {
        initAndCheck(null);
        this.schemaName = (String)schemaName;
        this.dropBehavior = ((Integer)dropBehavior).intValue();
        this.existenceCheck = (ExistenceCheck)ec;
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        DropSchemaNode other = (DropSchemaNode)node;
        this.dropBehavior = other.dropBehavior;
        this.schemaName = other.schemaName;
        this.existenceCheck = other.existenceCheck;
    }

    /**
     * Convert this object to a String.  See comments in QueryTreeNode.java
     * for how this should be done for tree printing.
     *
     * @return This object as a String
     */

    public String toString() {
        return super.toString() +
            "schemaName: " + "\n" + schemaName + "\n" +
            "dropBehavior: " + "\n" + dropBehavior + "\n";
        //TODO: add existence check here
    }

    public String statementToString() {
        return "DROP SCHEMA";
    }

    public int getDropBehavior() {
        return this.dropBehavior;
    }
    
    public String getSchemaName() {
        return this.schemaName;
    }
    
    public ExistenceCheck getExistenceCheck()
    {
        return existenceCheck;
    }
}