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
 * A NewInvocationNode represents a new object() invocation.
 *
 */
public class NewInvocationNode extends MethodCallNode
{
  private boolean delimitedIdentifier;
  private boolean isBuiltinVTI = false;

  /**
   * Initializer for a NewInvocationNode. Parameters are:
   *
   * <ul>
   * <li>javaClassName		The full package.class name of the class</li>
   * <li>parameterList		The parameter list for the constructor</li>
   * </ul>
   *
   * @exception StandardException		Thrown on error
   */
  public void init(Object javaClassName,
                   Object params,
                   Object delimitedIdentifier) 
      throws StandardException {
    super.init("<init>");
    addParms((List<ValueNode>)params);

    this.javaClassName = (String)javaClassName;
    this.delimitedIdentifier = ((Boolean)delimitedIdentifier).booleanValue();
  }

  /* This version of the "init" method is used for mapping a table name
   * or table function name to a corresponding VTI class name.  The VTI
   * is then invoked as a regular NEW invocation node.
   *
   * There are two kinds of VTI mappings that we do: the first is for
   * "table names", the second is for "table function names".  Table
   * names can only be mapped to VTIs that do not accept any arguments;
   * any VTI that has at least one constructor which accepts one or more
   * arguments must be mapped from a table *function* name.  The way we
   * tell the difference is by looking at the received arguments: if
   * the vtiTableFuncName that we receive is null then we are mapping
   * a "table name" and tableDescriptor must be non-null; if the
   * vtiTableFuncName is non-null then we are mapping a "table
   * function name" and tableDescriptor must be null.
   *
   * Note that we could have just used a single "init()" method and
   * performed the mappings based on what type of Object "javaClassName"
   * was (String, TableDescriptor, or TableName), but making this VTI
   * mapping method separate from the "normal" init() method seems
   * cleaner...
   *
   * @param vtiTableFuncName A TableName object holding a qualified name
   *  that maps to a VTI which accepts arguments.  If vtiTableFuncName is
   *  null then tableDescriptor must NOT be null.
   * @param tableDescriptor A table descriptor that corresponds to a
   *  table name (as opposed to a table function name) that will be
   *  mapped to a no-argument VTI.  If tableDescriptor is null then
   *  vtiTableFuncName should not be null.
   * @param params Parameter list for the VTI constructor.
   * @param delimitedIdentifier Whether or not the target class name
   *  is a delimited identifier.
   */
  public void init(Object vtiTableFuncName,
                   Object tableDescriptor,
                   Object params,
                   Object delimitedIdentifier)
      throws StandardException {
    super.init("<init>");
    addParms((List<ValueNode>)params);

    // TODO: Need to handle tableDescriptor being null, which it always is in this case.

    this.delimitedIdentifier = ((Boolean)delimitedIdentifier).booleanValue();
  }

  @Override
  public void init (Object methodName,
                   Object params,
                   Object delimitedIdentifier,
                   Object nothing1,
                   Object nothing2) throws StandardException
  {
       super.init((String)methodName);
        addParms((List<ValueNode>)params);

    // TODO: Need to handle tableDescriptor being null, which it always is in this case.

    this.delimitedIdentifier = ((Boolean)delimitedIdentifier).booleanValue();
  }
  /**
   * Fill this node with a deep copy of the given node.
   */
  public void copyFrom(QueryTreeNode node) throws StandardException {
    super.copyFrom(node);

    NewInvocationNode other = (NewInvocationNode)node;
    this.delimitedIdentifier = other.delimitedIdentifier;
    this.isBuiltinVTI = other.isBuiltinVTI;
  }

  /**
   * Report whether this node represents a builtin VTI.
   */
  public boolean isBuiltinVTI() {
    // TODO: How to set this?
    return isBuiltinVTI; 
  }

}