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
import com.akiban.sql.types.DataTypeDescriptor;
import com.akiban.sql.types.JSQLType;
import com.akiban.sql.types.TypeId;

/**
 * This abstract node class represents a data value in the Java domain.
 */

// TODO: I think this is too much (or too little).

public abstract class JavaValueNode extends QueryTreeNode
{
    private boolean mustCastToPrimitive;

    protected boolean forCallStatement;
    private boolean valueReturnedToSQLDomain;
    private boolean returnValueDiscarded;
    protected JSQLType jsqlType;

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        JavaValueNode other = (JavaValueNode)node;
        this.mustCastToPrimitive = other.mustCastToPrimitive;
        this.forCallStatement = other.forCallStatement;
        this.valueReturnedToSQLDomain = other.valueReturnedToSQLDomain;
        this.returnValueDiscarded = other.returnValueDiscarded;
        this.jsqlType = other.jsqlType;
    }

    /**
     * Get the resolved data type of this node. May be overridden by descendants.
     */
    public DataTypeDescriptor getType() throws StandardException {
        return DataTypeDescriptor.getSQLDataTypeDescriptor(getJavaTypeName());
    }

    public boolean isPrimitiveType() throws StandardException {
        JSQLType myType = getJSQLType();

        if (myType == null) { 
            return false;
        }
        else { 
            return (myType.getCategory() == JSQLType.JAVA_PRIMITIVE); 
        }
    }

    public String getJavaTypeName() throws StandardException {
        JSQLType myType = getJSQLType();

        if (myType == null) { 
            return ""; 
        }

        switch(myType.getCategory()) {
        case JSQLType.JAVA_CLASS: 
            return myType.getJavaClassName();

        case JSQLType.JAVA_PRIMITIVE: 
            return JSQLType.getPrimitiveName(myType.getPrimitiveKind());

        default:
            assert false : "Inappropriate JSQLType: " + myType;
        }

        return "";
    }

    public void setJavaTypeName(String javaTypeName) {
        jsqlType = new JSQLType(javaTypeName);
    }

    public String getPrimitiveTypeName() throws StandardException {
        JSQLType myType = getJSQLType();

        if (myType == null) { 
            return ""; 
        }

        switch(myType.getCategory()) {
        case JSQLType.JAVA_PRIMITIVE: 
            return JSQLType.getPrimitiveName(myType.getPrimitiveKind());

        default:
            assert false : "Inappropriate JSQLType: " + myType;
        }

        return "";
    }

    /**
     * Toggles whether the code generator should add a cast to extract a primitive
     * value from an object.
     *
     * @param booleanValue true if we want the code generator to add a cast
     *                                       false otherwise
     */
    public void castToPrimitive(boolean booleanValue) {
        mustCastToPrimitive = booleanValue;
    }

    /**
     * Reports whether the code generator should add a cast to extract a primitive
     * value from an object.
     *
     * @return true if we want the code generator to add a cast
     *               false otherwise
     */
    public boolean mustCastToPrimitive() { 
        return mustCastToPrimitive; 
    }

    /**
     * Get the JSQLType that corresponds to this node. Could be a SQLTYPE,
     * a Java primitive, or a Java class.
     *
     * @return the corresponding JSQLType
     *
     */
    public JSQLType getJSQLType() throws StandardException { 
        return jsqlType; 
    }

    /**
     * Map a JSQLType to a compilation type id.
     *
     * @param jsqlType the universal type to map
     *
     * @return the corresponding compilation type id
     *
     */
    public TypeId mapToTypeID(JSQLType jsqlType) throws StandardException {
        DataTypeDescriptor dts = jsqlType.getSQLType();

        if (dts == null) { 
            return null; 
        }

        return dts.getTypeId();
    }

    /**
     * Mark this node as being for a CALL Statement.
     * (void methods are only okay for CALL Statements)
     */
    public void markForCallStatement() {
        forCallStatement = true;
    }

    /** @see ValueNode#getConstantValueAsObject 
     *
     * @exception StandardException Thrown on error
     */
    Object getConstantValueAsObject() throws StandardException {
        return null;
    }

    /** Inform this node that it returns its value to the SQL domain */
    protected void returnValueToSQLDomain() {
        valueReturnedToSQLDomain = true;
    }

    /** Tell whether this node returns its value to the SQL domain */
    protected boolean valueReturnedToSQLDomain() {
        return valueReturnedToSQLDomain;
    }

    /** Tell this node that nothing is done with the returned value */
    protected void markReturnValueDiscarded() {
        returnValueDiscarded = true;
    }

    /** Tell whether the return value from this node is discarded */
    protected boolean returnValueDiscarded() {
        return returnValueDiscarded;
    }

}