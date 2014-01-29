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
 * A DMLStatement for a table modification: to wit, INSERT
 * UPDATE or DELETE.
 *
 */

public abstract class DMLModStatementNode extends DMLStatementNode
{
    protected FromVTI targetVTI;
    protected TableName targetTableName;
    protected ResultColumnList returningColumnList;
    private int statementType;

    /**
     * Initializer for a DMLModStatementNode -- delegate to DMLStatementNode
     *
     * @param resultSet A ResultSetNode for the result set of the
     *                                  DML statement
     */
    public void init(Object resultSet) {
        super.init(resultSet);
        statementType = getStatementType();
    }

    /**
     * Initializer for a DMLModStatementNode -- delegate to DMLStatementNode
     *
     * @param resultSet A ResultSetNode for the result set of the
     *                                  DML statement
     * @param statementType used by nodes that allocate a DMLMod directly
     *                                          (rather than inheriting it).
     */
    public void init(Object resultSet, Object statementType) {
        super.init(resultSet);
        this.statementType = ((Integer)statementType).intValue();
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        DMLModStatementNode other = (DMLModStatementNode)node;
        this.targetVTI = (FromVTI)getNodeFactory().copyNode(other.targetVTI,
                                                            getParserContext());
        this.targetTableName = (TableName)getNodeFactory().copyNode(other.targetTableName,
                                                                    getParserContext());
        this.statementType = other.statementType;
        
        this.returningColumnList = (ResultColumnList)getNodeFactory()
                .copyNode(other.returningColumnList, getParserContext());
    }

    void setTarget(QueryTreeNode targetName) {
        if (targetName instanceof TableName) {
            this.targetTableName = (TableName)targetName;
        }
        else {
            this.targetVTI = (FromVTI)targetName;
            targetVTI.setTarget();
        }
    }

    /**
     *
     * INSERT/UPDATE/DELETE are always atomic.
     *
     * @return true 
     */
    public boolean isAtomic() {
        return true;
    }

    public TableName getTargetTableName() {
        return targetTableName;
    }

    public ResultColumnList getReturningList() {
        return this.returningColumnList;
    }

    public void setReturningList(ResultColumnList returningColumnList) {
        this.returningColumnList = returningColumnList;
    }

    /**
     * Prints the sub-nodes of this object.  See QueryTreeNode.java for
     * how tree printing is supposed to work.
     *
     * @param depth The depth of this node in the tree
     */

    public void printSubNodes(int depth) {
        super.printSubNodes(depth);

        printLabel(depth, "targetTableName: ");
        targetTableName.treePrint(depth + 1);

        if (returningColumnList != null) {
            printLabel(depth, "returningList: ");
            returningColumnList.treePrint(depth+1);
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

        if (targetTableName != null) {
            targetTableName.accept(v);
        }
        if (returningColumnList != null) {
            returningColumnList.accept(v);
        }
    }
}