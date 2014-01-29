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
 * An CallStatementNode represents a CALL <procedure> statement.
 * It is the top node of the query tree for that statement.
 * A procedure call is very simple.
 * 
 * CALL [<schema>.]<procedure>(<args>)
 * 
 * <args> are either constants or parameter markers.
 * This implementation assumes that no subqueries or aggregates
 * can be in the argument list.
 * 
 * A procedure is always represented by a MethodCallNode.
 *
 */
public class CallStatementNode extends DMLStatementNode
{
    /**
     * The method call for the Java procedure. Guaranteed to be
     * a JavaToSQLValueNode wrapping a MethodCallNode by checks
     * in the parser.
     */
    private JavaToSQLValueNode methodCall;

    /**
     * Initializer for a CallStatementNode.
     *
     * @param methodCall The expression to "call"
     */

    public void init(Object methodCall) {
        super.init(null);
        this.methodCall = (JavaToSQLValueNode)methodCall;
        this.methodCall.getJavaValueNode().markForCallStatement();
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        CallStatementNode other = (CallStatementNode)node;
        this.methodCall = (JavaToSQLValueNode)
            getNodeFactory().copyNode(other.methodCall, getParserContext());
    }

    public String statementToString() {
        return "CALL";
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
    }

    public JavaToSQLValueNode methodCall() {
        return methodCall;
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
            methodCall = (JavaToSQLValueNode)methodCall.accept(v);
        }
    }

}