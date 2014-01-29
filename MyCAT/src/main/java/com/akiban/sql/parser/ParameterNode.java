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
 * This node type represents a ? parameter.
 *
 */

public class ParameterNode extends ValueNode
{
    /*
    ** The parameter number for this parameter.  The numbers start at 0.
    */
    private int parameterNumber;

    /**
     * By default, we assume we are just a normal, harmless
     * little ole parameter.    But sometimes we may be a return
     * parameter (e.g. ? = CALL myMethod()).    
     */
    private ValueNode returnOutputParameter;

    /**
     * Initializer for a ParameterNode.
     *
     * @param parameterNumber The number of this parameter,
     *                                              (unique per query starting at 0)
     * @param defaultValue The default value for this parameter
     *
     */

    public void init(Object parameterNumber, Object defaultValue) {
        this.parameterNumber = ((Integer)parameterNumber).intValue();
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        ParameterNode other = (ParameterNode)node;
        this.parameterNumber = other.parameterNumber;
        this.returnOutputParameter = (ValueNode)
            getNodeFactory().copyNode(other.returnOutputParameter, getParserContext());
    }

    /**
     * Get the parameter number
     *
     * @return The parameter number
     */

    public int getParameterNumber() {
        return parameterNumber;
    }

    /**
     * Mark this as a return output parameter (e.g.
     * ? = CALL myMethod())
     */
    public void setReturnOutputParam(ValueNode valueNode) {
        returnOutputParameter = valueNode;
    }

    /**
     * Is this as a return output parameter (e.g.
     * ? = CALL myMethod())
     *
     * @return true if it is a return param
     */
    public boolean isReturnOutputParam() {
        return returnOutputParameter != null;
    }

    /**
     * @see ValueNode#isParameterNode
     */
    public boolean isParameterNode() {
        return true;
    }

    /**
     * @inheritDoc
     */
    protected boolean isEquivalent(ValueNode o) {
        return false;
    }

    /**
     * Convert this object to a String.  See comments in QueryTreeNode.java
     * for how this should be done for tree printing.
     *
     * @return This object as a String
     */

    public String toString() {
        return "number: " + parameterNumber + "\n" +
            super.toString();
    }

}