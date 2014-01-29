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
 * Represents aggregate function calls on a window
 */
public final class AggregateWindowFunctionNode extends WindowFunctionNode
{
    private AggregateNode aggregateFunction;

    /**
     * Initializer. QueryTreeNode override.
     *
     * @param arg1 The window definition or reference
     * @param arg2 aggregate function node
     *
     * @exception StandardException
     */
    public void init(Object arg1, Object arg2) throws StandardException {
        super.init(null, "?", arg1);
        aggregateFunction = (AggregateNode)arg2;
    }

    public AggregateNode getAggregateFunction() {
        return aggregateFunction;
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);
        
        AggregateWindowFunctionNode other = (AggregateWindowFunctionNode)node;
        aggregateFunction = (AggregateNode)getNodeFactory().copyNode(other.aggregateFunction,
                                                                     getParserContext());
    }

    /**
     * QueryTreeNode override. Prints the sub-nodes of this object.
     * @see QueryTreeNode#printSubNodes
     *
     * @param depth         The depth of this node in the tree
     */

    public void printSubNodes(int depth) {
        super.printSubNodes(depth);
        printLabel(depth, "aggregate: ");
        aggregateFunction.treePrint(depth + 1);
    }
}