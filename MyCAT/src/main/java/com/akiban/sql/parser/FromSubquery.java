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
 * A FromSubquery represents a subquery in the FROM list of a DML statement.
 *
 * The current implementation of this class is only
 * sufficient for Insert's need to push a new
 * select on top of the one the user specified,
 * to make the selected structure match that
 * of the insert target table.
 *
 */
public class FromSubquery extends FromTable
{
    ResultSetNode subquery;
    private OrderByList orderByList;
    private ValueNode offset;
    private ValueNode fetchFirst;

    /**
     * Intializer for a table in a FROM list.
     *
     * @param subquery The subquery
     * @param orderByList       ORDER BY list if any, or null
     * @param offset                OFFSET if any, or null
     * @param fetchFirst        FETCH FIRST if any, or null
     * @param correlationName The correlation name
     * @param derivedRCL The derived column list
     * @param tableProperties Properties list associated with the table
     */
    public void init(Object subquery,
                     Object orderByList,
                     Object offset,
                     Object fetchFirst,
                     Object correlationName,
                     Object derivedRCL,
                     Object tableProperties)
    {
        super.init(correlationName, tableProperties);
        this.subquery = (ResultSetNode)subquery;
        this.orderByList = (OrderByList)orderByList;
        this.offset = (ValueNode)offset;
        this.fetchFirst = (ValueNode)fetchFirst;
        resultColumns = (ResultColumnList)derivedRCL;
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        FromSubquery other = (FromSubquery)node;
        this.subquery = (ResultSetNode)getNodeFactory().copyNode(other.subquery,
                                                                 getParserContext());
        this.orderByList = (OrderByList)getNodeFactory().copyNode(other.orderByList,
                                                                  getParserContext());
        this.offset = (ValueNode)getNodeFactory().copyNode(other.offset,
                                                           getParserContext());
        this.fetchFirst = (ValueNode)getNodeFactory().copyNode(other.fetchFirst,
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

        if (subquery != null) {
            printLabel(depth, "subquery: ");
            subquery.treePrint(depth + 1);
        }

        if (orderByList != null) {
            printLabel(depth, "orderByList: ");
            orderByList.treePrint(depth + 1);
        }

        if (offset != null) {
            printLabel(depth, "offset: ");
            offset.treePrint(depth + 1);
        }

        if (fetchFirst != null) {
            printLabel(depth, "fetchFirst: ");
            fetchFirst.treePrint(depth + 1);
        }
    }

    /** 
     * Return the "subquery" from this node.
     *
     * @return ResultSetNode The "subquery" from this node.
     */
    public ResultSetNode getSubquery() {
        return subquery;
    }

    /**
     * Get the exposed name for this table, which is the name that can
     * be used to refer to it in the rest of the query.
     *
     * @return The exposed name for this table.
     */

    public String getExposedName() {
        return correlationName;
    }


    public OrderByList getOrderByList() {
        return orderByList;
    }

    public ValueNode getOffset() {
        return offset;
    }

    public ValueNode getFetchFirst() {
        return fetchFirst;
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

        if (subquery != null) {
            subquery = (ResultSetNode)subquery.accept(v);
        }
        if (orderByList != null) {
            orderByList = (OrderByList)orderByList.accept(v);
        }
    }

}