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
 * EXECUTE a previously prepare statement.
 */

public class ExecuteStatementNode extends StatementNode
{
    private String name;
    private ValueNodeList parameterList;

    /**
     * Initializer for an ExecuteStatementNode
     *
     * @param name The name of the prepared statement.
     * @param parameterList Any parameter values to be bound.
     */

    public void init(Object name,
                     Object parameterList) {
        this.name = (String)name;
        this.parameterList = (ValueNodeList)parameterList;
    }

    public String getName() {
        return name;
    }

    public ValueNodeList getParameterList() {
        return parameterList;
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);
        
        ExecuteStatementNode other = (ExecuteStatementNode)node;
        this.name = other.name;
        this.parameterList = (ValueNodeList)
            getNodeFactory().copyNode(other.parameterList, getParserContext());
    }

    /**
     * Convert this object to a String.  See comments in QueryTreeNode.java
     * for how this should be done for tree printing.
     *
     * @return This object as a String
     */

    public String toString() {
        return "name: " + name + "\n" +
            super.toString();
    }

    public String statementToString() {
        return "EXECUTE";
    }

    /**
     * Prints the sub-nodes of this object.  See QueryTreeNode.java for
     * how tree printing is supposed to work.
     *
     * @param depth The depth of this node in the tree
     */

    public void printSubNodes(int depth) {
        super.printSubNodes(depth);

        printLabel(depth, "parameterList: ");
        parameterList.treePrint(depth + 1);
    }

    /**
     * Accept the visitor for all visitable children of this node.
     *
     * @param v the visitor
     * @throws StandardException on error in the visitor
     */
    void acceptChildren(Visitor v) throws StandardException {
        super.acceptChildren(v);

        parameterList = (ValueNodeList)parameterList.accept(v);
    }

}