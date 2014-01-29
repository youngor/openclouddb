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
 * This node represents a unary extract operator, used to extract
 * a field from a date/time. The field value is returned as an integer.
 *
 */
public class ExtractOperatorNode extends UnaryOperatorNode 
{
    public static enum Field {
        YEAR("YEAR", "year"),
        MONTH("MONTH", "month"),
        DAY("DAY", "day"),
        HOUR("HOUR", "hour"),
        MINUTE("MINUTE", "minute"),
        SECOND("SECOND", "second");

        String fieldName, fieldMethod;

        Field(String fieldName, String fieldMethod) {
            this.fieldName = fieldName;
            this.fieldMethod = fieldMethod;
        }
    }

    private Field extractField;

    /**
     * Initializer for a ExtractOperatorNode
     *
     * @param field     The field to extract
     * @param operand The operand
     */
    public void init(Object field, Object operand) throws StandardException {
        extractField = (Field)field;
        super.init(operand,
                   "EXTRACT "+ extractField.fieldName,
                   extractField.fieldMethod);
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        ExtractOperatorNode other = (ExtractOperatorNode)node;
        this.extractField = other.extractField;
    }

    public String toString() {
        return "fieldName: " + extractField.fieldName + "\n" +
            super.toString();
    }

}