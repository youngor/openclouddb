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

/**
 * A BinaryOperatorNode represents a built-in binary operator as defined by
 * the ANSI/ISO SQL standard.    This covers operators like +, -, *, /, =, <, etc.
 * Java operators are not represented here: the JSQL language allows Java
 * methods to be called from expressions, but not Java operators.
 *
 */

public class XMLBinaryOperatorNode extends BinaryOperatorNode
{
    // Derby did the following, which just make things too messy:
    //   At the time of adding XML support, it was decided that
    //   we should avoid creating new OperatorNodes where possible.
    //   So for the XML-related binary operators we just add the
    //   necessary code to _this_ class, similar to what is done in
    //   TernarnyOperatorNode. Subsequent binary operators (whether
    //   XML-related or not) should follow this example when
    //   possible.

    public static enum OperatorType {
        EXISTS("xmlexists", "XMLExists",
               ValueClassName.BooleanDataValue,
               new String[] { ValueClassName.StringDataValue, ValueClassName.XMLDataValue }),
        QUERY("xmlquery", "XMLQuery", 
              ValueClassName.XMLDataValue,
              new String [] { ValueClassName.StringDataValue, ValueClassName.XMLDataValue });

        String operator, methodName;
        String resultType;
        String[] argTypes;
        OperatorType(String operator, String methodName,
                     String resultType, String[] argTypes) {
            this.operator = operator;
            this.methodName = methodName;
            this.resultType = resultType;
            this.argTypes = argTypes;
        }
    }

    public static enum PassByType {
        REF, VALUE
    }
    public static enum ReturnType {
        SEQUENCE, CONTENT
    }
    public static enum OnEmpty {
        EMPTY, NULL
    }

    /**
     * Initializer for a BinaryOperatorNode
     *
     * @param leftOperand The left operand of the node
     * @param rightOperand The right operand of the node
     * @param opType    An Integer holding the operatorType
     *  for this operator.
     */
    public void init(Object leftOperand,
                     Object rightOperand,
                     Object opType) {
        this.leftOperand = (ValueNode)leftOperand;
        this.rightOperand = (ValueNode)rightOperand;
        OperatorType operatorType = (OperatorType)opType;
        this.operator = operatorType.operator;
        this.methodName = operatorType.operator;
        this.leftInterfaceType = operatorType.argTypes[0];
        this.rightInterfaceType = operatorType.argTypes[1];
        this.resultInterfaceType = operatorType.resultType;
    }

}