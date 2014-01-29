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
 * This node represents a set of privileges that are granted or revoked on one object.
 */
public class PrivilegeNode extends QueryTreeNode
{
    public static enum ObjectType {
        TABLE_PRIVILEGES, ROUTINE_PRIVILEGES, SEQUENCE_PRIVILEGES, UDT_PRIVILEGES
    }

    public static final String USAGE_PRIV = "USAGE";

    //
    // State initialized when the node is instantiated
    //
    private ObjectType objectType;
    private TableName objectName;
    private TablePrivilegesNode specificPrivileges; // Null for routine and usage privs
    private RoutineDesignator routineDesignator; // Null for table and usage privs

    private String privilege;    // E.g., USAGE_PRIV
    private boolean restrict;
        
    /**
     * Initialize a PrivilegeNode for use against SYS.SYSTABLEPERMS and SYS.SYSROUTINEPERMS.
     *
     * @param objectType (an Integer)
     * @param objectOfPrivilege (a TableName or RoutineDesignator)
     * @param specificPrivileges null for routines and usage
     */
    public void init(Object objectType, Object objectOfPrivilege, 
                     Object specificPrivileges)
            throws StandardException {
        this.objectType = (ObjectType)objectType;
        switch(this.objectType) {
        case TABLE_PRIVILEGES:
            objectName = (TableName)objectOfPrivilege;
            this.specificPrivileges = (TablePrivilegesNode)specificPrivileges;
            break;
                        
        case ROUTINE_PRIVILEGES:
            routineDesignator = (RoutineDesignator)objectOfPrivilege;
            objectName = routineDesignator.name;
            break;
                        
        default:
            assert false;
        }
    }

    /**
     * Initialize a PrivilegeNode for use against SYS.SYSPERMS.
     *
     * @param objectType E.g., SEQUENCE
     * @param objectName A possibles schema-qualified name
     * @param privilege A privilege, e.g. USAGE_PRIV
     * @param restrict True if this is a REVOKE...RESTRICT action
     */
    public void init(Object objectType, Object objectName, Object privilege, 
                     Object restrict) {
        this.objectType = (ObjectType)objectType;
        this.objectName = (TableName)objectName;
        this.privilege = (String)privilege;
        this.restrict = ((Boolean)restrict).booleanValue();
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        PrivilegeNode other = (PrivilegeNode)node;
        this.objectType = other.objectType;
        this.objectName = (TableName)getNodeFactory().copyNode(other.objectName,
                                                               getParserContext());
        this.specificPrivileges = (TablePrivilegesNode)getNodeFactory().copyNode(other.specificPrivileges,
                                                                                 getParserContext());
        if (other.routineDesignator != null)
            this.routineDesignator = 
                new RoutineDesignator(other.routineDesignator.isSpecific,
                                      (TableName)getNodeFactory().copyNode(other.routineDesignator.name,
                                                                           getParserContext()),
                                      other.routineDesignator.isFunction,
                                      other.routineDesignator.paramTypeList);
        this.privilege = other.privilege;
        this.restrict = other.restrict;
    }

}