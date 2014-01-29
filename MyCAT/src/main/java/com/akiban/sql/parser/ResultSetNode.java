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
 * A ResultSetNode represents a result set, that is, a set of rows.  It is
 * analogous to a ResultSet in the LanguageModuleExternalInterface.  In fact,
 * code generation for a a ResultSetNode will create a "new" call to a
 * constructor for a ResultSet.
 *
 */

public abstract class ResultSetNode extends QueryTreeNode
{
    ResultColumnList resultColumns;
    boolean insertSource;

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        ResultSetNode other = (ResultSetNode)node;
        this.resultColumns = (ResultColumnList)getNodeFactory().copyNode(other.resultColumns,
                                                                         getParserContext());
        this.insertSource = other.insertSource;
    }

    /**
     * Convert this object to a String.  See comments in QueryTreeNode.java
     * for how this should be done for tree printing.
     *
     * @return This object as a String
     */

    public String toString() {
        return super.toString();
    }

    /**
     * Remember that this node is the source result set for an INSERT.
     */
    public void setInsertSource() {
        insertSource = true;
    }

    /**
     * Set the resultColumns in this ResultSetNode
     *
     * @param newRCL The new ResultColumnList for this ResultSetNode
     */
    public void setResultColumns(ResultColumnList newRCL) {
        resultColumns = newRCL;
    }

    /**
     * Get the resultColumns for this ResultSetNode
     *
     * @return ResultColumnList for this ResultSetNode
     */
    public ResultColumnList getResultColumns() {
        return resultColumns;
    }

    /**
     * Prints the sub-nodes of this object.  See QueryTreeNode.java for
     * how tree printing is supposed to work.
     *
     * @param depth The depth of this node in the tree
     */

    public void printSubNodes(int depth) {
        super.printSubNodes(depth);

        if (resultColumns != null) {
            printLabel(depth, "resultColumns: ");
            resultColumns.treePrint(depth + 1);
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

        if (resultColumns != null) {
            resultColumns = (ResultColumnList)resultColumns.accept(v);
        }
    }

}