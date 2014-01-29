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
 * A UnionNode represents a UNION in a DML statement.    It contains a boolean
 * telling whether the union operation should eliminate duplicate rows.
 *
 */

public class UnionNode extends SetOperatorNode
{
    /* Is this a UNION ALL generated for a table constructor -- a VALUES expression with multiple rows. */
    boolean tableConstructor;

    /* True if this is the top node of a table constructor */
    boolean topTableConstructor;

    /**
     * Initializer for a UnionNode.
     *
     * @param leftResult The ResultSetNode on the left side of this union
     * @param rightResult The ResultSetNode on the right side of this union
     * @param all Whether or not this is a UNION ALL.
     * @param tableConstructor Whether or not this is from a table constructor.
     * @param tableProperties Properties list associated with the table
     *
     * @exception StandardException Thrown on error
     */

    public void init(Object leftResult,
                     Object rightResult,
                     Object all,
                     Object tableConstructor,
                     Object tableProperties) throws StandardException {
        super.init(leftResult, rightResult, all, tableProperties);

        /* Is this a UNION ALL for a table constructor? */
        this.tableConstructor = ((Boolean)tableConstructor).booleanValue();
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        UnionNode other = (UnionNode)node;
        this.tableConstructor = other.tableConstructor;
        this.topTableConstructor = other.topTableConstructor;
    }

    /**
     * Mark this as the top node of a table constructor.
     */
    public void markTopTableConstructor() {
        topTableConstructor = true;
    }

    /**
     * Tell whether this is a UNION for a table constructor.
     */
    boolean tableConstructor() {
        return tableConstructor;
    }

    /**
     * Convert this object to a String.  See comments in QueryTreeNode.java
     * for how this should be done for tree printing.
     *
     * @return This object as a String
     */

    public String toString() {
        return "tableConstructor: " + tableConstructor + "\n" + super.toString();
    }

    String getOperatorName() {
        return "UNION";
    }

}