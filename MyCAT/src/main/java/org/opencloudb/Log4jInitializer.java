/*
 * Copyright 2012-2015 org.opencloudb.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opencloudb;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.LogManager;
import org.apache.log4j.helpers.FileWatchdog;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * @author mycat
 */
public final class Log4jInitializer {
    private static final String format = "yyyy-MM-dd HH:mm:ss";
    public static void configureAndWatch(String filename, long delay) {
        XMLWatchdog xdog = new XMLWatchdog(filename);
        xdog.setName("Log4jWatchdog");
        xdog.setDelay(delay);
        xdog.start();
    }

    private static final class XMLWatchdog extends FileWatchdog {

        public XMLWatchdog(String filename) {
            super(filename);
        }

        @Override
        public void doOnChange() {
            new DOMConfigurator().doConfigure(filename, LogManager.getLoggerRepository());
            System.out.println("log4j "+new SimpleDateFormat(format).format(new Date()) + " [" + filename + "] load completed.");
        }
    }

}