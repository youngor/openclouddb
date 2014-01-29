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
 * A ValueNodeList represents a list of ValueNodes within a specific predicate 
 * (eg, IN list, NOT IN list or BETWEEN) in a DML statement.    
 * It extends QueryTreeNodeList.
 *
 */

public class ValueNodeList extends QueryTreeNodeList<ValueNode>
{
    /**
     * Add a ValueNode to the list.
     *
     * @param valueNode A ValueNode to add to the list
     *
     * @exception StandardException     Thrown on error
     */

    public void addValueNode(ValueNode valueNode) throws StandardException {
        add(valueNode);
    }

    /**
     * Check if all the elements in this list are equivalent to the elements
     * in another list. The two lists must have the same size, and the
     * equivalent nodes must appear in the same order in both lists, for the
     * two lists to be equivalent.
     *
     * @param other the other list
     * @return {@code true} if the two lists contain equivalent elements, or
     * {@code false} otherwise
     * @throws StandardException thrown on error
     * @see ValueNode#isEquivalent(ValueNode)
     */
    boolean isEquivalent(ValueNodeList other) throws StandardException {
        if (size() != other.size()) {
            return false;
        }

        for (int i = 0; i < size(); i++) {
            ValueNode vn1 = get(i);
            ValueNode vn2 = other.get(i);
            if (!vn1.isEquivalent(vn2)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Return whether or not this expression tree represents a constant expression.
     *
     * @return Whether or not this expression tree represents a constant expression.
     */
    public boolean isConstantExpression() {
        int size = size();

        for (int index = 0; index < size; index++) {
            boolean retcode;
            retcode = get(index).isConstantExpression();
            if (!retcode) {
                return retcode;
            }
        }

        return true;
    }

}