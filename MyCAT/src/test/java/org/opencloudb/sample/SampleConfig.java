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
package org.opencloudb.sample;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 服务器配置信息示例
 * 
 * @author mycat
 */
public class SampleConfig {

    /**
     * 服务器名
     */
    private String serverName;

    /**
     * 可登录的用户和密码
     */
    private Map<String, String> users;

    /**
     * 可使用的schemas
     */
    private Set<String> schemas;

    /**
     * 指定用户可使用的schemas
     */
    private Map<String, Set<String>> userSchemas;

    public SampleConfig() {
        this.serverName = "Sample";

        // add user/password
        this.users = new HashMap<String, String>();
        this.users.put("root", null);
        this.users.put("test", "12345");

        // add schema
        this.schemas = new HashSet<String>();
        this.schemas.add("schema1");
        this.schemas.add("schema2");
        this.schemas.add("schema3");

        // add user/schema
        this.userSchemas = new HashMap<String, Set<String>>();
        Set<String> schemaSet = new HashSet<String>();
        schemaSet.add("schema1");
        schemaSet.add("schema3");
        this.userSchemas.put("test", schemaSet);
    }

    public String getServerName() {
        return serverName;
    }

    public Map<String, String> getUsers() {
        return users;
    }

    public Set<String> getSchemas() {
        return schemas;
    }

    public Map<String, Set<String>> getUserSchemas() {
        return userSchemas;
    }

}