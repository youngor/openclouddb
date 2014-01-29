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

/**
        This node represents a like comparison operator (no escape)

        If the like pattern is a constant or a parameter then if possible
        the like is modified to include a >= and < operator. In some cases
        the like can be eliminated.  By adding =, >= or < operators it may
        allow indexes to be used to greatly narrow the search range of the
        query, and allow optimizer to estimate number of rows to affected.


        constant or parameter LIKE pattern with prefix followed by optional wild 
        card e.g. Derby%

        CHAR(n), VARCHAR(n) where n < 255

                >=   prefix padded with '\u0000' to length n -- e.g. Derby\u0000\u0000
                <=   prefix appended with '\uffff' -- e.g. Derby\uffff

                [ can eliminate LIKE if constant. ]


        CHAR(n), VARCHAR(n), LONG VARCHAR where n >= 255

                >= prefix backed up one characer
                <= prefix appended with '\uffff'

                no elimination of like


        parameter like pattern starts with wild card e.g. %Derby

        CHAR(n), VARCHAR(n) where n <= 256

                >= '\u0000' padded with '\u0000' to length n
                <= '\uffff'

                no elimination of like

        CHAR(n), VARCHAR(n), LONG VARCHAR where n > 256

                >= NULL

                <= '\uffff'


        Note that the Unicode value '\uffff' is defined as not a character value
        and can be used by a program for any purpose. We use it to set an upper
        bound on a character range with a less than predicate. We only need a single
        '\uffff' appended because the string 'Derby\uffff\uffff' is not a valid
        String because '\uffff' is not a valid character.

**/

public final class LikeEscapeOperatorNode extends TernaryOperatorNode
{

    /**
     * Initializer for a LikeEscapeOperatorNode
     *
     * receiver like pattern [ escape escapeValue ]
     *
     * @param receiver          The left operand of the like: 
     *                                                          column, CharConstant or Parameter
     * @param leftOperand       The right operand of the like: the pattern
     * @param rightOperand  The optional escape clause, null if not present
     */
    public void init(Object receiver,
                     Object leftOperand,
                     Object rightOperand)
    {
        /* By convention, the method name for the like operator is "like" */
        super.init(receiver, leftOperand, rightOperand, 
                   TernaryOperatorNode.OperatorType.LIKE, null); 
    }

}