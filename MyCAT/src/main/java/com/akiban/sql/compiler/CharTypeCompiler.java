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
 * This class implements TypeCompiler for the SQL char datatypes.
 *
 */

public final class CharTypeCompiler extends TypeCompiler
{
    protected CharTypeCompiler(TypeId typeId) {
        super(typeId);
    }

    /**
     * Tell whether this type (char) can be converted to the given type.
     *
     * @see TypeCompiler#convertible
     */
    public boolean convertible(TypeId otherType, boolean forDataTypeFunction) {
        if (otherType.isAnsiUDT()) { 
            return false; 
        }
                        
        // LONGVARCHAR can only be converted from    character types
        // or CLOB or boolean.
        if (getTypeId().isLongVarcharTypeId()) {
            return (otherType.isStringTypeId() || otherType.isBooleanTypeId());
        }

        // The double function can convert CHAR and VARCHAR
        if (forDataTypeFunction && otherType.isDoubleTypeId())
            return (getTypeId().isStringTypeId());

        // can't CAST to CHAR and VARCHAR from REAL or DOUBLE
        // or binary types or XML
        // all other types are ok.
        if (otherType.isFloatingPointTypeId() || otherType.isBitTypeId() ||
            otherType.isBlobTypeId() || otherType.isXMLTypeId())
            return false;

        return true;
    }

    /**
     * Tell whether this type (char) is compatible with the given type.
     *
     * @param otherType The TypeId of the other type.
     */
    public boolean compatible(TypeId otherType) {
        return (otherType.isStringTypeId() || 
                (otherType.isDateTimeTimeStampTypeId() && 
                 !getTypeId().isLongVarcharTypeId()));
    }

    /**
     * @see TypeCompiler#getCorrespondingPrimitiveTypeName
     */

    public String getCorrespondingPrimitiveTypeName() {
        /* Only numerics and booleans get mapped to Java primitives */
        return "java.lang.String";
    }

    /**
     * Get the method name for getting out the corresponding primitive
     * Java type.
     *
     * @return String The method call name for getting the
     *                              corresponding primitive Java type.
     */
    public String getPrimitiveMethodName() {
        return "getString";
    }

    /**
     * @see TypeCompiler#getCastToCharWidth
     */
    public int getCastToCharWidth(DataTypeDescriptor dts) {
        return dts.getMaximumWidth();
    }

}