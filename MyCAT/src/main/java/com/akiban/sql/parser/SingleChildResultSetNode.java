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
 * A SingleChildResultSetNode represents a result set with a single child.
 *
 */

abstract class SingleChildResultSetNode extends FromTable
{
    /**
     * ResultSetNode under the SingleChildResultSetNode
     */
    ResultSetNode childResult;

    /**
     * Initialilzer for a SingleChildResultSetNode.
     *
     * @param childResult The child ResultSetNode
     * @param tableProperties Properties list associated with the table
     */

    public void init(Object childResult, Object tableProperties) {
        /* correlationName is always null */
        super.init(null, tableProperties);
        this.childResult = (ResultSetNode)childResult;
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        SingleChildResultSetNode other = (SingleChildResultSetNode)node;
        this.childResult = (ResultSetNode)getNodeFactory().copyNode(other.childResult,
                                                                    getParserContext());
    }

    /**
     * Return the childResult from this node.
     *
     * @return ResultSetNode The childResult from this node.
     */
    public ResultSetNode getChildResult() {
        return childResult;
    }

    /**
     * Set the childResult for this node.
     *
     * @param childResult The new childResult for this node.
     */
    void setChildResult(ResultSetNode childResult) {
        this.childResult = childResult;
    }

    /**
     * Prints the sub-nodes of this object.  See QueryTreeNode.java for
     * how tree printing is supposed to work.
     *
     * @param depth The depth of this node in the tree
     */

    public void printSubNodes(int depth) {
        super.printSubNodes(depth);

        if (childResult != null) {
            printLabel(depth, "childResult: ");
            childResult.treePrint(depth + 1);
        }
    }

    /**
     * Accept the visitor for all visitable children of this node.
     * 
     * @param v the visitor
     *
     * @exception StandardException on error
     */
    void acceptChildren(Visitor v) throws StandardException {
        super.acceptChildren(v);

        if (childResult != null) {
            childResult = (ResultSetNode)childResult.accept(v);
        }
    }

}