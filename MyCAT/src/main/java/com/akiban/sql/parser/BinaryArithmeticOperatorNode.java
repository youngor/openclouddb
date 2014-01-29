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

import com.akiban.sql.types.ValueClassName;

/**
 * This node represents a binary arithmetic operator, like + or *.
 *
 */

public class BinaryArithmeticOperatorNode extends BinaryOperatorNode
{
    /**
     * Initializer for a BinaryArithmeticOperatorNode
     *
     * @param leftOperand The left operand
     * @param rightOperand  The right operand
     */

    public void init(Object leftOperand, Object rightOperand) {
        super.init(leftOperand, rightOperand,
                   ValueClassName.NumberDataValue, ValueClassName.NumberDataValue);
    }

    public void setNodeType(int nodeType) {
        String operator = null;
        String methodName = null;

        switch (nodeType) {
        case NodeTypes.BINARY_DIVIDE_OPERATOR_NODE:
            operator = "/";
            methodName = "divide";
            break;

        case NodeTypes.BINARY_MINUS_OPERATOR_NODE:
            operator = "-";
            methodName = "minus";
            break;

        case NodeTypes.BINARY_PLUS_OPERATOR_NODE:
            operator = "+";
            methodName = "plus";
            break;

        case NodeTypes.BINARY_TIMES_OPERATOR_NODE:
            operator = "*";
            methodName = "times";
            break;

        case NodeTypes.MOD_OPERATOR_NODE:
            operator = "mod";
            methodName = "mod";
            break;

        case NodeTypes.BINARY_DIV_OPERATOR_NODE:
            operator = "div";
            methodName = "div";
            break;
            
        default:
            assert false : "Unexpected nodeType:" + nodeType;
        }
        setOperator(operator);
        setMethodName(methodName);
        super.setNodeType(nodeType);
    }

}