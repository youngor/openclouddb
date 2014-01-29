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
 * A class that represents a value obtained from a Sequence using 'NEXT VALUE'
 */
public class NextSequenceNode extends ValueNode 
{
    private TableName sequenceName;

    /**
     * Initializer for a NextSequenceNode
     *
     * @param sequenceName The name of the sequence being called
     * @throws StandardException Thrown on error
     */
    public void init(Object sequenceName) throws StandardException {
        this.sequenceName = (TableName)sequenceName;
    }

    public TableName getSequenceName () {
        return sequenceName;
    }
    
    
    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        NextSequenceNode other = (NextSequenceNode)node;
        this.sequenceName = (TableName)getNodeFactory().copyNode(other.sequenceName,
                                                                 getParserContext());
    }

    /**
     * Convert this object to a String.  See comments in QueryTreeNode.java
     * for how this should be done for tree printing.
     *
     * @return This object as a String
     */

    public String toString() {
        return super.toString() +
         "Sequence: " + sequenceName;
    }

    protected boolean isEquivalent(ValueNode other) throws StandardException {
        return false;
    }

    
}