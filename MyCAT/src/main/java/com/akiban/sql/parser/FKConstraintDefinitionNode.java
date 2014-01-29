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
 * A FKConstraintDefintionNode represents table constraint definitions.
 *
 */

public class FKConstraintDefinitionNode extends ConstraintDefinitionNode
{
    TableName refTableName;
    ResultColumnList refRcl;
    int refActionDeleteRule;    // referential action on delete
    int refActionUpdateRule;    // referential action on update
    boolean grouping;

    // For ADD
    public void init(Object constraintName, 
                     Object refTableName, 
                     Object fkRcl,
                     Object refRcl,
                     Object refActions,
                     Object grouping) {
        super.init(constraintName,
                   ConstraintType.FOREIGN_KEY,
                   fkRcl, 
                   null,
                   null,
                   null);
        this.refRcl = (ResultColumnList)refRcl;
        this.refTableName = (TableName)refTableName;

        this.refActionDeleteRule = ((int[])refActions)[0];
        this.refActionUpdateRule = ((int[])refActions)[1];

        this.grouping = ((Boolean)grouping).booleanValue();
    }

    // For DROP
    public void init(Object constraintName,
                     Object constraintType,
                     Object behavior,
                     Object grouping) {
        super.init(constraintName,
                   constraintType,
                   null,
                   null,
                   null,
                   null,
                   behavior,
                   ConstraintType.FOREIGN_KEY);
        this.grouping = ((Boolean)grouping).booleanValue();
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        FKConstraintDefinitionNode other = (FKConstraintDefinitionNode)node;
        this.refTableName = (TableName)getNodeFactory().copyNode(other.refTableName,
                                                                 getParserContext());
        this.refRcl = (ResultColumnList)getNodeFactory().copyNode(other.refRcl,
                                                                  getParserContext());
        this.refActionDeleteRule = other.refActionDeleteRule;
        this.refActionUpdateRule = other.refActionUpdateRule;
    }

    public TableName getRefTableName() { 
        return refTableName; 
    }

    public ResultColumnList getRefResultColumnList() {
        return refRcl;
    }

    public boolean isGrouping() {
        return grouping;
    }
    
    public String toString() {
        return "refTable name : " + refTableName + "\n" +
            "grouping: " + grouping + "\n" + 
            super.toString();
    }
    

}