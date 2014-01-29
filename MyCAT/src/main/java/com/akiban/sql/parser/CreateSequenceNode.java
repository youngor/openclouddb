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
import com.akiban.sql.types.DataTypeDescriptor;
import com.akiban.sql.types.TypeId;

/**
 * A CreateSequenceNode is the root of a QueryTree that
 * represents a CREATE SEQUENCE statement.
 */

public class CreateSequenceNode extends DDLStatementNode
{
    private TableName sequenceName;
    private DataTypeDescriptor dataType;
    private long initialValue;
    private long stepValue;
    private long maxValue;
    private long minValue;
    private boolean cycle;

    /**
     * Initializer for a CreateSequenceNode
     *
     * @param sequenceName The name of the new sequence
     * @param dataType Exact numeric type of the new sequence
     * @param initialValue Starting value
     * @param stepValue Increment amount
     * @param maxValue Largest value returned by the sequence generator
     * @param minValue Smallest value returned by the sequence generator
     * @param cycle True if the generator should wrap around, false otherwise
     *
     * @throws StandardException on error
     */
    public void init (Object sequenceName,
                      Object dataType,
                      Object initialValue,
                      Object stepValue,
                      Object maxValue,
                      Object minValue,
                      Object cycle) 
            throws StandardException {

        this.sequenceName = (TableName)sequenceName;
        initAndCheck(this.sequenceName);

        if (dataType != null) {
            this.dataType = (DataTypeDescriptor)dataType;
        } 
        else {
            this.dataType = DataTypeDescriptor.INTEGER;
        }

        this.stepValue = stepValue != null ? ((Long)stepValue).longValue() : 1;

        if (this.dataType.getTypeId().equals(TypeId.SMALLINT_ID)) {
            this.minValue = minValue != null ? ((Long)minValue).longValue() : Short.MIN_VALUE;
            this.maxValue = maxValue != null ? ((Long)maxValue).longValue() : Short.MAX_VALUE;
        } 
        else if (this.dataType.getTypeId().equals(TypeId.INTEGER_ID)) {
            this.minValue = minValue != null ? ((Long)minValue).longValue() : Integer.MIN_VALUE;
            this.maxValue = maxValue != null ? ((Long)maxValue).longValue() : Integer.MAX_VALUE;
        }
        else {
            this.minValue = minValue != null ? ((Long)minValue).longValue() : Long.MIN_VALUE;
            this.maxValue = maxValue != null ? ((Long)maxValue).longValue() : Long.MAX_VALUE;
        }

        if (initialValue != null) {
            this.initialValue = ((Long)initialValue).longValue();
        } 
        else {
            if (this.stepValue > 0) {
                this.initialValue = this.minValue;
            } 
            else {
                this.initialValue = this.maxValue;
            }
        }
        this.cycle = cycle != null ? ((Boolean)cycle).booleanValue() : Boolean.FALSE;

    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        CreateSequenceNode other = (CreateSequenceNode)node;
        this.sequenceName = (TableName)getNodeFactory().copyNode(other.sequenceName,
                                                                 getParserContext());
        this.dataType = other.dataType;
        this.initialValue = other.initialValue;
        this.stepValue = other.stepValue;
        this.maxValue = other.maxValue;
        this.minValue = other.minValue;
        this.cycle = other.cycle;
    }

    /**
     * Convert this object to a String.  See comments in QueryTreeNode.java
     * for how this should be done for tree printing.
     *
     * @return This object as a String
     */

    public String toString() {
        return super.toString() +
            "sequenceName: " + sequenceName + "\n" +
            "initial value: " + initialValue + "\n" +
            "step value: " + stepValue + "\n" +
            "maxValue: " + maxValue + "\n" +
            "minValue:" + minValue + "\n" +
            "cycle: " + cycle + "\n";
    }

    public String statementToString() {
        return "CREATE SEQUENCE";
    }

    public final long getInitialValue() {
        return initialValue;
    }

    public final long getStepValue() {
        return stepValue;
    }

    public final long getMaxValue() {
        return maxValue;
    }

    public final long getMinValue() {
        return minValue;
    }

    public final boolean isCycle() {
        return cycle;
    }

}