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

import java.util.List;

/**
 * MySQL's index hint.
 */
public class IndexHintNode extends QueryTreeNode
{
    public static enum HintType {
        USE, IGNORE, FORCE
    }
    
    public static enum HintScope {
        JOIN, ORDER_BY, GROUP_BY
    }

    private HintType hintType;
    private HintScope hintScope;
    private List<String> indexes;

    public void init(Object hintType,
                     Object hintScope,
                     Object indexes)
    {
        this.hintType = (HintType)hintType;
        this.hintScope = (HintScope)hintScope;
        this.indexes = (List<String>)indexes;
    }

    public HintType getHintType() {
        return hintType;
    }
    public HintScope getHintScope() {
        return hintScope;
    }
    public List<String> getIndexes() {
        return indexes;
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        IndexHintNode other = (IndexHintNode)node;
        this.hintType = other.hintType;
        this.hintScope = other.hintScope;
        this.indexes = other.indexes;
    }
    
    /**
     * Convert this object to a String.  See comments in QueryTreeNode.java
     * for how this should be done for tree printing.
     *
     * @return This object as a String
     */
    public String toString() {
        return "hintType: " + hintType + "\n" +
            "hintScope: " + hintScope + "\n" +
            "indexes: " + indexes + "\n" +
            super.toString();
    }

}