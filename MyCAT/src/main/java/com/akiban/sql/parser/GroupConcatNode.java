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

public class GroupConcatNode extends AggregateNode
{
    private String sep;
    private OrderByList orderCols;
    
    @Override
    public void init(Object value,
                     Object aggClass,
                     Object distinct,
                     Object aggName,
                     Object orderCols,
                     Object sep)
            throws StandardException
    {
        super.init(value,
                  aggClass,
                  distinct,
                  aggName);
        
        this.orderCols = (OrderByList) orderCols;
        this.sep = (String) sep;
    }
    
    @Override
    public void copyFrom(QueryTreeNode node) throws StandardException
    {
        super.copyFrom(node);
        
        GroupConcatNode other = (GroupConcatNode) node;
        this.sep = other.sep;
        this.orderCols = (OrderByList) getNodeFactory().copyNode(other.orderCols,
                                                   getParserContext());
    }
    
    @Override
    void acceptChildren(Visitor v) throws StandardException
    {
        super.acceptChildren(v);
        
        if (orderCols != null)
            orderCols.acceptChildren(v);
    }

     /**
     * @inheritDoc
     */
    @Override
    protected boolean isEquivalent(ValueNode o) throws StandardException
    {
        if (!isSameNodeType(o))
            return false;
        
        GroupConcatNode other = (GroupConcatNode) o;
        
        return  this.sep.equals(other.sep)
             && this.orderCols.equals(other.orderCols);
    }

    @Override
    public String toString()
    {
        return super.toString() + 
               "\nseparator: " + sep +
               "\norderyByList: "+ orderCols;
                
    }
    
    public String getSeparator()
    {
        return sep;
    }
    
    public OrderByList getOrderBy()
    {
        return orderCols;
    }
}