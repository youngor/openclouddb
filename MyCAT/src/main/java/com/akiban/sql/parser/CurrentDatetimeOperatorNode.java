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

import java.sql.Types;

/**
 * The CurrentDatetimeOperator operator is for the builtin CURRENT_DATE,
 * CURRENT_TIME, and CURRENT_TIMESTAMP operations.
 *
 */
public class CurrentDatetimeOperatorNode extends ValueNode 
{
    public static enum Field {
        DATE("CURRENT DATE", Types.DATE),
        TIME("CURRENT TIME", Types.TIME),
        TIMESTAMP("CURRENT TIMESTAMP", Types.TIMESTAMP);

        String methodName;
        int jdbcTypeId;

        Field(String methodName, int jdbcTypeId) {
            this.methodName = methodName;
            this.jdbcTypeId = jdbcTypeId;
        }
    }

    private Field field;

    public void init(Object field) {
        this.field = (Field)field;
    }

    public Field getField() {
        return field;
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        CurrentDatetimeOperatorNode other = (CurrentDatetimeOperatorNode)node;
        this.field = other.field;
    }

    public String toString() {
        return "methodName: " + field.methodName + "\n" +
            super.toString();
    }

    /**
     * {@inheritDoc}
     */
    protected boolean isEquivalent(ValueNode o) {
        if (isSameNodeType(o)) {
            CurrentDatetimeOperatorNode other = (CurrentDatetimeOperatorNode)o;
            return other.field == field;
        }
        return false;
    }
}