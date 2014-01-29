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
 * This node represents coalesce/value function which returns the first argument that is not null.
 * The arguments are evaluated in the order in which they are specified, and the result of the
 * function is the first argument that is not null. The result can be null only if all the arguments
 * can be null. The selected argument is converted, if necessary, to the attributes of the result.
 *
 */

public class CoalesceFunctionNode extends ValueNode
{
    private String functionName; //Are we here because of COALESCE function or VALUE function
    private ValueNodeList argumentsList; //this is the list of arguments to the function. We are interested in the first not-null argument

    /**
     * Initializer for a CalesceFunctionNode
     *
     * @param functionName Tells if the function was called with name COALESCE or with name VALUE
     * @param argumentsList The list of arguments to the coalesce/value function
     */
    public void init(Object functionName, Object argumentsList) {
        this.functionName = (String)functionName;
        this.argumentsList = (ValueNodeList)argumentsList;
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        CoalesceFunctionNode other = (CoalesceFunctionNode)node;
        this.functionName = other.functionName;
        this.argumentsList = (ValueNodeList)
            getNodeFactory().copyNode(other.argumentsList, getParserContext());
    }

    public String getFunctionName() {
        return functionName;
    }

    public ValueNodeList getArgumentsList() {
        return argumentsList;
    }

    /*
     * print the non-node subfields
     */
    public String toString() {
        return "functionName: " + functionName + "\n" +
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

        printLabel(depth, "argumentsList: ");
        argumentsList.treePrint(depth + 1);
    }

    /**
     * {@inheritDoc}
     */
    protected boolean isEquivalent(ValueNode o) throws StandardException {
        if (!isSameNodeType(o)) {
            return false;
        }

        CoalesceFunctionNode other = (CoalesceFunctionNode)o;

        if (!argumentsList.isEquivalent(other.argumentsList)) {
            return false;
        }

        return true;
    }

    /**
     * Accept the visitor for all visitable children of this node.
     *
     * @param v the visitor
     * @throws StandardException on error in the visitor
     */
    void acceptChildren(Visitor v) throws StandardException {
        super.acceptChildren(v);

        argumentsList = (ValueNodeList)argumentsList.accept(v);
    }

}