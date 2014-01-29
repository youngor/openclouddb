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
 * This class represents a set of privileges on one table.
 */
public class TablePrivilegesNode extends QueryTreeNode
{
    // Action types
    // TODO: Could be enum, but used as array index below.
    public static final int SELECT_ACTION = 0;
    public static final int DELETE_ACTION = 1;
    public static final int INSERT_ACTION = 2;
    public static final int UPDATE_ACTION = 3;
    public static final int REFERENCES_ACTION = 4;
    public static final int TRIGGER_ACTION = 5;
    public static final int ACTION_COUNT = 6;

    private boolean[] actionAllowed = new boolean[ACTION_COUNT];
    private ResultColumnList[] columnLists = new ResultColumnList[ACTION_COUNT];

    /**
     * Add all actions
     */
    public void addAll() {
        for (int i = 0; i < ACTION_COUNT; i++) {
            actionAllowed[i] = true;
            columnLists[i] = null;
        }
    }

    /**
     * Add one action to the privileges for this table
     *
     * @param action The action type
     * @param privilegeColumnList The set of privilege columns. Null for all columns
     *
     * @exception StandardException standard error policy.
     */
    public void addAction(int action, ResultColumnList privilegeColumnList) {
        actionAllowed[action] = true;
        if (privilegeColumnList == null)
            columnLists[action] = null;
        else if (columnLists[action] == null)
            columnLists[action] = privilegeColumnList;
        else
            columnLists[action].appendResultColumns(privilegeColumnList, false);
    }

}