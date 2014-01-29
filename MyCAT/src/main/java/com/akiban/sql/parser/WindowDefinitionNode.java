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
 * This class represents an OLAP window definition.
 */
public final class WindowDefinitionNode extends WindowNode
{
    /**
     * True of the window definition was inlined.
     */
    private boolean inlined;

    /**
     * The partition by list if the window definition contains a <window partition
     * clause>, else null.
     */
    private PartitionByList partitionByList;

    /**
     * The order by list if the window definition contains a <window order
     * clause>, else null.
     */
    private OrderByList orderByList;

    /**
     * Initializer.
     *
     * @param arg1 The window name, null if in-lined definition
     * @param arg2 PARTITION BY list
     * @param arg3 ORDER BY list
     * @exception StandardException
     */
    public void init(Object arg1, Object arg2, Object arg3) throws StandardException {
        String name = (String)arg1;

        partitionByList = (PartitionByList)arg2;
        orderByList = (OrderByList)arg3;

        if (name != null) {
            super.init(arg1);
            inlined = false;
        } 
        else {
            super.init("IN-LINE");
            inlined = true;
        }
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        WindowDefinitionNode other = (WindowDefinitionNode)node;
        this.inlined = other.inlined;
        this.partitionByList = (PartitionByList)getNodeFactory().copyNode(other.partitionByList,
                                                                          getParserContext());
        this.orderByList = (OrderByList)getNodeFactory().copyNode(other.orderByList,
                                                                  getParserContext());
    }

    /**
     * java.lang.Object override.
     * @see QueryTreeNode#toString
     */
    public String toString() {
        return ("name: " + getName() + "\n" +
                "inlined: " + inlined + "\n" +
                "()\n");
    }

    /**
     * QueryTreeNode override. Prints the sub-nodes of this object.
     * @see QueryTreeNode#printSubNodes
     *
     * @param depth         The depth of this node in the tree
     */

    public void printSubNodes(int depth) {
        super.printSubNodes(depth);

        if (partitionByList != null) {
            printLabel(depth, "partitionByList: ");
            partitionByList.treePrint(depth + 1);
        }
        if (orderByList != null) {
            printLabel(depth, "orderByList: ");
            orderByList.treePrint(depth + 1);
        }
    }

    /**
     * Used to merge equivalent window definitions.
     *
     * @param wl list of window definitions
     * @return an existing window definition from wl, if 'this' is equivalent
     * to a window in wl.
     */
    public WindowDefinitionNode findEquivalentWindow(WindowList wl) {
        for (int i = 0; i < wl.size(); i++) {
            WindowDefinitionNode old = wl.get(i);
            if (isEquivalent(old)) {
                return old;
            }
        }
        return null;
    }

    /**
     * @return true if the window specifications are equal; no need to create
     * more than one window then.
     */
    private boolean isEquivalent(WindowDefinitionNode other) {
        if (orderByList == null && other.getOrderByList() == null &&
            partitionByList == null && other.getPartitionByList() == null) {
            return true;
        }

        assert false : "FIXME: ordering in windows not implemented yet";
        return false;
    }

    /**
     * @return whether this definition is inline
     */
    public boolean isInline() {
        return inlined;
    }

    /**
     * @return the order by list of this window definition if any, else null.
     */
    public OrderByList getOrderByList() {
        return orderByList;
    }

    /**
     * @return the partition by list of this window definition if any, else null.
     */
    public PartitionByList getPartitionByList() {
        return partitionByList;
    }

}