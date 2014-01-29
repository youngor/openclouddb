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
 * FETCH rows from declared cursor.
 */

public class FetchStatementNode extends StatementNode
{
    private String name;
    private int count;

    /**
     * Initializer for an FetchStatementNode
     *
     * @param name The name of the cursor
     * @param count The number of rows to fetch
     */

    public void init(Object name,
                     Object count) {
        this.name = (String)name;
        this.count = (Integer)count;
    }
    
    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);
        
        FetchStatementNode other = (FetchStatementNode)node;
        this.name = other.name;
        this.count = other.count;
    }

    /**
     * Convert this object to a String.  See comments in QueryTreeNode.java
     * for how this should be done for tree printing.
     *
     * @return This object as a String
     */

    public String toString() {
        return "name: " + name + "\n" +
            "count: " + ((count < 0) ? "ALL" : Integer.toString(count)) + "\n" +
            super.toString();
    }

    public String statementToString() {
        return "FETCH";
    }

}