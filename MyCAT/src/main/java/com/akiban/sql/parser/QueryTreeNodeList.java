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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * QueryTreeNodeList is the root class for all lists of query tree nodes.
 * It provides a wrapper for java.util.List. All
 * lists of query tree nodes inherit from QueryTreeNodeList.
 *
 */

public abstract class QueryTreeNodeList<N extends QueryTreeNode> 
    extends QueryTreeNode implements Iterable<N>
{
    private List<N> list = new ArrayList<N>();

    public final int size() {
        return list.size();
    }

    protected List<N> getList()
    {
        return list;
    }

    public final boolean isEmpty() {
        return list.isEmpty();
    }

    public N get(int index) {
        return list.get(index);
    }

    public void add(N n) {
        list.add(n);
    }

    public final N remove(int index) {
        return list.remove(index);
    }

    public final void remove(N n) {
        list.remove(n);
    }

    public final int indexOf(N n) {
        return list.indexOf(n);
    }

    public final void set(int index, N n) {
        list.set(index, n);
    }

    public final void add(int index, N n) {
        list.add(index, n);
    }

    public final void addAll(QueryTreeNodeList<N> other) {
        list.addAll(other.list);
    }

    public final void clear() {
        list.clear();
    }

    public final Iterator<N> iterator() {
        return list.iterator();
    }

    public final void destructiveAddAll(QueryTreeNodeList<N> other) {
        addAll(other);
        other.clear();
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        QueryTreeNodeList<N> other = (QueryTreeNodeList<N>)node;
        for (N n : other.list)
            list.add((N)getNodeFactory().copyNode(n, getParserContext()));
    }

    /**
     * Prints the sub-nodes of this object.  See QueryTreeNode.java for
     * how tree printing is supposed to work.
     * @param depth     The depth to indent the sub-nodes
     */
    public void printSubNodes(int depth) {
        for (int index = 0; index < size(); index++) {
            debugPrint(formatNodeString("[" + index + "]:", depth));
            N elt = get(index);
            elt.treePrint(depth);
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

        int size = size();
        for (int index = 0; index < size; index++) {
            set(index, (N)get(index).accept(v));
        }
    }
}