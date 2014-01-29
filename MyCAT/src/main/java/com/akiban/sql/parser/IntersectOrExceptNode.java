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
 * A IntersectOrExceptNode represents an INTERSECT or EXCEPT DML statement.
 *
 */

public class IntersectOrExceptNode extends SetOperatorNode
{
    public static enum OpType { 
        INTERSECT("INTERSECT"), 
        EXCEPT("EXCEPT");

        String operatorName;
        OpType(String operatorName) {
            this.operatorName = operatorName;
        }
    }
    private OpType opType;

    /**
     * Initializer for an IntersectOrExceptNode.
     *
     * @param leftResult The ResultSetNode on the left side of this union
     * @param rightResult The ResultSetNode on the right side of this union
     * @param all Whether or not this is an ALL.
     * @param tableProperties Properties list associated with the table
     *
     * @exception StandardException Thrown on error
     */

    public void init(Object opType,
                     Object leftResult,
                     Object rightResult,
                     Object all,
                     Object tableProperties) 
            throws StandardException {
        super.init(leftResult, rightResult, all, tableProperties);
        this.opType = (OpType)opType;
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        IntersectOrExceptNode other = (IntersectOrExceptNode)node;
        this.opType = other.opType;
    }

    public OpType getOpType() {
        return opType;
    }
        
    public String getOperatorName() {
        return opType.operatorName;
    }

}