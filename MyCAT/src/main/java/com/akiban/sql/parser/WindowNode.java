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
 * Superclass of window definition and window reference.
 */
public abstract class WindowNode extends QueryTreeNode
{
    /**
     * The provided name of the window if explicitly defined in a window
     * clause. If the definition is inlined, currently the definition has
     * windowName "IN_LINE".    The standard 2003 sec. 4.14.9 calls for a
     * impl. defined one.
     */
    private String windowName;

    /**
     * Initializer
     *
     * @param arg1 The window name
     *
     * @exception StandardException
     */
    public void init(Object arg1) throws StandardException {
        windowName = (String)arg1;
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        WindowNode other = (WindowNode)node;
        this.windowName = other.windowName;
    }

    /**
     * @return the name of this window
     */
    public String getName() {
        return windowName;
    }

}