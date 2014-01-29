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
 * A StaticClassFieldReferenceNode represents a Java static field reference from 
 * a Class (as opposed to an Object).    Field references can be 
 * made in DML (as expressions).
 *
 */

public final class StaticClassFieldReferenceNode extends JavaValueNode
{
    /*
    ** Name of the field.
    */
    private String fieldName;

    /* The class name */
    private String javaClassName;
    private boolean classNameDelimitedIdentifier;

    /**
     * Initializer for a StaticClassFieldReferenceNode
     *
     * @param javaClassName The class name
     * @param fieldName The field name
     */
    public void init(Object javaClassName, 
                     Object fieldName, 
                     Object classNameDelimitedIdentifier) {
        this.fieldName = (String)fieldName;
        this.javaClassName = (String)javaClassName;
        this.classNameDelimitedIdentifier = ((Boolean)classNameDelimitedIdentifier).booleanValue();
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        StaticClassFieldReferenceNode other = (StaticClassFieldReferenceNode)node;
        this.fieldName = other.fieldName;
        this.javaClassName = other.javaClassName;
        this.classNameDelimitedIdentifier = other.classNameDelimitedIdentifier;
    }

}