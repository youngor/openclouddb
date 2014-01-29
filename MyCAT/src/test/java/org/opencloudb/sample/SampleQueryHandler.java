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

import org.apache.log4j.Logger;
import org.opencloudb.net.handler.FrontendQueryHandler;

/**
 * @author mycat
 */
public class SampleQueryHandler implements FrontendQueryHandler {
    private static final Logger LOGGER = Logger.getLogger(SampleQueryHandler.class);

    private SampleConnection source;

    public SampleQueryHandler(SampleConnection source) {
        this.source = source;
    }

    @Override
    public void query(String sql) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(new StringBuilder().append(source).append(sql).toString());
        }

        // sample response
        SampleResponseHandler.response(source, sql);
    }

}