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
 * Describe an A (Abstract Data Type) alias. For the first release of USer
 * Defined Types, this is a vacuous object. Future revs may add real information
 * to this object. The UDTAliasInfo maintains a version stamp so that it
 * can evolve its persistent form over time.
 *
 * @see AliasInfo
 */
public class UDTAliasInfo implements AliasInfo
{
    public UDTAliasInfo() {
    }

    public boolean isTableFunction() {
        return false; 
    }

    public String toString() {
        return "LANGUAGE JAVA";
    }

    public String getMethodName()
    {
        return null;
    }
}