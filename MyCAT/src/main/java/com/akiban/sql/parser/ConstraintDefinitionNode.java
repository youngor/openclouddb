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

import java.util.Properties;

/**
 * A ConstraintDefinitionNode is a class for all nodes that can represent
 * constraint definitions.
 *
 */

public class ConstraintDefinitionNode extends TableElementNode
{
    public static enum ConstraintType {
        NOT_NULL, PRIMARY_KEY, UNIQUE, CHECK, DROP, FOREIGN_KEY, INDEX
    }

    protected TableName constraintName;
    protected ConstraintType constraintType;
    protected Properties properties;
    private ResultColumnList columnList;
    private String constraintText;
    private ValueNode checkCondition;
    private int behavior;                   // A StatementType.DROP_XXX
    private ConstraintType verifyType = ConstraintType.DROP; // By default do not check the constraint type

    public void init(Object constraintName,
                     Object constraintType,
                     Object rcl,
                     Object properties,
                     Object checkCondition,
                     Object constraintText,
                     Object behavior) {
        this.constraintName = (TableName)constraintName;

        /* We need to pass null as name to TableElementNode's constructor 
         * since constraintName may be null.
         */
        super.init(null);
        if (this.constraintName != null) {
            this.name = this.constraintName.getTableName();
        }
        this.constraintType = (ConstraintType)constraintType;
        this.properties = (Properties)properties;
        this.columnList = (ResultColumnList)rcl;
        this.checkCondition = (ValueNode)checkCondition;
        this.constraintText = (String)constraintText;
        this.behavior = ((Integer)behavior).intValue();
    }
    
    public void init(Object constraintName,
                     Object constraintType,
                     Object rcl,
                     Object properties,
                     Object checkCondition,
                     Object constraintText) {
        init(constraintName,
             constraintType,
             rcl,
             properties, 
             checkCondition,
             constraintText,
             StatementType.DROP_DEFAULT);
    }

    public void init(Object constraintName,
                     Object constraintType,
                     Object rcl,
                     Object properties,
                     Object checkCondition,
                     Object constraintText,
                     Object behavior,
                     Object verifyType) {
        init(constraintName, constraintType, rcl, properties, checkCondition, 
             constraintText, behavior);
        this.verifyType = (ConstraintType)verifyType;
    }
        
    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        ConstraintDefinitionNode other = (ConstraintDefinitionNode)node;
        this.constraintName = (TableName)
            getNodeFactory().copyNode(other.constraintName, getParserContext());
        this.constraintType = other.constraintType;
        this.properties = other.properties; // TODO: Clone?
        this.columnList = (ResultColumnList)
            getNodeFactory().copyNode(other.columnList, getParserContext());
        this.constraintText = other.constraintText;
        this.checkCondition = (ValueNode)
            getNodeFactory().copyNode(other.checkCondition, getParserContext());
        this.behavior = other.behavior;
        this.verifyType = other.verifyType;
    }

    /**
     * Get the constraint type
     *
     * @return constraintType The constraint type.
     */
    public ConstraintType getConstraintType() {
        return constraintType;
    }

    /**
     * Get the verify constraint type. Clarifies DROP actions.
     *
     * @return verify The verify constraint type.
     */
    public ConstraintType getVerifyType() {
        return verifyType;
    }

    /**
     * Get the column list
     *
     * @return columnList The column list.
     */
    public ResultColumnList getColumnList() {
        return columnList;
    }

    /**
     * Set the optional properties for the backing index to this constraint.
     *
     * @param properties The optional Properties for this constraint.
     */
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    /** 
     * Get the optional properties for the backing index to this constraint.
     *
     *
     * @return The optional properties for the backing index to this constraint
     */
    public Properties getProperties()
    {
        return properties;
    }


    /**
     * Convert this object to a String.  See comments in QueryTreeNode.java
     * for how this should be done for tree printing.
     *
     * @return This object as a String
     */

    public String toString() {
        return "constraintName: " + 
            ( ( constraintName != null) ?
              constraintName.toString() : "null" ) + "\n" +
            "constraintType: " + constraintType + "\n" +
            (constraintType == ConstraintType.DROP ? "verifyType: " + verifyType + "\n" : "") +
            "properties: " +
            ((properties != null) ? properties.toString() : "null") + "\n" +
            super.toString();
    }

    /**
     * Prints the sub-nodes of this object.  See QueryTreeNode.java for
     * how tree printing is supposed to work.
     * @param depth The depth to indent the sub-nodes
     */
    public void printSubNodes(int depth) {
        super.printSubNodes(depth);
        if (columnList != null) {
            printLabel(depth, "columnList: ");
            columnList.treePrint(depth + 1);
        }
    }

}