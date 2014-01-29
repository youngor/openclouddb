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
 * An ordered column has position.   It is an
 * abstract class for group by and order by
 * columns.
 *
 */
public abstract class OrderedColumn extends QueryTreeNode 
{
    protected static final int UNMATCHEDPOSITION = -1;
    protected int columnPosition = UNMATCHEDPOSITION;

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        OrderedColumn other = (OrderedColumn)node;
        this.columnPosition = other.columnPosition;
    }

    /**
     * Indicate whether this column is ascending or not.
     * By default assume that all ordered columns are
     * necessarily ascending.    If this class is inherited
     * by someone that can be desceneded, they are expected
     * to override this method.
     *
     * @return true
     */
    public boolean isAscending() {
        return true;
    }

    /**
     * Indicate whether this column should be ordered NULLS low.
     * By default we assume that all ordered columns are ordered
     * with NULLS higher than non-null values. If this class is inherited
     * by someone that can be specified to have NULLs ordered lower than
     * non-null values, they are expected to override this method.
     *
     * @return false
     */
    public boolean isNullsOrderedLow() {
        return false;
    }

    /**
     * Convert this object to a String.  See comments in QueryTreeNode.java
     * for how this should be done for tree printing.
     *
     * @return This object as a String
     */
    public String toString() {
        return "columnPosition: " + columnPosition + "\n" +
            super.toString();
    }

    /**
     * Get the position of this column
     *
     * @return The position of this column
     */
    public int getColumnPosition() {
        return columnPosition;
    }

    /**
     * Set the position of this column
     */
    public void setColumnPosition(int columnPosition) {
        this.columnPosition = columnPosition;
    }

}