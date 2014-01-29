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
 * An InListOperatorNode represents an IN list.
 *
 */
public final class InListOperatorNode extends ValueNode
{
    protected RowConstructorNode leftOperand;
    protected RowConstructorNode rightOperandList;
    protected boolean negated;
    
    /**
     * Initializer for a InListOperatorNode
     *
     * @param leftOperand The left operand of the node
     * @param rightOperandList The right operand list of the node
     */
    @Override
    public void init(Object leftOperand, Object rightOperandList) throws StandardException
    {
        if (leftOperand instanceof RowConstructorNode)
            this.leftOperand = (RowConstructorNode) leftOperand;
        else
        {
            // if left operand is not a RowConstructorNode
            // but soemthing else, wrap it in a one-element RowConstructorNode (1 column)
            ValueNodeList list = (ValueNodeList)getNodeFactory().getNode(NodeTypes.VALUE_NODE_LIST,
                                                                 getParserContext());
            list.addValueNode((ValueNode)leftOperand);
            
            this.leftOperand = (RowConstructorNode)
                                        getNodeFactory().getNode(NodeTypes.ROW_CTOR_NODE,
                                                                 list,
                                                                 new int[]{0},
                                                                 getParserContext());
            
        }
        this.rightOperandList = (RowConstructorNode) rightOperandList;
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    @Override
    public void copyFrom(QueryTreeNode node) throws StandardException
    {
        super.copyFrom(node);

        InListOperatorNode other = (InListOperatorNode) node;
        this.leftOperand = (RowConstructorNode) getNodeFactory().copyNode(other.leftOperand, getParserContext());
        this.rightOperandList = (RowConstructorNode) getNodeFactory().copyNode(other.rightOperandList, getParserContext());
    }

    /**
     * Convert this object to a String.  See comments in QueryTreeNode.java
     * for how this should be done for tree printing.
     *
     * @return This object as a String
     */
    @Override
    public String toString()
    {
        return "operator: " + (negated ? "NOT " : "") + "IN\n"
               + "methodName: in\n"
               + super.toString();
    }

    /**
     * Prints the sub-nodes of this object.  See QueryTreeNode.java for
     * how tree printing is supposed to work.
     *
     * @param depth The depth of this node in the tree
     */
    public void printSubNodes(int depth)
    {
        super.printSubNodes(depth);

        if (leftOperand != null)
        {
            printLabel(depth, "leftOperand: ");
            leftOperand.treePrint(depth + 1);
        }

        if (rightOperandList != null)
        {
            printLabel(depth, "rightOperandList: ");
            rightOperandList.treePrint(depth + 1);
        }
    }

    /**
     * Set the leftOperand to the specified ValueNode
     *
     * @param newLeftOperand The new leftOperand
     */
    public void setLeftOperand(RowConstructorNode newLeftOperand)
    {
        leftOperand = newLeftOperand;
    }

    /**
     * Get the leftOperand
     *
     * @return The current leftOperand.
     */
    public RowConstructorNode getLeftOperand()
    {
        return leftOperand;
    }

    /**
     * Set the rightOperandList to the specified ValueNodeList
     *
     * @param newRightOperandList The new rightOperandList
     *
     */
    public void setRightOperandList(RowConstructorNode newRightOperandList)
    {
        rightOperandList = newRightOperandList;
    }

    /**
     * Get the rightOperandList
     *
     * @return The current rightOperandList.
     */
    public RowConstructorNode getRightOperandList()
    {
        return rightOperandList;
    }

    /**
     * Return whether or not this expression tree represents a constant expression.
     *
     * @return Whether or not this expression tree represents a constant expression.
     */
    @Override
    public boolean isConstantExpression()
    {
        return (leftOperand.isConstantExpression()
                && rightOperandList.isConstantExpression());
    }

    /**
     * Accept the visitor for all visitable children of this node.
     * 
     * @param v the visitor
     *
     * @exception StandardException on error
     */
    void acceptChildren(Visitor v) throws StandardException
    {
        super.acceptChildren(v);

        if (leftOperand != null)
        {
            leftOperand = (RowConstructorNode) leftOperand.accept(v);
        }

        if (rightOperandList != null)
        {
            rightOperandList = (RowConstructorNode) rightOperandList.accept(v);
        }
    }

    /**
     * @inheritDoc
     */
    protected boolean isEquivalent(ValueNode o) throws StandardException
    {
        if (!isSameNodeType(o))
        {
            return false;
        }

        InListOperatorNode other = (InListOperatorNode) o;
        if (!leftOperand.isEquivalent(other.getLeftOperand())
                || !rightOperandList.isEquivalent(other.rightOperandList))
            return false;

        return true;
    }

    public boolean isNegated() {
        return negated;
    }

    public void setNegated(boolean negated) {
        this.negated = negated;
    }


}