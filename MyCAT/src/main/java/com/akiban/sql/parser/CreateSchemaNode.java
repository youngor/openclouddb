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

import com.akiban.sql.types.CharacterTypeAttributes;

/**
 * A CreateSchemaNode is the root of a QueryTree that 
 * represents a CREATE SCHEMA statement.
 *
 */

public class CreateSchemaNode extends DDLStatementNode
{
    private String name;
    private String aid;
    private CharacterTypeAttributes defaultCharacterAttributes;
    private ExistenceCheck existenceCheck;

    /**
     * Initializer for a CreateSchemaNode
     *
     * @param schemaName The name of the new schema
     * @param aid The authorization id
     *
     * @exception StandardException Thrown on error
     */
    public void init(Object schemaName, 
                     Object aid,
                     Object defaultCharacterAttributes,
                     Object c
            )
            throws StandardException {
        /*
        ** DDLStatementNode expects tables, null out
        ** objectName explicitly to clarify that we
        ** can't hang with schema.object specifiers.
        */
        initAndCheck(null);

        this.name = (String)schemaName;
        this.aid = (String)aid;
        this.defaultCharacterAttributes = (CharacterTypeAttributes)defaultCharacterAttributes;
        this.existenceCheck = (ExistenceCheck)c;
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        CreateSchemaNode other = (CreateSchemaNode)node;
        this.name = other.name;
        this.aid = other.aid;
        this.defaultCharacterAttributes = other.defaultCharacterAttributes;
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
            "schemaName: " + "\n" + name + "\n" +
            "authorizationId: " + "\n" + aid + "\n" +
            "defaultChar: " + "\n" + defaultCharacterAttributes + "\n" 
            + "existenceCheck:\n" + existenceCheck + "\n"
                ;
    }

    public String statementToString() {
        return "CREATE SCHEMA";
    }

    public String getSchemaName() {
        return this.name;
    }
    
    public String getAuthorizationID() {
        return this.aid;
    }

    public CharacterTypeAttributes getDefaultCharacterAttributes() {
        return defaultCharacterAttributes;
    }
    
    public ExistenceCheck getExistenceCheck()
    {
        return existenceCheck;
    }
}