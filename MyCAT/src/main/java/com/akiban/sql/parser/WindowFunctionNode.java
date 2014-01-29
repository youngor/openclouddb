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
 * Superclass of any window function call.
 */
public abstract class WindowFunctionNode extends UnaryOperatorNode
{
    private WindowNode window;      // definition or reference

    /**
     * Initializer for a WindowFunctionNode
     * @param arg1 null (operand)
     * @param arg2 function mame (operator)
     * @param arg3 window node (definition or reference)
     * @exception StandardException
     */
    public void init(Object arg1, Object arg2, Object arg3) throws StandardException {
        super.init(arg1, arg2, null);
        this.window = (WindowNode)arg3;
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        WindowFunctionNode other = (WindowFunctionNode)node;
        this.window = (WindowNode)getNodeFactory().copyNode(other.window,
                                                            getParserContext());
    }

    /**
     * ValueNode override.
     * @see ValueNode#isConstantExpression
     */
    public boolean isConstantExpression() {
        return false;
    }

    /**
     * @return window associated with this window function
     */
    public WindowNode getWindow() {
        return window;
    }

    /**
     * Set window associated with this window function call.
     * @param wdn window definition
     */
    public void setWindow(WindowDefinitionNode wdn) {
        this.window = wdn;
    }

    /**
     * @return if name matches a defined window (in windows), return the
     * definition of that window, else null.
     */
    private WindowDefinitionNode definedWindow(WindowList windows, String name) {
        for (int i = 0; i < windows.size(); i++) {
            WindowDefinitionNode wdn = windows.get(i);
            if (wdn.getName().equals(name)) {
                return wdn;
            }
        }
        return null;
    }

    /**
     * QueryTreeNode override.
     * @see QueryTreeNode#printSubNodes
     */

    public void printSubNodes(int depth) {
        super.printSubNodes(depth);

        printLabel(depth, "window: ");
        window.treePrint(depth + 1);
    }

}