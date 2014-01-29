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

import java.sql.Types;

public class TimestampTypeCompiler extends TypeCompiler
{
    protected TimestampTypeCompiler(TypeId typeId) {
        super(typeId);
    }

    /**
     * User types are convertible to other user types only if
     * (for now) they are the same type and are being used to
     * implement some JDBC type.    This is sufficient for
     * date/time types; it may be generalized later for e.g.
     * comparison of any user type with one of its subtypes.
     *
     * @see TypeCompiler#convertible 
     *
     */
    public boolean convertible(TypeId otherType, boolean forDataTypeFunction) {
        if (otherType.isStringTypeId() &&
            (!otherType.isLongConcatableTypeId())) {
            return true;
        }

        int otherJDBCTypeId = otherType.getJDBCTypeId();

        /*
        ** At this point, we have only date/time.    If
        ** same type, convert always ok.
        */
        if (otherJDBCTypeId == Types.TIMESTAMP) {
            return true;
        }

        /*
        ** Otherwise, we can convert timestamp to
        ** date or time only.
        */
        return ((otherJDBCTypeId == Types.DATE) ||
                (otherJDBCTypeId == Types.TIME));
    }

    /**
     * Tell whether this type (timestamp) is compatible with the given type.
     *
     * @param otherType The TypeId of the other type.
     */
    public boolean compatible(TypeId otherType) {
        if (otherType.isStringTypeId() &&
            (!otherType.isLongConcatableTypeId())) {
            return true;
        }
        /*
        ** Both are timestamp datatypes and hence compatible.
        */
        return (getStoredFormatIdFromTypeId() == otherType.getTypeFormatId());
    }
            
    /**
     * @see TypeCompiler#getCorrespondingPrimitiveTypeName
     */

    public String getCorrespondingPrimitiveTypeName() {
        return "java.sql.Timestamp";
    }

    /**
     * Get the method name for getting out the corresponding primitive
     * Java type.
     *
     * @return String The method call name for getting the
     *                              corresponding primitive Java type.
     */
    public String getPrimitiveMethodName() {
        return "getTimestamp";
    }

    /**
     * @see TypeCompiler#getCastToCharWidth
     */
    public int getCastToCharWidth(DataTypeDescriptor dts) {
        return 26; // DATE TIME.milliseconds (extra few for good measure)
    }

    /**
     * @see TypeCompiler#resolveArithmeticOperation
     *
     * @exception StandardException     Thrown on error
     */
    public DataTypeDescriptor resolveArithmeticOperation(DataTypeDescriptor leftType,
                                                         DataTypeDescriptor rightType,
                                                         String operator)
            throws StandardException {
        TypeId rightTypeId = rightType.getTypeId();
        boolean nullable = leftType.isNullable() || rightType.isNullable();
        if (rightTypeId.isDateTimeTimeStampTypeId()) {
            if (operator.equals(TypeCompiler.MINUS_OP)) {
                // TIMESTAMP - other datetime is INTERVAL DAY TO SECOND
                return new DataTypeDescriptor(TypeId.INTERVAL_DAY_SECOND_ID, nullable);
            }
        }
        else if (rightTypeId.isIntervalTypeId()) {
            if (operator.equals(TypeCompiler.PLUS_OP) ||
                operator.equals(TypeCompiler.MINUS_OP)) {
                // TIMESTAMP +/- interval is TIMESTAMP
                return leftType.getNullabilityType(nullable);
            }
        }

        // Unsupported
        return super.resolveArithmeticOperation(leftType, rightType, operator);
    }

}