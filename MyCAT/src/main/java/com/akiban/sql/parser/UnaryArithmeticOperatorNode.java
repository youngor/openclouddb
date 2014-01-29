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
 * This node represents a unary arithmetic operator
 *
 */

public class UnaryArithmeticOperatorNode extends UnaryOperatorNode
{
    public static enum OperatorType {
        PLUS("+", "plus"), 
        MINUS("-", "minus"), 
        SQRT("SQRT", "sqrt"), 
        ABSOLUTE("ABS", "absolute");

        String operator, methodName;
        OperatorType(String operator, String methodName) {
            this.operator = operator;
            this.methodName = methodName;
        }
    }
    private OperatorType operatorType;
    
    /**
     * Initializer for a UnaryArithmeticOperatorNode
     *
     * @param operand The operand of the node
     */
    public void init(Object operand) throws StandardException {
        switch(getNodeType()) {
        case NodeTypes.UNARY_PLUS_OPERATOR_NODE:
            operatorType = OperatorType.PLUS;
            break;
        case NodeTypes.UNARY_MINUS_OPERATOR_NODE:
            operatorType = OperatorType.MINUS;
            break;
        case NodeTypes.SQRT_OPERATOR_NODE:
            operatorType = OperatorType.SQRT;
            break;
        case NodeTypes.ABSOLUTE_OPERATOR_NODE:
            operatorType = OperatorType.ABSOLUTE;
            break;
        default:
            assert false : "init for UnaryArithmeticOperator called with wrong nodeType = " + getNodeType();
            break;
        }
        init(operand, operatorType.operator, operatorType.methodName);
    }
        
    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        UnaryArithmeticOperatorNode other = (UnaryArithmeticOperatorNode)node;
        this.operatorType = other.operatorType;
    }

}