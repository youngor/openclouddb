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
 * This class represents the 6 binary operators: LessThan, LessThanEquals,
 * Equals, NotEquals, GreaterThan and GreaterThanEquals.
 *
 */

public class BinaryRelationalOperatorNode extends BinaryComparisonOperatorNode 
{
    // TODO: Is there any point to this?

    public static final int EQUALS_RELOP = 1;
    public static final int NOT_EQUALS_RELOP = 2;
    public static final int GREATER_THAN_RELOP = 3;
    public static final int GREATER_EQUALS_RELOP = 4;
    public static final int LESS_THAN_RELOP = 5;
    public static final int LESS_EQUALS_RELOP = 6;
    public static final int IS_NULL_RELOP = 7;
    public static final int IS_NOT_NULL_RELOP = 8;

    private int operatorType;

    public void init(Object leftOperand, Object rightOperand) {
        String methodName = "";
        String operatorName = "";

        switch (getNodeType()) {
        case NodeTypes.BINARY_EQUALS_OPERATOR_NODE:
            methodName = "equals";
            operatorName = "=";
            operatorType = EQUALS_RELOP;
            break;

        case NodeTypes.BINARY_GREATER_EQUALS_OPERATOR_NODE:
            methodName = "greaterOrEquals";
            operatorName = ">=";
            operatorType = GREATER_EQUALS_RELOP;
            break;

        case NodeTypes.BINARY_GREATER_THAN_OPERATOR_NODE:
            methodName = "greaterThan";
            operatorName = ">";
            operatorType = GREATER_THAN_RELOP;
            break;

        case NodeTypes.BINARY_LESS_EQUALS_OPERATOR_NODE:
            methodName = "lessOrEquals";
            operatorName = "<=";
            operatorType =  LESS_EQUALS_RELOP;
            break;

        case NodeTypes.BINARY_LESS_THAN_OPERATOR_NODE:
            methodName = "lessThan";
            operatorName = "<";
            operatorType = LESS_THAN_RELOP;
            break;
        case NodeTypes.BINARY_NOT_EQUALS_OPERATOR_NODE:
            methodName = "notEquals";
            operatorName = "<>";
            operatorType = NOT_EQUALS_RELOP;
            break;

        default:
            assert false : "init for BinaryRelationalOperator called with wrong nodeType = " + getNodeType();
            break;
        }
        super.init(leftOperand, rightOperand, operatorName, methodName);
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        BinaryRelationalOperatorNode other = (BinaryRelationalOperatorNode)node;
        this.operatorType = other.operatorType;
    }

    public int getOperatorType() {
        return operatorType;
    }

}