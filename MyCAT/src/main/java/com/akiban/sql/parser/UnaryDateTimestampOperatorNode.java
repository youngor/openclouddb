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

import java.sql.Types;

/**
 * This class implements the timestamp(x) and date(x) functions.
 *
 * These two functions implement a few special cases of string conversions beyond the normal string to
 * date/timestamp casts.
 */
public class UnaryDateTimestampOperatorNode extends UnaryOperatorNode
{
    private static final String DATE_METHOD_NAME = "date";
    private static final String TIME_METHOD_NAME = "time";
    private static final String TIMESTAMP_METHOD_NAME = "timestamp";
        
    /**
     * @param operand The operand of the function
     * @param targetType The type of the result. Timestamp or Date.
     *
     * @exception StandardException Thrown on error
     */

    public void init(Object operand, Object targetType) throws StandardException {
        setType((DataTypeDescriptor)targetType);
        switch(getType().getJDBCTypeId()) {
        case Types.DATE:
            super.init(operand, "date", DATE_METHOD_NAME);
            break;

        case Types.TIME:
            super.init(operand, "time", TIME_METHOD_NAME);
            break;

        case Types.TIMESTAMP:
            super.init(operand, "timestamp", TIMESTAMP_METHOD_NAME);
            break;

        default:
            assert false;
            super.init(operand);
        }
    }
        
}