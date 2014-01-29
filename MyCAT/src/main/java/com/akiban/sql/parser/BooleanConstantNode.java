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
import com.akiban.sql.types.TypeId;

import java.sql.Types;

public class BooleanConstantNode extends ConstantNode
{
    private boolean booleanValue;
    private boolean unknownValue;

    /**
     * Initializer for a BooleanConstantNode.
     *
     * @param arg1 A boolean containing the value of the constant OR The TypeId for the type of the node
     *
     * @exception StandardException
     */
    public void init(Object arg1) throws StandardException {
        if (arg1 == null) {
            /* Fill in the type information in the parent ValueNode */
            super.init(TypeId.BOOLEAN_ID,
                       Boolean.TRUE,
                       4);

            setValue(null);
        }
        else if (arg1 instanceof Boolean) {
            booleanValue = ((Boolean)arg1).booleanValue();

            /* Fill in the type information in the parent ValueNode */
            super.init(TypeId.BOOLEAN_ID,
                       Boolean.FALSE,
                       booleanValue ? 4 : 5);

            super.setValue(arg1);
        }
        else {
            super.init(arg1,
                       Boolean.TRUE,
                       TypeId.BOOLEAN_MAXWIDTH);
            unknownValue = true;
        }
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        BooleanConstantNode other = (BooleanConstantNode)node;
        this.booleanValue = other.booleanValue;
        this.unknownValue = other.unknownValue;
    }

    public boolean getBooleanValue() {
        return booleanValue;
    }

    public void setBooleanValue(boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    /**
     * Return an Object representing the bind time value of this
     * expression tree.  If the expression tree does not evaluate to
     * a constant at bind time then we return null.
     * This is useful for bind time resolution of VTIs.
     * RESOLVE: What do we do for primitives?
     *
     * @return An Object representing the bind time value of this expression tree.
     *               (null if not a bind time constant.)
     *
     */
    Object getConstantValueAsObject() {
        return booleanValue ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * Return the value as a string.
     *
     * @return The value as a string.
     *
     */
    String getValueAsString() {
        if (booleanValue) {
            return "true";
        }
        else {
            return "false";
        }
    }

    /**
     * Does this represent a true constant.
     *
     * @return Whether or not this node represents a true constant.
     */
    public boolean isBooleanTrue() {
        return (booleanValue && !unknownValue);
    }

    /**
     * Does this represent a false constant.
     *
     * @return Whether or not this node represents a false constant.
     */
    public boolean isBooleanFalse() {
        return (!booleanValue && !unknownValue);
    }

}