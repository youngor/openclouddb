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

import java.util.List;

/**
 * A MethodCallNode represents a Java method call.  Method calls can be done
 * through DML (as expressions) or through the CALL statement.
 *
 */

public abstract class MethodCallNode extends JavaValueNode
{
    /*
    ** Name of the method.
    */
    protected String methodName;

    /** The name of the class containing the method. May not be known until bindExpression() has been called.
     * @see #bindExpression
     * @see #getJavaClassName()
     */
    protected String javaClassName;

    /*
    ** Parameters to the method, if any.    No elements if no parameters.
    */
    protected JavaValueNode[] methodParms;

    /**
     * Initializer for a MethodCallNode
     *
     * @param methodName The name of the method to call
     */
    public void init(Object methodName) {
        this.methodName = (String)methodName;
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        MethodCallNode other = (MethodCallNode)node;
        this.methodName = other.methodName;
        this.javaClassName = other.javaClassName;
        if (other.methodParms != null) {
            this.methodParms = new JavaValueNode[other.methodParms.length];
            for (int i = 0; i < this.methodParms.length; i++) {
                this.methodParms[i] = (JavaValueNode)
                    getNodeFactory().copyNode(other.methodParms[i], getParserContext());
            }
        }
    }

    public String getMethodName() {
        return methodName;
    }

    /**
     * @return the name of the class that contains the method, null if not known. It may not be known
     *               until this node has been bound.
     */
    public String getJavaClassName() {
        return javaClassName;
    }

    public void setJavaClassName(String javaClassName) {
        this.javaClassName = javaClassName;
    }

    public JavaValueNode[] getMethodParameters() {
        return methodParms;
    }

    /**
     * Add the parameter list
     *
     * @param parameterList A List of the parameters
     *
     * @exception StandardException Thrown on error
     */
    public void addParms(List<ValueNode> parameterList) throws StandardException {
        methodParms = new JavaValueNode[parameterList.size()];

        int plSize = parameterList.size();
        for (int index = 0; index < plSize; index++) {
            QueryTreeNode qt = parameterList.get(index);

            /*
            ** Since we need the parameter to be in Java domain format, put a
            ** SQLToJavaValueNode on top of the parameter node if it is a 
            ** SQLValueNode. But if the parameter is already in Java domain 
            ** format, then we don't need to do anything.
            */
            if (!(qt instanceof JavaValueNode)) {
                qt = (SQLToJavaValueNode)
                    getNodeFactory().getNode(NodeTypes.SQL_TO_JAVA_VALUE_NODE, 
                                             qt, 
                                             getParserContext());
            }

            methodParms[index] = (JavaValueNode)qt;
        }
    }

    /**
     * Prints the sub-nodes of this object.  See QueryTreeNode.java for
     * how tree printing is supposed to work.
     *
     * @param depth The depth of this node in the tree
     */

    public void printSubNodes(int depth) {
        super.printSubNodes(depth);
        if (methodParms != null) {
            for (int parm = 0; parm < methodParms.length; parm++) {
                if (methodParms[parm] != null) {
                    printLabel(depth, "methodParms[" + parm + "] :");
                    methodParms[parm].treePrint(depth + 1);
                }
            }
        }
    }

    /**
     * Convert this object to a String.  See comments in QueryTreeNode.java
     * for how this should be done for tree printing.
     *
     * @return This object as a String
     */

    public String toString() {
        return "methodName: " +
            (methodName != null ? methodName : "null") + "\n" +
            super.toString();
    }

    /**
     * Accept the visitor for all visitable children of this node.
     * 
     * @param v the visitor
     *
     * @exception StandardException on error
     */
    void acceptChildren(Visitor v) throws StandardException {
        super.acceptChildren(v);

        for (int parm = 0; !v.stopTraversal() && parm < methodParms.length; parm++) {
            if (methodParms[parm] != null) {
                methodParms[parm] = (JavaValueNode)methodParms[parm].accept(v);
            }
        }
    }

}