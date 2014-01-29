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

public final class CharConstantNode extends ConstantNode
{
    /**
     * Initializer for a CharConstantNode.
     *
     * @param arg1 A String containing the value of the constant OR The TypeId for the type of the node
     *
     * @exception StandardException
     */
    public void init(Object arg1) throws StandardException {
        if (arg1 instanceof TypeId) {
            super.init(arg1,
                       Boolean.TRUE,
                       0);
        }
        else {
            String val = (String)arg1;

            super.init(TypeId.CHAR_ID,
                       (val == null) ? Boolean.TRUE : Boolean.FALSE,
                       (val != null) ? val.length() : 0);

            setValue(val);
        }
    }

    /**
     * Initializer for a CharConstantNode of a specific length.
     *
     * @param newValue A String containing the value of the constant
     * @param newLength The length of the new value of the constant
     *
     * @exception StandardException
     */
    public void init(Object newValue, Object newLength) throws StandardException {
        String val = (String)newValue;
        int newLen = ((Integer)newLength).intValue();

        super.init(TypeId.CHAR_ID,
                   (val == null) ? Boolean.TRUE : Boolean.FALSE,
                   newLength);

        if (val.length() > newLen) {
            throw new StandardException("Value truncated");
        }

        // Blank pad the string if necessesary
        while (val.length() < newLen) {
            val = val + ' ';
        }

        setValue(val);
    }

    /**
     * Return the value from this CharConstantNode
     *
     * @return The value of this CharConstantNode.
     *
     * @exception StandardException Thrown on error
     */

    public String getString() throws StandardException {
        return (String)value;
    }

    /**
     * Return an Object representing the bind time value of this
     * expression tree.  If the expression tree does not evaluate to
     * a constant at bind time then we return null.
     * This is useful for bind time resolution of VTIs.
     * RESOLVE: What do we do for primitives?
     *
     * @return An Object representing the bind time value of this expression tree.
     *               (null if not a bind time constant.)
     *
     * @exception StandardException Thrown on error
     */
    Object getConstantValueAsObject() throws StandardException {
        return (String)value;
    }

}