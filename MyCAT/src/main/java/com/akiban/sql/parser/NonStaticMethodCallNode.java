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
 * A NonStaticMethodCallNode is really a node to represent a (static or non-static)
 * method call from an object (as opposed to a static method call from a class.
 */
public class NonStaticMethodCallNode extends MethodCallNode
{
    /*
    ** The receiver for a non-static method call is an object, represented
    ** by a ValueNode.
    */
    JavaValueNode receiver; 

    /**
     * Initializer for a NonStaticMethodCallNode
     *
     * @param methodName    The name of the method to call
     * @param receiver      A JavaValueNode representing the receiving object
     * @exception StandardException     Thrown on error
     */
    public void init(Object methodName, Object receiver) throws StandardException {
        super.init(methodName);

        /*
        ** If the receiver is a Java value that has been converted to a
        ** SQL value, get rid of the conversion and just use the Java value
        ** as-is.    If the receiver is a "normal" SQL value, then convert
        ** it to a Java value to use as the receiver.
        */
        if (receiver instanceof JavaToSQLValueNode) {
            this.receiver = ((JavaToSQLValueNode)receiver).getJavaValueNode();
        }
        else {
            this.receiver = (JavaValueNode)
                getNodeFactory().getNode(NodeTypes.SQL_TO_JAVA_VALUE_NODE,
                                         receiver,
                                         getParserContext());
        }
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        NonStaticMethodCallNode other = (NonStaticMethodCallNode)node;
        this.receiver = (JavaValueNode)getNodeFactory().copyNode(other.receiver,
                                                                 getParserContext());
    }

    /**
     * Prints the sub-nodes of this object.  See QueryTreeNode.java for
     * how tree printing is supposed to work.
     *
     * @param depth     The depth of this node in the tree
     */

    public void printSubNodes(int depth) {
        super.printSubNodes(depth);
        if (receiver != null) {
            printLabel(depth, "receiver :");
            receiver.treePrint(depth + 1);
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

        if (receiver != null) {
            receiver = (JavaValueNode)receiver.accept(v);
        }
    }
}