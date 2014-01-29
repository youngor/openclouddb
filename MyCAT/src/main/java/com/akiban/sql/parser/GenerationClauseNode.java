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
 * This node describes a Generation Clause in a column definition.
 *
 */
public class GenerationClauseNode extends ValueNode
{
    private ValueNode generationExpression;
    private String expressionText;

    public void init(Object generationExpression, Object expressionText) {
        this.generationExpression = (ValueNode)generationExpression;
        this.expressionText = (String)expressionText;
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        GenerationClauseNode other = (GenerationClauseNode)node;
        this.generationExpression = (ValueNode)
            getNodeFactory().copyNode(other.generationExpression, getParserContext());
        this.expressionText = other.expressionText;
    }

    /** Get the defining text of this generation clause */
    public String getExpressionText() { 
        return expressionText; 
    }

    protected boolean isEquivalent(ValueNode other) throws StandardException {
        if (!(other instanceof GenerationClauseNode)) { 
            return false; 
        }

        GenerationClauseNode that = (GenerationClauseNode)other;
        return this.generationExpression.isEquivalent(that.generationExpression);
    }
        
    public String toString() {
        return
            "expressionText: GENERATED ALWAYS AS ( " +
            expressionText + " )\n" +
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

        printLabel(depth, "generationExpression: ");
        generationExpression.treePrint(depth + 1);
    }

}