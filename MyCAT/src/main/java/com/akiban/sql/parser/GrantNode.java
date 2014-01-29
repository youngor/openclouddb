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

import java.util.List;
import java.util.Iterator;

/**
 * This class represents a GRANT statement.
 */
public class GrantNode extends DDLStatementNode
{
    private PrivilegeNode privileges;
    private List<String> grantees;

    /**
     * Initialize a GrantNode.
     *
     * @param privileges PrivilegesNode
     * @param grantees List
     */
    public void init(Object privileges, Object grantees) {
        this.privileges = (PrivilegeNode)privileges;
        this.grantees = (List<String>)grantees;
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        GrantNode other = (GrantNode)node;
        this.privileges = (PrivilegeNode)getNodeFactory().copyNode(other.privileges,
                                                                   getParserContext());
        this.grantees = other.grantees; // TODO: Clone?
    }

    /**
     * Convert this object to a String.  See comments in QueryTreeNode.java
     * for how this should be done for tree printing.
     *
     * @return This object as a String
     */

    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (Iterator<String> it = grantees.iterator(); it.hasNext();) {
            if (sb.length() > 0)
                sb.append( ",");
            sb.append(it.next());
        }
        return super.toString() +
            privileges.toString() +
            "TO: \n" + sb.toString() + "\n";
    }

    public String statementToString() {
        return "GRANT";
    }

}