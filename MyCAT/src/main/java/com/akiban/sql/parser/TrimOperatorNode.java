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

import com.akiban.sql.types.ValueClassName;

public class TrimOperatorNode extends BinaryOperatorNode
{
    @Override
    public void init(Object trimSource, Object trimChar, Object operatorType)
    {   
        BinaryOperatorNode.OperatorType optype = (BinaryOperatorNode.OperatorType)operatorType;
        switch(optype)
        {
            default:     assert false : "TrimOperatorNode.init(trimSource, trimChar, operatorType) called with wrong OperatoryType: " + operatorType;
            case LTRIM:
            case TRIM:
            case RTRIM:  super.init(trimSource,
                                    trimChar,
                                    optype.name(),
                                    optype.name().toLowerCase(),
                                    ValueClassName.StringDataValue,
                                    ValueClassName.StringDataValue);
        }
    }
}