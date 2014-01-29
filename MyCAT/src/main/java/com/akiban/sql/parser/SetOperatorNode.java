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
 * A SetOperatorNode represents a UNION, INTERSECT, or EXCEPT in a DML statement. Binding and optimization
 * preprocessing is the same for all of these operations, so they share bind methods in this abstract class.
 *
 * The class contains a boolean telling whether the operation should eliminate
 * duplicate rows.
 *
 */

public abstract class SetOperatorNode extends TableOperatorNode
{
    /**
     ** Tells whether to eliminate duplicate rows.  all == TRUE means do
     ** not eliminate duplicates, all == FALSE means eliminate duplicates.
     */
    boolean all;

    OrderByList orderByList;
    ValueNode offset; // OFFSET n ROWS
    ValueNode fetchFirst; // FETCH FIRST n ROWS ONLY

    /**
     * Initializer for a SetOperatorNode.
     *
     * @param leftResult The ResultSetNode on the left side of this union
     * @param rightResult The ResultSetNode on the right side of this union
     * @param all Whether or not this is an ALL.
     * @param tableProperties Properties list associated with the table
     *
     * @exception StandardException Thrown on error
     */

    public void init(Object leftResult,
                     Object rightResult,
                     Object all,
                     Object tableProperties)
            throws StandardException {
        super.init(leftResult, rightResult, tableProperties);
        this.all = ((Boolean)all).booleanValue();

        /* resultColumns cannot be null, so we make a copy of the left RCL
         * for now.  At bind() time, we need to recopy the list because there
         * may have been a "*" in the list.  (We will set the names and
         * column types at that time, as expected.)
         */
        resultColumns = (ResultColumnList)
            getNodeFactory().copyNode(leftResultSet.getResultColumns(),
                                      getParserContext());
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        SetOperatorNode other = (SetOperatorNode)node;
        this.all = other.all;
        this.orderByList = (OrderByList)getNodeFactory().copyNode(other.orderByList,
                                                                  getParserContext());
        this.offset = (ValueNode)getNodeFactory().copyNode(other.offset,
                                                           getParserContext());
        this.fetchFirst = (ValueNode)getNodeFactory().copyNode(other.fetchFirst,
                                                               getParserContext());
    }

    /**
     * Convert this object to a String.  See comments in QueryTreeNode.java
     * for how this should be done for tree printing.
     *
     * @return This object as a String
     */

    public String toString() {
        return "all: " + all + "\n" +
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

        if (orderByList != null) {
            printLabel(depth, "orderByList:");
            orderByList.treePrint(depth + 1);
        }
    }

    public boolean isAll() {
        return all;
    }

    /**
     * @return the operator name: "UNION", "INTERSECT", or "EXCEPT"
     */
    abstract String getOperatorName();

}