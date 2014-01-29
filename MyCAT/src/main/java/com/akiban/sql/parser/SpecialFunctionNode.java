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

import java.sql.Types;

/**
         SpecialFunctionNode handles system SQL functions.
         A function value is either obtained by a method
         call off the LanguageConnectionContext or Activation.
         LanguageConnectionContext functions are state related to the connection.
         Activation functions are those related to the statement execution.

         Each SQL function takes no arguments and returns a SQLvalue.
         <P>
         Functions supported:
         <UL>
         <LI> USER
         <LI> CURRENT_USER
         <LI> CURRENT_ROLE
         <LI> SESSION_USER
         <LI> SYSTEM_USER
         <LI> CURRENT SCHEMA
         <LI> CURRENT ISOLATION
         <LI> IDENTITY_VAL_LOCAL

         </UL>


        <P>

         This node is used rather than some use of MethodCallNode for
         runtime performance. MethodCallNode does not provide a fast access
         to the current language connection or activatation, since it is geared
         towards user defined routines.


*/
public class SpecialFunctionNode extends ValueNode 
{
    /**
       Name of SQL function
    */
    String sqlName;

    /*
      print the non-node subfields
    */
    public String toString() {
        return "sqlName: " + sqlName + "\n" +
            super.toString();
    }
                
    protected boolean isEquivalent(ValueNode o) {
        if (isSameNodeType(o)) {
            SpecialFunctionNode other = (SpecialFunctionNode)o;
            return sqlName.equals(other.sqlName);
        }
        return false;
    }

}