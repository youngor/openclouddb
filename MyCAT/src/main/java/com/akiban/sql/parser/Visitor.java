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
 * A visitor is an object that traverses the querytree
 * and performs some action. 
 *
 */
public interface Visitor
{
    /**
     * This is the default visit operation on a 
     * QueryTreeNode.    It just returns the node.  This
     * will typically suffice as the default visit 
     * operation for most visitors unless the visitor 
     * needs to count the number of nodes visited or 
     * something like that.
     * <p>
     * Visitors will overload this method by implementing
     * a version with a signature that matches a specific
     * type of node.    For example, if I want to do
     * something special with aggregate nodes, then
     * that Visitor will implement a 
     *              <I> visit(AggregateNode node)</I>
     * method which does the aggregate specific processing.
     *
     * @param node      the node to process
     *
     * @return a query tree node.    Often times this is
     * the same node that was passed in, but Visitors that
     * replace nodes with other nodes will use this to
     * return the new replacement node.
     *
     * @exception StandardException may be throw an error
     *      as needed by the visitor (i.e. may be a normal error
     *      if a particular node is found, e.g. if checking 
     *      a group by, we don't expect to find any ColumnReferences
     *      that aren't under an AggregateNode -- the easiest
     *      thing to do is just throw an error when we find the
     *      questionable node).
     */
    Visitable visit(Visitable node) throws StandardException;

    /**
     * Method that is called to see if {@code visit()} should be called on
     * the children of {@code node} before it is called on {@code node} itself.
     * If this method always returns {@code true}, the visitor will walk the
     * tree bottom-up. If it always returns {@code false}, the tree is visited
     * top-down.
     *
     * @param node the top node of a sub-tree about to be visited
     * @return {@code true} if {@code node}'s children should be visited
     * before {@code node}, {@code false} otherwise
     */
    boolean visitChildrenFirst(Visitable node);

    /**
     * Method that is called to see
     * if query tree traversal should be
     * stopped before visiting all nodes.
     * Useful for short circuiting traversal
     * if we already know we are done.
     *
     * @return true/false
     */
    boolean stopTraversal();

    /**
     * Method that is called to indicate whether
     * we should skip all nodes below this node
     * for traversal.    Useful if we want to effectively
     * ignore/prune all branches under a particular 
     * node.    
     * <p>
     * Differs from stopTraversal() in that it
     * only affects subtrees, rather than the
     * entire traversal.
     *
     * @param node the node to process
     * 
     * @return true/false
     */
    boolean skipChildren(Visitable node) throws StandardException;
}