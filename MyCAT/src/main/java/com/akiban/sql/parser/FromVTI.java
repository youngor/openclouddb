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
 * A FromVTI represents a VTI in the FROM list of a DML statement.
 *
 */
public class FromVTI extends FromTable
{
    MethodCallNode methodCall;
    TableName exposedName;
    SubqueryList subqueryList;
    boolean isTarget;

    /**
     * @param invocation The constructor or static method for the VTI
     * @param correlationName The correlation name
     * @param derivedRCL The derived column list
     * @param tableProperties Properties list associated with the table
     *
     * @exception StandardException Thrown on error
     */
    public void init(Object invocation,
                     Object correlationName,
                     Object derivedRCL,
                     Object tableProperties)
            throws StandardException {
        init(invocation,
             correlationName,
             derivedRCL,
             tableProperties,
             makeTableName(null, (String)correlationName));
    }

    /**
     * @param invocation The constructor or static method for the VTI
     * @param correlationName The correlation name
     * @param derivedRCL The derived column list
     * @param tableProperties Properties list associated with the table
     * @param exposedTableName  The table name (TableName class)
     *
     * @exception StandardException Thrown on error
     */
    public void init(Object invocation,
                     Object correlationName,
                     Object derivedRCL,
                     Object tableProperties,
                     Object exposedTableName)
            throws StandardException {
        super.init(correlationName, tableProperties);

        this.methodCall = (MethodCallNode)invocation;

        resultColumns = (ResultColumnList)derivedRCL;
        subqueryList = (SubqueryList)getNodeFactory().getNode(NodeTypes.SUBQUERY_LIST,
                                                              getParserContext());

        /* Cache exposed name for this table.
         * The exposed name becomes the qualifier for each column
         * in the expanded list.
         */
        this.exposedName = (TableName)exposedTableName;
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        FromVTI other = (FromVTI)node;
        this.methodCall = (MethodCallNode)getNodeFactory().copyNode(other.methodCall,
                                                                    getParserContext());
        this.exposedName = (TableName)getNodeFactory().copyNode(other.exposedName,
                                                                getParserContext());
        this.subqueryList = (SubqueryList)getNodeFactory().copyNode(other.subqueryList,
                                                                    getParserContext());
        this.isTarget = other.isTarget;
    }

    /**
     * Convert this object to a String.  See comments in QueryTreeNode.java
     * for how this should be done for tree printing.
     *
     * @return This object as a String
     */

    public String toString() {
        return super.toString();
    }

    /**
     * Prints the sub-nodes of this object.  See QueryTreeNode.java for
     * how tree printing is supposed to work.
     *
     * @param depth The depth of this node in the tree
     */

    public void printSubNodes(int depth) {
        super.printSubNodes(depth);

        if (methodCall != null) {
            printLabel(depth, "methodCall: ");
            methodCall.treePrint(depth + 1);
        }

        if (exposedName != null) {
            printLabel(depth, "exposedName: ");
            exposedName.treePrint(depth + 1);
        }

        if (subqueryList != null) {
            printLabel(depth, "subqueryList: ");
            subqueryList.treePrint(depth + 1);
        }
    }

    /** 
     * Return true if this VTI is a constructor. Otherwise, it is a static method.
     */
    public boolean  isConstructor() {
        return (methodCall instanceof NewInvocationNode);
    }

    /** 
     * Return the constructor or static method invoked from this node
     */
    public MethodCallNode getMethodCall() {
        return methodCall;
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

    /**
     * @return the table name used for matching with column references.
     *
     */
    public TableName getExposedTableName() {
        return exposedName;
    }

    /**
     * Mark this VTI as the target of a delete or update.
     */
    void setTarget() {
        isTarget = true;
    }

    /**
     * Search to see if a query references the specifed table name.
     *
     * @param name Table name (String) to search for.
     * @param baseTable Whether or not name is for a base table
     *
     * @return true if found, else false
     *
     * @exception StandardException Thrown on error
     */
    public boolean referencesTarget(String name, boolean baseTable)
            throws StandardException {
        return (!baseTable) && name.equals(methodCall.getJavaClassName());
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

        if (methodCall != null) {
            methodCall = (MethodCallNode)methodCall.accept(v);
        }
    }

}