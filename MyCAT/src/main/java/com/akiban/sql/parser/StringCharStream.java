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

import java.io.EOFException;
import java.io.IOException;

/**
 * {@link CharStream} that simply reads from a string.
 */
public class StringCharStream implements CharStream
{
    private static final IOException EOF = new EOFException();

    private String string;
    private int beginIndex, currentIndex; // 0-based, exclusive end.
    private int currentLine, currentColumn; // 1-based.
    // End represents the position of the last character returned, and
    // in particular if a newline was returned, it at the end of the
    // previous line.
    private int beginLine, beginColumn, endLine, endColumn;
    
    public StringCharStream(String string) {
        init(string);
    }

    public void ReInit(String string) {
        init(string);
    }

    private void init(String string) {
        this.string = string;
        beginIndex = currentIndex = 0;
        currentLine = currentColumn = beginLine = beginColumn = endLine = endColumn = 1;
    }
    
    @Override
    public char BeginToken() throws java.io.IOException {
        beginIndex = currentIndex;
        beginLine = currentLine;
        beginColumn = currentColumn;
        return readChar();
    }

    @Override
    public char readChar() throws java.io.IOException {
        if (currentIndex >= string.length())
            throw EOF;

        return advance();
    }

    @Override
    public void backup(int amount) {
        int target = currentIndex - amount;
        assert (target >= beginIndex);
        currentIndex = beginIndex;
        currentLine = beginLine;
        currentColumn = beginColumn;
        while (currentIndex < target)
            advance();          // Adjusting line / column.
    }

    private char advance() {
        endLine = currentLine;
        endColumn = currentColumn;
        char ch = string.charAt(currentIndex++);
        switch (ch) {
        case '\r':
            if ((currentIndex < string.length()) &&
                (string.charAt(currentIndex) == '\n')) {
                currentColumn++;
                break;
            }
            /* else falls through (bare CR) */
        case '\n':
            currentLine++;
            currentColumn = 1;
            break;
        case '\t':
            endColumn += (8 - (endColumn & 7));
            currentColumn = endColumn + 1;
            break;
        default:
            currentColumn++;
            break;
        }
        return ch;
    }

    @Override
    public int getBeginOffset() {
        return beginIndex;
    }
    @Override
    public int getEndOffset() {
        return currentIndex - 1;   // Want inclusive.
    }

    @Override
    public int getBeginLine() {
        return beginLine;
    }
    @Override
    public int getBeginColumn() {
        return beginColumn;
    }

    @Override
    public int getEndLine() {
        return endLine;
    }
    @Override
    public int getEndColumn() {
        return endColumn;
    }

    @Override
    public int getLine() {
        return getEndLine();
    }
    @Override
    public int getColumn() {
        return getEndColumn();
    }

    @Override
    public String GetImage() {
        return string.substring(beginIndex, currentIndex);
    }

    @Override
    public char[] GetSuffix(int len) {
        char[] result = new char[len];
        string.getChars(currentIndex - len, currentIndex, result, 0);
        return result;
    }

    @Override
    public void Done() {
    }

}