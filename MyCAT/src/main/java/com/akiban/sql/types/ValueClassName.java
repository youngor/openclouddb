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
package com.akiban.sql.types;

/**
    List of strings representing class names, which are typically found
    for classes with implement the Formatable interface.
    These strings are removed from the code to separate them from the
    strings which need to be internationalized. It also reduces footprint.
    <P>
    This class has no methods, all it contains are String's which by default
    are public, static and final since they are declared in an interface.
*/

// TODO: These aren't actually used, but are kept to make it easier to bring them back.

public interface ValueClassName
{
    String BitDataValue = "BitDataValue";
    String BooleanDataValue = "BooleanDataValue";
    String ConcatableDataValue  = "ConcatableDataValue";
    String DataValueDescriptor = "DataValueDescriptor";
    String DateTimeDataValue = "DateTimeDataValue";
    String NumberDataValue = "NumberDataValue";
    String RefDataValue = "RefDataValue";
    String StringDataValue = "StringDataValue";
    String UserDataValue = "UserDataValue";
    String XMLDataValue  = "XMLDataValue";
}