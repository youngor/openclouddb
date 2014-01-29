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
 * A SimpleCaseNode represents the CASE <expr> THEN ... form.
 */

public class SimpleCaseNode extends ValueNode
{
    private ValueNode operand;
    private ValueNodeList caseOperands, resultValues;
    private ValueNode elseValue;

    /**
     * Initializer for a SimpleCaseNode
     *
     * @param operand The expression being compared
     */

    public void init(Object operand) throws StandardException {
        this.operand = (ValueNode)operand;
        this.caseOperands = (ValueNodeList)getNodeFactory().getNode(NodeTypes.VALUE_NODE_LIST,
                                                                    getParserContext());
        this.resultValues = (ValueNodeList)getNodeFactory().getNode(NodeTypes.VALUE_NODE_LIST,
                                                                    getParserContext());
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        SimpleCaseNode other = (SimpleCaseNode)node;
        this.operand = (ValueNode)
            getNodeFactory().copyNode(other.operand, getParserContext());
        this.caseOperands = (ValueNodeList)
            getNodeFactory().copyNode(other.caseOperands, getParserContext());
        this.resultValues = (ValueNodeList)
            getNodeFactory().copyNode(other.resultValues, getParserContext());
        if (other.elseValue == null)
            this.elseValue = null;
        else
            this.elseValue = (ValueNode)
                getNodeFactory().copyNode(other.elseValue, getParserContext());
    }

    public ValueNode getOperand() {
        return operand;
    }

    public ValueNodeList getCaseOperands() {
        return caseOperands;
    }

    public ValueNodeList getResultValues() {
        return resultValues;
    }

    public ValueNode getElseValue() {
        return elseValue;
    }

    public void setElseValue(ValueNode elseValue) {
        this.elseValue = elseValue;
    }

    /** The number of <code>WHEN</code> cases. */
    public int getNumberOfCases() {
        return caseOperands.size();
    }

    /** The <code>WHEN</code> part. */
    public ValueNode getCaseOperand(int index) {
        return caseOperands.get(index);
    }

    /** The <code>THEN</code> part. */
    public ValueNode getResultValue(int index) {
        return resultValues.get(index);
    }

    public void addCase(ValueNode operand, ValueNode result) {
        caseOperands.add(operand);
        resultValues.add(result);
    }

    /**
     * Prints the sub-nodes of this object.  See QueryTreeNode.java for
     * how tree printing is supposed to work.
     *
     * @param depth The depth of this node in the tree
     */

    public void printSubNodes(int depth) {
        super.printSubNodes(depth);

        printLabel(depth, "operand: ");
        operand.treePrint(depth + 1);

        for (int i = 0; i < getNumberOfCases(); i++) {
            printLabel(depth, "when: ");
            getCaseOperand(i).treePrint(depth + 1);
            printLabel(depth, "then: ");
            getResultValue(i).treePrint(depth + 1);
        }

        if (elseValue != null) {
            printLabel(depth, "else: ");
            elseValue.treePrint(depth + 1);
        }
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

        operand = (ValueNode)operand.accept(v);
        caseOperands = (ValueNodeList)caseOperands.accept(v);
        resultValues = (ValueNodeList)resultValues.accept(v);
        if (elseValue != null)
            elseValue = (ValueNode)elseValue.accept(v);
    }
                
    /**
     * {@inheritDoc}
     */
    protected boolean isEquivalent(ValueNode o) throws StandardException {
        if (isSameNodeType(o)) {
            SimpleCaseNode other = (SimpleCaseNode)o;
            return operand.isEquivalent(other.operand) &&
                caseOperands.isEquivalent(other.caseOperands) &&
                resultValues.isEquivalent(other.resultValues) &&
                ((elseValue == null) ? (other.elseValue == null) :
                 elseValue.isEquivalent(other.elseValue));
        }
        return false;
    }

}