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
import com.akiban.sql.types.TypeId;

public class SQLBooleanConstantNode extends ConstantNode
{
    /**
     * Initializer for a SQLBooleanConstantNode.
     *
     * @param newValue A String containing the value of the constant: true, false, unknown
     *
     * @exception StandardException
     */

    public void init(Object newValue) throws StandardException {
        String strVal = (String)newValue;
        Boolean val = null;

        if ("true".equalsIgnoreCase(strVal))
            val = Boolean.TRUE;
        else if ("false".equalsIgnoreCase(strVal))
            val = Boolean.FALSE;

        /*
        ** RESOLVE: The length is fixed at 1, even for nulls.
        ** Is that OK?
        */

        /* Fill in the type information in the parent ValueNode */
        super.init(TypeId.BOOLEAN_ID,
                   Boolean.TRUE,
                   1);

        setValue(val);
    }

}