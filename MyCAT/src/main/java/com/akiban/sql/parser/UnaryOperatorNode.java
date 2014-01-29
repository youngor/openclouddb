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
import com.akiban.sql.types.ValueClassName;

/**
 * A UnaryOperatorNode represents a built-in unary operator as defined by
 * the ANSI/ISO SQL standard.    This covers operators like +, -, NOT, and IS NULL.
 * Java operators are not represented here: the JSQL language allows Java
 * methods to be called from expressions, but not Java operators.
 *
 */

public class UnaryOperatorNode extends ValueNode
{
    protected String operator;
    protected String methodName;

    protected String resultInterfaceType;
    protected String receiverInterfaceType;

    /**
     * WARNING: operand may be NULL for COUNT(*).    
     */
    ValueNode operand;

    /**
     * Initializer for a UnaryOperatorNode.
     *
     * <ul>
     * @param operand The operand of the node
     * @param operator The name of the operator,
     * @param methodName Name of the method
     */

    public void init(Object operand,
                     Object operatorOrOpType,
                     Object methodNameOrAddedArgs) 
            throws StandardException {
        this.operand = (ValueNode)operand;
        this.operator = (String)operatorOrOpType;
        this.methodName = (String)methodNameOrAddedArgs;
    }

    /**
     * Initializer for a UnaryOperatorNode
     *
     * @param operand The operand of the node
     */
    public void init(Object operand) throws StandardException {
        this.operand = (ValueNode)operand;
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        UnaryOperatorNode other = (UnaryOperatorNode)node;
        this.operator = other.operator;
        this.methodName = other.methodName;
        this.resultInterfaceType = other.resultInterfaceType;
        this.receiverInterfaceType = other.receiverInterfaceType;
        this.operand = (ValueNode)getNodeFactory().copyNode(other.operand,
                                                            getParserContext());
    }

    /**
     * Set the operator.
     *
     * @param operator The operator.
     */
    void setOperator(String operator) {
        this.operator = operator;
    }

    /**
     * Get the operator of this unary operator.
     *
     * @return The operator of this unary operator.
     */
    public String getOperator() {
        return operator;
    }

    /**
     * Set the methodName.
     *
     * @param methodName The methodName.
     */
    void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
    }

    /**
     * Convert this object to a String.  See comments in QueryTreeNode.java
     * for how this should be done for tree printing.
     *
     * @return This object as a String
     */

    public String toString() {
        return "operator: " + operator + "\n" +
            "methodName: " + methodName + "\n" +
            super.toString();
    }

    /**
     * Prints the sub-nodes of this object.  See QueryTreeNode.java for
     * how tree printing is supposed to work.
     *
     * @param depth The depth of this node in the tree
     */

    public void printSubNodes(int depth) {
        super.printSubNodes(depth);

        if (operand != null) {
            printLabel(depth, "operand: ");
            operand.treePrint(depth + 1);
        }
    }

    /**
     * Get the operand of this unary operator.
     *
     * @return The operand of this unary operator.
     */
    public ValueNode getOperand() {
        return operand;
    }

    public void setOperand(ValueNode operand) {
        this.operand = operand;
    }

    /**
     * Accept the visitor for all visitable children of this node.
     * 
     * @param v the visitor
     *
     * @exception StandardException on error
     */
    void acceptChildren(Visitor v) throws StandardException {
        super.acceptChildren(v);

        if (operand != null) {
            operand = (ValueNode)operand.accept(v);
        }
    }
        
    /**
     * @throws StandardException 
     * {@inheritDoc}
     */
    protected boolean isEquivalent(ValueNode o) throws StandardException {
        if (isSameNodeType(o)) {
            // the first condition in the || covers the case when 
            // both operands are null.
            UnaryOperatorNode other = (UnaryOperatorNode)o;
            return (operator.equals(other.operator) && 
                    ((operand == other.operand)|| 
                     ((operand != null) && operand.isEquivalent(other.operand))));
        }
        return false;
    }

}