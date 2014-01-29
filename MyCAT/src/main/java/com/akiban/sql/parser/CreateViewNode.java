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
 * A CreateViewNode is the root of a QueryTree that represents a CREATE VIEW
 * statement.
 *
 */

public class CreateViewNode extends DDLStatementNode
{
    // TODO: Need the rest.
    public static final int NO_CHECK_OPTION = 0;

    private ResultColumnList resultColumns;
    private ResultSetNode queryExpression;
    private String qeText;
    private int checkOption;
    private OrderByList orderByList;
    private ValueNode offset;
    private ValueNode fetchFirst;
    private ExistenceCheck existenceCheck;

    /**
     * Initializer for a CreateViewNode
     *
     * @param newObjectName The name of the table to be created
     * @param resultColumns The column list from the view definition, 
     *              if specified
     * @param queryExpression The query expression for the view
     * @param checkOption The type of WITH CHECK OPTION that was specified
     *              (NONE for now)
     * @param qeText The text for the queryExpression
     * @param orderCols ORDER BY list
     * @param offset OFFSET if any, or null
     * @param fetchFirst FETCH FIRST if any, or null
     *
     * @exception StandardException Thrown on error
     */

    public void init(Object newObjectName,
                     Object resultColumns,
                     Object queryExpression,
                     Object checkOption,
                     Object qeText,
                     Object orderCols,
                     Object offset,
                     Object fetchFirst,
                     Object existenceCheck) 
            throws StandardException {
        initAndCheck(newObjectName);
        this.resultColumns = (ResultColumnList)resultColumns;
        this.queryExpression = (ResultSetNode)queryExpression;
        this.checkOption = ((Integer)checkOption).intValue();
        this.qeText = ((String)qeText).trim();
        this.orderByList = (OrderByList)orderCols;
        this.offset = (ValueNode)offset;
        this.fetchFirst = (ValueNode)fetchFirst;
        this.existenceCheck = (ExistenceCheck)existenceCheck;
        implicitCreateSchema = true;
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        CreateViewNode other = (CreateViewNode)node;
        this.resultColumns = (ResultColumnList)
            getNodeFactory().copyNode(other.resultColumns, getParserContext());
        this.queryExpression = (ResultSetNode)
            getNodeFactory().copyNode(other.queryExpression, getParserContext());
        this.qeText = other.qeText;
        this.checkOption = other.checkOption;
        this.orderByList = (OrderByList)
            getNodeFactory().copyNode(other.orderByList, getParserContext());
        this.offset = (ValueNode)
            getNodeFactory().copyNode(other.offset, getParserContext());
        this.fetchFirst = (ValueNode)
            getNodeFactory().copyNode(other.fetchFirst, getParserContext());
        this.existenceCheck = other.existenceCheck;
    }

    /**
     * Convert this object to a String.  See comments in QueryTreeNode.java
     * for how this should be done for tree printing.
     *
     * @return This object as a String
     */

    public String toString() {
        return super.toString() +
            "checkOption: " + checkOption + "\n" +
            "qeText: " + qeText + "\n"
          + "existenceCheck: " + existenceCheck + "\n";
    }

    public String statementToString() {
        return "CREATE VIEW";
    }

    /**
     * Prints the sub-nodes of this object.  See QueryTreeNode.java for
     * how tree printing is supposed to work.
     *
     * @param depth The depth of this node in the tree
     */

    public void printSubNodes(int depth) {
        super.printSubNodes(depth);

        if (resultColumns != null) {
            printLabel(depth, "resultColumns: ");
            resultColumns.treePrint(depth + 1);
        }

        printLabel(depth, "queryExpression: ");
        queryExpression.treePrint(depth + 1);

        if (orderByList != null) {
            printLabel(depth, "orderByList: ");
            orderByList.treePrint(depth + 1);
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

        if (queryExpression != null) {
            queryExpression = (ResultSetNode)queryExpression.accept(v);
        }
    }

    public int getCheckOption() { 
        return checkOption; 
    }

    public ResultColumnList getResultColumns() {
        return resultColumns;
    }

    public String getQueryExpression() {
        return qeText;
    }

    public ResultSetNode getParsedQueryExpression() { 
        return queryExpression; 
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
    
    public ExistenceCheck getExistenceCheck()
    {
        return existenceCheck;
    }
}