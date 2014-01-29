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
 * An Aggregate Node is a node that reprsents a set function/aggregate.
 * It used for all system aggregates as well as user defined aggregates.
 *
 */

public class AggregateNode extends UnaryOperatorNode
{
    private String aggregateName;
    private String aggregateDefinitionClassName;
    private boolean distinct;

    /**
     * Intializer.  Used for user defined and internally defined aggregates.
     * Called when binding a StaticMethodNode that we realize is an aggregate.
     *
     * @param operand   the value expression for the aggregate
     * @param uadClass  the class name for user aggregate definition for the aggregate
     *                  or internal aggregate type.
     * @param distinct  boolean indicating whether this is distinct
     *                  or not.
     * @param aggregateName the name of the aggregate from the user's perspective,
     *                      e.g. MAX
     *
     * @exception StandardException on error
     */
    public void init(Object operand,
                     Object uadClass,
                     Object distinct,
                     Object aggregateName) 
            throws StandardException {
        super.init(operand);
        this.aggregateDefinitionClassName = (String)uadClass;
        this.aggregateName = (String)aggregateName;
        this.distinct = ((Boolean)distinct).booleanValue();
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        AggregateNode other = (AggregateNode)node;
        this.aggregateDefinitionClassName = other.aggregateDefinitionClassName;
        this.aggregateName = other.aggregateName;
        this.distinct = other.distinct;
    }

    /**
     * Get the name of the aggregate.
     *
     * @return the aggregate name
     */
    public String getAggregateName() {
        return aggregateName;
    }

    /**
     * Indicate whether this aggregate is distinct or not.
     *
     * @return true/false
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * Convert this object to a String.  See comments in QueryTreeNode.java
     * for how this should be done for tree printing.
     *
     * @return This object as a String
     */

    public String toString() {
        return "aggregateName: " + aggregateName + "\n" +
            super.toString();
    }

}