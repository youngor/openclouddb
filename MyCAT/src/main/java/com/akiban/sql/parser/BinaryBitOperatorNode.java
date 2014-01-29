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

public class BinaryBitOperatorNode extends BinaryOperatorNode
{
    /**
     * Initializer for a BinaryBitOperatorNode
     *
     * @param leftOperand The left operand
     * @param rightOperand  The right operand
     */

    public void init(Object operatorType, Object leftOperand, Object rightOperand) {
        super.init(leftOperand, rightOperand,
                   ValueClassName.NumberDataValue, ValueClassName.NumberDataValue);

        String operator = null;
        String methodName = null;

        switch ((OperatorType)operatorType) {
        case BITAND:
            operator = "&";
            methodName = "bitand";
            break;

        case BITOR:
            operator = "|";
            methodName = "bitor";
            break;

        case BITXOR:
            operator = "^";
            methodName = "bitxor";
            break;

        case LEFT_SHIFT:
            operator = "<<";
            methodName = "leftshift";
            break;

        case RIGHT_SHIFT:
            operator = ">>";
            methodName = "rightshift";
            break;

        default:
            assert false : "Unexpected operator:" + operatorType;
        }
        setOperator(operator);
        setMethodName(methodName);
    }

}