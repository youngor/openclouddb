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
import com.akiban.sql.types.AliasInfo;

/**
 * A DropAliasNode  represents a DROP ALIAS statement.
 *
 */

public class DropAliasNode extends DDLStatementNode
{
    private AliasInfo.Type aliasType;
    private ExistenceCheck existenceCheck;

    /**
     * Initializer for a DropAliasNode
     *
     * @param dropAliasName The name of the method alias being dropped
     * @param aliasType Alias type
     *
     * @exception StandardException
     */
    public void init(Object dropAliasName, Object aliasType, Object existenceCheck) throws StandardException {
        TableName dropItem = (TableName)dropAliasName;
        initAndCheck(dropItem);
        this.aliasType = (AliasInfo.Type)aliasType;
        this.existenceCheck = (ExistenceCheck)existenceCheck;
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        DropAliasNode other = (DropAliasNode)node;
        this.aliasType = other.aliasType;
        this.existenceCheck = other.existenceCheck;
    }

    public AliasInfo.Type getAliasType() { 
        return aliasType; 
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
            "existenceCheck: " + existenceCheck + "\n";
    }

    public String statementToString() {
        return "DROP " + aliasTypeName(aliasType);
    }

    /* returns the alias type name given the alias char type */
    private static String aliasTypeName(AliasInfo.Type type) {
        String typeName = null;
        switch (type) {
        case PROCEDURE:
            typeName = "PROCEDURE";
            break;
        case FUNCTION:
            typeName = "FUNCTION";
            break;
        case SYNONYM:
            typeName = "SYNONYM";
            break;
        case UDT:
            typeName = "TYPE";
            break;
        }
        return typeName;
    }

}