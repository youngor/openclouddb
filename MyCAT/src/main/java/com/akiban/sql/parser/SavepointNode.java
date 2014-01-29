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
 * A SavepointNode is the root of a QueryTree that represents a Savepoint (ROLLBACK savepoint, RELASE savepoint and SAVEPOINT)
 * statement.
 */

public class SavepointNode extends DDLStatementNode
{
    public static enum StatementType {
        SET, ROLLBACK, RELEASE
    }
    private StatementType statementType;
    private String savepointName; // Name of the savepoint.

    /**
     * Initializer for a SavepointNode
     *
     * @param objectName The name of the savepoint
     * @param savepointStatementType Type of savepoint statement ie rollback, release or set savepoint
     *
     * @exception StandardException Thrown on error
     */

    public void init(Object objectName,
                     Object statementType)
            throws StandardException {
        initAndCheck(null);
        this.savepointName = (String)objectName;
        this.statementType = (StatementType)statementType;
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        SavepointNode other = (SavepointNode)node;
        this.statementType = other.statementType;
        this.savepointName = other.savepointName;
    }

    /**
     * Convert this object to a String.  See comments in QueryTreeNode.java
     * for how this should be done for tree printing.
     *
     * @return This object as a String
     */

    public String toString() {
        String tempString = "savepointName: " + "\n" + savepointName + "\n";
        tempString = tempString + "savepointStatementType: " + "\n" + statementType + "\n";
        return super.toString() +    tempString;
    }

    public String statementToString() {
        switch (statementType) {
        case SET:
            return "SAVEPOINT";
        case ROLLBACK:
            return "ROLLBACK WORK TO SAVEPOINT";
        case RELEASE:
            return "RELEASE TO SAVEPOINT";
        default:
            assert false : "Unknown savepoint statement type";
            return "UNKNOWN";
        }
    }

}