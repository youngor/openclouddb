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
import com.akiban.sql.types.DataTypeDescriptor;

/**
 * This node type converts a value in the SQL domain to a value in the Java
 * domain.
 */

public class SQLToJavaValueNode extends JavaValueNode
{
    ValueNode value;

    /**
     * Constructor for a SQLToJavaValueNode
     *
     * @param value A ValueNode representing a SQL value to convert to
     *                          the Java domain.
     */

    public void init(Object value) {
        this.value = (ValueNode)value;
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        SQLToJavaValueNode other = (SQLToJavaValueNode)node;
        this.value = (ValueNode)getNodeFactory().copyNode(other.value,
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
        if (value != null) {
            printLabel(depth, "value: ");
            value.treePrint(depth + 1);
        }
    }

    /**
     * Override behavior in superclass.
     */
    public DataTypeDescriptor getType() throws StandardException {
        return value.getType();
    }

    /**
     * Get the SQL ValueNode that is being converted to a JavaValueNode
     *
     * @return The underlying SQL ValueNode
     */
    public ValueNode getSQLValueNode() {
        return value;
    }

    public void setSQLValueNode(ValueNode value) {
        this.value = value;
    }

    /** @see ValueNode#getConstantValueAsObject 
     *
     * @exception StandardException Thrown on error
     */
    Object getConstantValueAsObject() throws StandardException {
        return value.getConstantValueAsObject();
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

        if (value != null) {
            value = (ValueNode)value.accept(v);
        }
    }
}