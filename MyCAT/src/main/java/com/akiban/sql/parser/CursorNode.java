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
 * A CursorNode represents a result set that can be returned to a client.
 * A cursor can be a named cursor created by the DECLARE CURSOR statement,
 * or it can be an unnamed cursor associated with a SELECT statement (more
 * precisely, a table expression that returns rows to the client).  In the
 * latter case, the cursor does not have a name.
 *
 */

public class CursorNode extends DMLStatementNode
{
    public static enum UpdateMode {
        UNSPECIFIED, READ_ONLY, UPDATE
    }

    private String name;
    private OrderByList orderByList;
    private ValueNode offset;           // <result offset clause> value
    private ValueNode fetchFirst; // <fetch first clause> value
    private String statementType;
    private UpdateMode updateMode;
    private IsolationLevel scanIsolationLevel = IsolationLevel.UNSPECIFIED_ISOLATION_LEVEL;

    /**
     ** There can only be a list of updatable columns when FOR UPDATE
     ** is specified as part of the cursor specification.
     */
    private List<String> updatableColumns;

    /**
     * Initializer for a CursorNode
     *
     * @param statementType Type of statement (SELECT, UPDATE, INSERT)
     * @param resultSet A ResultSetNode specifying the result set for
     *                                  the cursor
     * @param name The name of the cursor, null if no name
     * @param orderByList The order by list for the cursor, null if no
     *                                      order by list
     * @param offset The value of a <result offset clause> if present
     * @param fetchFirst The value of a <fetch first clause> if present
     * @param updateMode The user-specified update mode for the cursor,
     *                                   for example, CursorNode.READ_ONLY
     * @param updatableColumns The list of updatable columns specified by
     *                         the user in the FOR UPDATE clause, null if no
     *                         updatable columns specified.    May only be
     *                         provided if the updateMode parameter is
     *                         CursorNode.UPDATE.
     */

    public void init(Object statementType,
                     Object resultSet,
                     Object name,
                     Object orderByList,
                     Object offset,
                     Object fetchFirst,
                     Object updateMode,
                     Object updatableColumns) {
        init(resultSet);
        this.name = (String)name;
        this.statementType = (String)statementType;
        this.orderByList = (OrderByList)orderByList;
        this.offset = (ValueNode)offset;
        this.fetchFirst = (ValueNode)fetchFirst;
        this.updateMode = (UpdateMode)updateMode;
        this.updatableColumns = (List<String>)updatableColumns;
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        CursorNode other = (CursorNode)node;
        this.name = other.name;
        this.orderByList = (OrderByList)getNodeFactory().copyNode(other.orderByList,
                                                                  getParserContext());
        this.offset = (ValueNode)getNodeFactory().copyNode(other.offset,
                                                           getParserContext());
        this.fetchFirst = (ValueNode)getNodeFactory().copyNode(other.fetchFirst,
                                                               getParserContext());
        this.statementType = other.statementType;
        this.updateMode = other.updateMode;
        this.scanIsolationLevel = other.scanIsolationLevel;
        this.updatableColumns = other.updatableColumns;
    }

    public void setScanIsolationLevel(IsolationLevel isolationLevel) {
        this.scanIsolationLevel = isolationLevel;
    }

    /**
     * Convert this object to a String.  See comments in QueryTreeNode.java
     * for how this should be done for tree printing.
     *
     * @return This object as a String
     */

    public String toString() {
        return "name: " + name + "\n" +
            "updateMode: " + updateMode + "\n" +
            super.toString();
    }

    public String statementToString() {
        return statementType;
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
     * Accept the visitor for all visitable children of this node.
     * 
     * @param v the visitor
     *
     * @exception StandardException on error
     */
    void acceptChildren(Visitor v) throws StandardException {
        super.acceptChildren(v);

        if (orderByList != null) {
            orderByList = (OrderByList)orderByList.accept(v);
        }
        if (offset != null) {
            offset = (ValueNode)offset.accept(v);
        }
        if (fetchFirst != null) {
            fetchFirst = (ValueNode)fetchFirst.accept(v);
        }
    }

    public String getName() {
        return name;
    }

    public OrderByList getOrderByList() {
        return orderByList;
    }

    public ValueNode getOffsetClause() {
        return offset;
    }

    public ValueNode getFetchFirstClause() {
        return fetchFirst;
    }

    public UpdateMode getUpdateMode() {
        return updateMode;
    }

    public IsolationLevel getScanIsolationLevel() {
        return scanIsolationLevel;
    }

    /**
     * Return collection of names from the FOR UPDATE OF List
     *
     * @return List<String> of names from the FOR UPDATE OF list.
     */
    public List<String> getUpdatableColumns() {
        return updatableColumns;
    }

}