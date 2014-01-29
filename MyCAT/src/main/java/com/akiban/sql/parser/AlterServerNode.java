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

public class AlterServerNode extends MiscellaneousStatementNode {


    public enum AlterType {
        SET_SERVER_VARIABLE,
        INTERRUPT_SESSION,
        DISCONNECT_SESSION,
        KILL_SESSION,
        SHUTDOWN
    }

    private Integer sessionID = null;
    private AlterType alterSessionType;
    private SetConfigurationNode scn = null;
    private boolean shutdownImmediate;
    
    
    
    public void init(Object config) {
      
        if (config instanceof SetConfigurationNode) {
            scn = (SetConfigurationNode)config;
            alterSessionType = AlterType.SET_SERVER_VARIABLE;
        } else if (config instanceof Boolean) {
            alterSessionType = AlterType.SHUTDOWN;
            shutdownImmediate = ((Boolean)config).booleanValue();
        }
    }
    
    public void init (Object interrupt, Object disconnect, Object kill, Object session)
    {
        if (interrupt != null) {
            alterSessionType = AlterType.INTERRUPT_SESSION;
        } else if (disconnect != null) {
            alterSessionType = AlterType.DISCONNECT_SESSION;
        } else if (kill != null) {
            alterSessionType = AlterType.KILL_SESSION;
        }
        if (session instanceof ConstantNode) {
            sessionID = (Integer)((ConstantNode)session).getValue();
        }
    }
    
    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);
        AlterServerNode other = (AlterServerNode)node;
        this.sessionID = other.sessionID;
        this.alterSessionType = other.alterSessionType;
        this.scn = (SetConfigurationNode)getNodeFactory().copyNode(other.scn, getParserContext());
        this.shutdownImmediate = other.shutdownImmediate;
    }
    
    @Override
    public String statementToString() {
        return "ALTER SERVER";
    }

    @Override
    public String toString() {
        String ret = null;
        switch (alterSessionType) {
        case SET_SERVER_VARIABLE:
            ret = scn.toString();
            break;
        case SHUTDOWN:
            ret = "shutdown immediate: " + shutdownImmediate;
            break;
        case INTERRUPT_SESSION:
        case DISCONNECT_SESSION:
        case KILL_SESSION:
            ret = "sessionType: " + alterSessionType.name() + "\n" +  
                    "sessionID: " + sessionID;
            break;
        }
        ret = super.toString() + ret;
        return ret;
    }
    
    public final Integer getSessionID() {
        return sessionID;
    }

    public final AlterType getAlterSessionType() {
        return alterSessionType;
    }

    public final boolean isShutdownImmediate() {
        return shutdownImmediate;
    }

    public String getVariable() {
        return scn.getVariable();
    }

    public String getValue() {
        return scn.getValue();
    }
    
}