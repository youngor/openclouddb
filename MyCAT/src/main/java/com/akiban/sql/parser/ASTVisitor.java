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
 * <p>
 * A Visitor which handles nodes in Derby's abstract syntax trees. In addition
 * to this contract, it is expected that an ASTVisitor will have a 0-arg
 * constructor. You use an ASTVisitor like this:
 * </p>
 *
 * <blockquote><pre>
 * // initialize your visitor
 * MyASTVisitor myVisitor = new MyASTVisitor();
 * myVisitor.initializeVisitor();
 * languageConnectionContext.setASTVisitor( myVisitor );
 *
 * // then run your queries.
 * ...
 *
 * // when you're done inspecting query trees, release resources and
 * // remove your visitor
 * languageConnectionContext.setASTVisitor( null );
 * myVisitor.teardownVisitor();
 * </pre></blockquote>
 *
 */
public interface ASTVisitor extends Visitor
{
    // Compilation phases for tree handling

    public static final int AFTER_PARSE = 0;
    public static final int AFTER_BIND = 1;
    public static final int AFTER_OPTIMIZE = 2;

    /**
     * Initialize the Visitor before processing any trees. User-written code
     * calls this method before poking the Visitor into the
     * LanguageConnectionContext. For example, an
     * implementation of this method might open a trace file.
     */
    public void initializeVisitor() throws StandardException;

    /**
     * Final call to the Visitor. User-written code calls this method when it is
     * done inspecting query trees. For instance, an implementation of this method
     * might release resources, closing files it has opened.
     */
    public void teardownVisitor() throws StandardException;

    /**
     * The compiler calls this method just before walking a query tree.
     *
     * @param statementText Text used to create the tree.
     * @param phase of compilation (AFTER_PARSE, AFTER_BIND, or AFTER_OPTIMIZE).
     */
    public void begin(String statementText, int phase) throws StandardException;
        
    /**
     * The compiler calls this method when it's done walking a tree.
     *
     * @param phase of compilation (AFTER_PARSE, AFTER_BIND, or AFTER_OPTIMIZE).
     */
    public void end(int phase) throws StandardException;
        
}