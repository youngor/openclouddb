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

public class RowConstructorNode extends ValueNode
{
    private ValueNodeList list;
    private int depth; // max depth
    
    @Override
    public void init(Object list, Object count)
    {
        this.list = (ValueNodeList)list;
        depth = ((int[])count)[0];
    }

    /**
     * @inheritDoc
     */
    @Override
    protected boolean isEquivalent(ValueNode o) throws StandardException
    {
        if (!isSameNodeType(o))
        {
            return false;
        }
        
        RowConstructorNode other = (RowConstructorNode)o;
        return list.isEquivalent(other.list) && depth == other.depth;
    }

    @Override
    public void copyFrom(QueryTreeNode o) throws StandardException
    {
        super.copyFrom(o);
        RowConstructorNode other = (RowConstructorNode) o;
        list = (ValueNodeList)getNodeFactory().copyNode(other.list,
                                                        getParserContext());
        depth = other.depth;
    }

     /**
     * Accept the visitor for all visitable children of this node.
     * 
     * @param v the visitor
     *
     * @exception StandardException on error
     */
    @Override
    void acceptChildren(Visitor v) throws StandardException 
    {
        super.acceptChildren(v);

        if (list != null)
            list.accept(v);
    }
    
    @Override
    public String toString()
    {
        return list.toString() + "depth: " + depth + "\n";
    }

    public int getDepth()
    {
        return depth;
    }

    public ValueNodeList getNodeList()
    {
        return list;
    }
    
    public int listSize()
    {
        return list.size();
    }
}