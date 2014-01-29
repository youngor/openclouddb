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
 * DefaultNode represents a column/parameter default.
 */
public  class DefaultNode extends ValueNode
{
    private String columnName;
    private String defaultText;
    private ValueNode defaultTree;

    /**
     * Initializer for a column/parameter default.
     *
     * @param defaultTree Query tree for default
     * @param defaultText The text of the default.
     */
    public void init(Object defaultTree,
                     Object defaultText) {
        this.defaultTree = (ValueNode)defaultTree;
        this.defaultText = (String)defaultText;
    }

    /**
     * Initializer for insert/update
     *
     */
    public void init(Object columnName) {
        this.columnName = (String)columnName;
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        DefaultNode other = (DefaultNode)node;
        this.columnName = other.columnName;
        this.defaultText = other.defaultText;
        this.defaultTree = (ValueNode)getNodeFactory().copyNode(other.defaultTree,
                                                                getParserContext());
    }

    /**
     * Get the text of the default.
     */
    public String getDefaultText() {
        return defaultText;
    }

    /**
     * Get the query tree for the default.
     *
     * @return The query tree for the default.
     */
    public ValueNode getDefaultTree() {
        return defaultTree;
    }

    /**
     * Convert this object to a String.  See comments in QueryTreeNode.java
     * for how this should be done for tree printing.
     *
     * @return This object as a String
     */

    public String toString() {
        return "columnName: " + columnName + "\n" +
            "defaultText: " + defaultText + "\n" +
            super.toString();
    }

    /**
     * Prints the sub-nodes of this object.  See QueryTreeNode.java for
     * how tree printing is supposed to work.
     *
     * @param depth The depth of this node in the tree
     */
    public void printSubNodes(int depth) {
        super.printSubNodes(depth);

        if (defaultTree != null) {
            printLabel(depth, "defaultTree:");
            defaultTree.treePrint(depth + 1);
        }
    }

    /**
     * @inheritDoc
     */
    protected boolean isEquivalent(ValueNode other) {
        return false;
    }
}