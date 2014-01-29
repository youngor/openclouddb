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
import com.akiban.sql.types.ValueClassName;

public abstract class BinaryLogicalOperatorNode extends BinaryOperatorNode
{
    protected boolean shortCircuitValue;

    /**
     * Initializer for a BinaryLogicalOperatorNode
     *
     * @param leftOperand The left operand of the comparison
     * @param rightOperand The right operand of the comparison
     * @param methodName The name of the method to call in the generated
     *              class.  In this case, it's actually an operator name.
     */

    public void init(Object leftOperand,
                     Object rightOperand,
                     Object methodName) {
        /* For logical operators, the operator and method names are the same */
        super.init(leftOperand, rightOperand, methodName, methodName,
                   ValueClassName.BooleanDataValue, ValueClassName.BooleanDataValue);
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        BinaryLogicalOperatorNode other = (BinaryLogicalOperatorNode)node;
        this.shortCircuitValue = other.shortCircuitValue;
    }

}