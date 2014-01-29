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
 * A UnaryOperatorNode represents a built-in unary operator as defined by
 * the ANSI/ISO SQL standard.    This covers operators like +, -, NOT, and IS NULL.
 * Java operators are not represented here: the JSQL language allows Java
 * methods to be called from expressions, but not Java operators.
 *
 */

public class XMLUnaryOperatorNode extends UnaryOperatorNode
{
    public static enum OperatorType {
        PARSE("xmlparse", "XMLParse",
              ValueClassName.XMLDataValue,
              ValueClassName.StringDataValue),
        SERIALIZE("xmlserialize", "XMLSerialize",
                  ValueClassName.StringDataValue,
                  ValueClassName.XMLDataValue);
        
        String operator, methodName;
        String resultType, argType;
        OperatorType(String operator, String methodName,
                     String resultType, String argType) {
            this.operator = operator;
            this.methodName = methodName;
            this.resultType = resultType;
            this.argType = argType;
        }
    }

    private OperatorType operatorType;

    // Array to hold Objects that contain primitive
    // args required by the operator method call.
    private Object[] additionalArgs;

    /**
     * Initializer for a UnaryOperatorNode.
     *
     * <ul>
     * @param operand The operand of the node
     * @param operatorType The operatorType for this operator.
     * @param addedArgs An array of Objects
     *  from which primitive method parameters can be
     *  retrieved.
     */

    public void init(Object operand,
                     Object operatorType,
                     Object addedArgs) 
            throws StandardException {
        this.operand = (ValueNode)operand;
        this.operatorType = (OperatorType)operatorType;
        this.operator = this.operatorType.operator;
        this.methodName = this.operatorType.methodName;
        this.resultInterfaceType = this.operatorType.resultType;
        this.receiverInterfaceType = this.operatorType.argType;
        this.additionalArgs = (Object[])addedArgs;
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        XMLUnaryOperatorNode other = (XMLUnaryOperatorNode)node;
        this.operatorType = other.operatorType;
        this.additionalArgs = other.additionalArgs; // TODO: Clone?
    }

}