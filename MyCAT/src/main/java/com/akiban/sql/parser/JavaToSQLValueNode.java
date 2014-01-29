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
 * This node type converts a value from the Java domain to the SQL domain.
 */

public class JavaToSQLValueNode extends ValueNode
{
    JavaValueNode javaNode;

    /**
     * Initializer for a JavaToSQLValueNode
     *
     * @param value The Java value to convert to the SQL domain
     */
    public void init(Object value) {
        this.javaNode = (JavaValueNode)value;
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        JavaToSQLValueNode other = (JavaToSQLValueNode)node;
        this.javaNode = (JavaValueNode)getNodeFactory().copyNode(other.javaNode,
                                                                 getParserContext());
    }

    /**
     * Prints the sub-nodes of this object.  See QueryTreeNode for
     * how tree printing is supposed to work.
     *
     * @param depth The depth of this node in the tree
     */

    public void printSubNodes(int depth) {
        super.printSubNodes(depth);

        printLabel(depth, "javaNode: ");
        javaNode.treePrint(depth + 1);
    }

    /**
     * Get the JavaValueNode that lives under this JavaToSQLValueNode.
     *
     * @return The JavaValueNode that lives under this node.
     */

    public JavaValueNode getJavaValueNode() {
        return javaNode;
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

        if (javaNode != null) {
            javaNode = (JavaValueNode)javaNode.accept(v);
        }
    }
                
    /**
     * {@inheritDoc}
     */
    protected boolean isEquivalent(ValueNode o) {
        // anything in the java domain is not equiavlent.
        return false;
    }

}