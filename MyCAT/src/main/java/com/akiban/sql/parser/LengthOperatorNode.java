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

import com.akiban.sql.types.TypeId;

import java.sql.Types;

/**
 * This node represents a unary XXX_length operator
 *
 */

public final class LengthOperatorNode extends UnaryOperatorNode
{
    private int parameterType;
    private int parameterWidth;

    public void setNodeType(int nodeType) {
        String operator = null;
        String methodName = null;

        if (nodeType == NodeTypes.CHAR_LENGTH_OPERATOR_NODE) {
            operator = "char_length";
            methodName = "charLength";
            parameterType = Types.VARCHAR;
            parameterWidth = TypeId.VARCHAR_MAXWIDTH;
        }
        else {
            assert false : "Unexpected nodeType = " + nodeType;
        }
        setOperator(operator);
        setMethodName(methodName);
        super.setNodeType(nodeType);
    }

}