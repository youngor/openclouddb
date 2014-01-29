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
 * Find out if we have a particular node anywhere in the
 * tree.    Stop traversal as soon as we find one.
 * <p>
 * Can find any type of node -- the class or class name
 * of the target node is passed in as a constructor
 * parameter.
 *
 */
public class HasNodeVisitor implements Visitor
{
    protected boolean hasNode;
    private Class nodeClass;
    private Class skipOverClass;
    /**
     * Construct a visitor
     *
     * @param nodeClass the class of the node that 
     * we are looking for.
     */
    public HasNodeVisitor(Class nodeClass) {
        this.nodeClass = nodeClass;
    }

    /**
     * Construct a visitor
     *
     * @param nodeClass the class of the node that 
     * we are looking for.
     * @param skipOverClass do not go below this
     * node when searching for nodeClass.
     */
    public HasNodeVisitor(Class nodeClass, Class skipOverClass) {
        this.nodeClass = nodeClass;
        this.skipOverClass = skipOverClass;
    }

    ////////////////////////////////////////////////
    //
    // VISITOR INTERFACE
    //
    ////////////////////////////////////////////////

    /**
     * If we have found the target node, we are done.
     *
     * @param node the node to process
     *
     * @return me
     */
    public Visitable visit(Visitable node) {
        if (nodeClass.isInstance(node)) {
            hasNode = true;
        }
        return node;
    }

    /**
     * Stop traversal if we found the target node
     *
     * @return true/false
     */
    public boolean stopTraversal() {
        return hasNode;
    }

    /**
     * Don't visit childen under the skipOverClass
     * node, if it isn't null.
     *
     * @return true/false
     */
    public boolean skipChildren(Visitable node) {
        return (skipOverClass == null) ? false: skipOverClass.isInstance(node);
    }

    /**
     * Visit parent before children.
     */
    public boolean visitChildrenFirst(Visitable node) {
        return false;
    }

    ////////////////////////////////////////////////
    //
    // CLASS INTERFACE
    //
    ////////////////////////////////////////////////
    /**
     * Indicate whether we found the node in
     * question
     *
     * @return true/false
     */
    public boolean hasNode() {
        return hasNode;
    }

    /**
     * Reset the status so it can be run again.
     *
     */
    public void reset() {
        hasNode = false;
    }
}