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
 * A StaticMethodCallNode represents a static method call from a Class
 * (as opposed to from an Object).

     For a procedure the call requires that the arguments be ? parameters.
     The parameter is *logically* passed into the method call a number of different ways.

     <P>
     For a application call like CALL MYPROC(?) the logically Java method call is
     (in psuedo Java/SQL code) (examples with CHAR(10) parameter)
     <BR>
     Fixed length IN parameters - com.acme.MyProcedureMethod(?)
     <BR>
     Variable length IN parameters - com.acme.MyProcedureMethod(CAST (? AS CHAR(10))
     <BR>
     Fixed length INOUT parameter -
        String[] holder = new String[] {?}; com.acme.MyProcedureMethod(holder); ? = holder[0]
     <BR>
     Variable length INOUT parameter -
        String[] holder = new String[] {CAST (? AS CHAR(10)}; com.acme.MyProcedureMethod(holder); ? = CAST (holder[0] AS CHAR(10))

     <BR>
     Fixed length OUT parameter -
        String[] holder = new String[1]; com.acme.MyProcedureMethod(holder); ? = holder[0]

     <BR>
     Variable length INOUT parameter -
        String[] holder = new String[1]; com.acme.MyProcedureMethod(holder); ? = CAST (holder[0] AS CHAR(10))


        <P>
    For static method calls there is no pre-definition of an IN or INOUT parameter, so a call to CallableStatement.registerOutParameter()
    makes the parameter an INOUT parameter, provided:
        - the parameter is passed directly to the method call (no casts or expressions).
        - the method's parameter type is a Java array type.

        Since this is a dynmaic decision we compile in code to take both paths, based upon a boolean isINOUT which is dervied from the
    ParameterValueSet. Code is logically (only single parameter String[] shown here). Note, no casts can exist here.

    boolean isINOUT = getParameterValueSet().getParameterMode(0) == PARAMETER_IN_OUT;
    if (isINOUT) {
        String[] holder = new String[] {?}; com.acme.MyProcedureMethod(holder); ? = holder[0]
         
    } else {
        com.acme.MyProcedureMethod(?)
    }

 *
 */
public class StaticMethodCallNode extends MethodCallNode
{
    private TableName procedureName;

    /**
     * Intializer for a NonStaticMethodCallNode
     *
     * @param methodName The name of the method to call
     * @param javaClassName The name of the java class that the static method belongs to.
     */
    public void init(Object methodName, Object javaClassName) {
        if (methodName instanceof String)
            init(methodName);
        else {
            procedureName = (TableName)methodName;
            init(procedureName.getTableName());
        }

        this.javaClassName = (String)javaClassName;
    }

    public TableName getProcedureName() {
        return procedureName;
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        StaticMethodCallNode other = (StaticMethodCallNode)node;
        this.procedureName = (TableName)getNodeFactory().copyNode(other.procedureName,
                                                                  getParserContext());
    }

    /**
     * Convert this object to a String.  See comments in QueryTreeNode.java
     * for how this should be done for tree printing.
     *
     * @return This object as a String
     */

    public String toString() {
        return "javaClassName: " +
            (javaClassName != null ? javaClassName : "null") + "\n" +
            super.toString();
    }

}