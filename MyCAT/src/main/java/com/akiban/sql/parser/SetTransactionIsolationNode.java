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
 * A SetTransactionIsolationNode is the root of a QueryTree that represents a SET
 * TRANSACTION ISOLATION command
 *
 */

public class SetTransactionIsolationNode extends TransactionStatementNode
{
    private boolean current;
    private IsolationLevel isolationLevel;

    /**
     * Initializer for SetTransactionIsolationNode
     *
     * @param current Whether applies to current transaction or session default
     * @param isolationLevel The new isolation level
     */
    public void init(Object current,
                     Object isolationLevel) {
        this.current = (Boolean)current;
        this.isolationLevel = (IsolationLevel)isolationLevel;
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        SetTransactionIsolationNode other = (SetTransactionIsolationNode)node;
        this.current = other.current;
        this.isolationLevel = other.isolationLevel;
    }

    /**
     * Convert this object to a String.  See comments in QueryTreeNode.java
     * for how this should be done for tree printing.
     *
     * @return This object as a String
     */

    public String toString() {
        return "current: " + current + "\n" +
               "isolationLevel: " + isolationLevel + "\n" +
            super.toString();
    }

    public boolean isCurrent() {
        return current;
    }

    public IsolationLevel getIsolationLevel() {
        return isolationLevel;
    }

    public String statementToString() {
        if (current)
            return "SET TRANSACTION ISOLATION";
        else
            return "SET SESSION CHARACTERISTICS AS TRANSACTION ISOLATION";
    }

}