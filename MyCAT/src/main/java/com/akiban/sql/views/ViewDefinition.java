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
package com.akiban.sql.views;

import com.akiban.sql.parser.*;

import com.akiban.sql.StandardException;

public class ViewDefinition
{
    private CreateViewNode definition;
    private FromSubquery subquery;

    /**
     * Parse the given SQL as CREATE VIEW and remember the definition.
     */
    public ViewDefinition(String sql, SQLParser parser)
            throws StandardException {
        this(parser.parseStatement(sql), parser);
    }

    public ViewDefinition(StatementNode parsed, SQLParserContext parserContext)
            throws StandardException {
        if (parsed.getNodeType() != NodeTypes.CREATE_VIEW_NODE) {
            throw new StandardException("Parsed statement was not a view");
        }
        definition = (CreateViewNode)parsed;
        subquery = (FromSubquery)
            parserContext.getNodeFactory().getNode(NodeTypes.FROM_SUBQUERY,
                                                   definition.getParsedQueryExpression(),
                                                   definition.getOrderByList(),
                                                   definition.getOffset(),
                                                   definition.getFetchFirst(),
                                                   getName().getTableName(),
                                                   definition.getResultColumns(),
                                                   null,
                                                   parserContext);
    }

    /** 
     * Get the name of the view.
     */
    public TableName getName() {
        return definition.getObjectName();
    }

    /**
     * Get the text of the view definition.
     */
    public String getQueryExpression() {
        return definition.getQueryExpression();
    }

    /**
     * Get the result columns for this view.
     */
    public ResultColumnList getResultColumns() {
        ResultColumnList rcl = subquery.getResultColumns();
        if (rcl == null)
            rcl = subquery.getSubquery().getResultColumns();
        return rcl;
    }

    /**
     * Get the original subquery for binding.
     */
    public FromSubquery getSubquery() {
        return subquery;
    }

    /**
     * Get the view as an equivalent subquery belonging to the given context.
     */
    public FromSubquery copySubquery(SQLParserContext parserContext) 
            throws StandardException {
        return (FromSubquery)
            parserContext.getNodeFactory().copyNode(subquery, parserContext);
    }

    /**
     * @deprecated
     * @see #copySubquery
     */
    @Deprecated
    public FromSubquery getSubquery(Visitor binder) throws StandardException {
        subquery = (FromSubquery)subquery.accept(binder);
        return copySubquery(subquery.getParserContext());
    }

}