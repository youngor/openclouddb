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

import java.util.Properties;

/**
 * An OrderByList is an ordered list of columns in the ORDER BY clause.
 * That is, the order of columns in this list is significant - the
 * first column in the list is the most significant in the ordering,
 * and the last column in the list is the least significant.
 *
 */
public class OrderByList extends OrderedColumnList<OrderByColumn>
{
    private boolean allAscending = true;

    /**
       Add a column to the list

       @param column The column to add to the list
    */
    public void addOrderByColumn(OrderByColumn column) {
        add(column);

        if (!column.isAscending())
            allAscending = false;
    }

    /**
     * Are all columns in the list ascending.
     *
     * @return Whether or not all columns in the list ascending.
     */
    boolean allAscending() {
        return allAscending;
    }

    /**
       Get a column from the list

       @param position The column to get from the list
    */
    public OrderByColumn getOrderByColumn(int position) {
        return get(position);
    }

    public String toString() {
        return
            "allAscending: " + allAscending + "\n" +
            super.toString();
    }

}