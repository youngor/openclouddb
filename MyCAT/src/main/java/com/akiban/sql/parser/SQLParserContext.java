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

import java.util.Map;

public interface SQLParserContext
{
    /** Check that string literal is not too long. */
    public void checkStringLiteralLengthLimit(String image) throws StandardException;

    /** Check that identifier is not too long. */
    public void checkIdentifierLengthLimit(String identifier) throws StandardException;
    
    /** Mark as returning a parameter. */
    public void setReturnParameterFlag();

    /** Mark as requesting locale. */
    public void setMessageLocale(String locale);

    /** Get a node factory. */
    public NodeFactory getNodeFactory();

    /**
     * Return a map of AST nodes that have already been printed during a
     * compiler phase, so as to be able to avoid printing a node more than once.
     * @see QueryTreeNode#treePrint(int)
     * @return the map
     */
    public Map getPrintedObjectsMap();

    /** Is the given feature enabled for this parser? */
    public boolean hasFeature(SQLParserFeature feature);

    enum IdentifierCase { UPPER, LOWER, PRESERVE };

    /** How are unquoted identifiers standardized? **/
    public IdentifierCase getIdentifierCase();
}