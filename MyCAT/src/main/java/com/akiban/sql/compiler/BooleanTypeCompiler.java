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
package com.akiban.sql.compiler;

import com.akiban.sql.parser.*;

import com.akiban.sql.StandardException;
import com.akiban.sql.types.DataTypeDescriptor;
import com.akiban.sql.types.TypeId;

/**
 * This class implements TypeCompiler for the SQL BOOLEAN datatype.
 *
 */

public class BooleanTypeCompiler extends TypeCompiler
{
    protected BooleanTypeCompiler(TypeId typeId) {
        super(typeId);
    }

    /**
     * Tell whether this type (boolean) can be converted to the given type.
     *
     * @see TypeCompiler#convertible
     */
    public boolean convertible(TypeId otherType, boolean forDataTypeFunction)
    {
        return (otherType.isStringTypeId() || otherType.isBooleanTypeId());
    }

    /**
     * Tell whether this type (boolean) is compatible with the given type.
     *
     * @param otherType         The TypeId of the other type.
     */
    public boolean compatible(TypeId otherType) {
        return convertible(otherType,false);
    }

    /**
     * @see TypeCompiler#getCorrespondingPrimitiveTypeName
     */
    public String getCorrespondingPrimitiveTypeName() {
        /* Only numerics and booleans get mapped to Java primitives */
        return "boolean";
    }

    /**
     * Get the method name for getting out the corresponding primitive
     * Java type.
     *
     * @return String The method call name for getting the
     *                              corresponding primitive Java type.
     */
    public String getPrimitiveMethodName() {
        return "getBoolean";
    }

    /**
     * @see TypeCompiler#getCastToCharWidth
     */
    public int getCastToCharWidth(DataTypeDescriptor dts) {
        return TypeCompiler.BOOLEAN_MAXWIDTH_AS_CHAR;
    }

}