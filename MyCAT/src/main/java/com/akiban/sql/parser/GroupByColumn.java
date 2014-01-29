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
 * A GroupByColumn is a column in the GROUP BY clause.
 *
 */
public class GroupByColumn extends OrderedColumn 
{
    private ValueNode columnExpression;

    /**
     * Initializer.
     *
     * @param colRef The ColumnReference for the grouping column
     */
    public void init(Object colRef) {
        this.columnExpression = (ValueNode)colRef;
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        GroupByColumn other = (GroupByColumn)node;
        this.columnExpression = (ValueNode)getNodeFactory().copyNode(other.columnExpression,
                                                                     getParserContext());
    }

    /**
     * Prints the sub-nodes of this object.  See QueryTreeNode.java for
     * how tree printing is supposed to work.
     *
     * @param depth The depth of this node in the tree
     */

    public void printSubNodes(int depth) {
        super.printSubNodes(depth);

        if (columnExpression != null) {
            printLabel(depth, "columnExpression: ");
            columnExpression.treePrint(depth + 1);
        }
    }

    /**
     * Get the name of this column
     *
     * @return The name of this column
     */
    public String getColumnName() {
        return columnExpression.getColumnName();
    }

    public ValueNode getColumnExpression() {
        return columnExpression;
    }

    public void setColumnExpression(ValueNode cexpr) {
        this.columnExpression = cexpr;

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

        if (columnExpression != null) {
            columnExpression = (ValueNode)columnExpression.accept(v);
        }
    }

}