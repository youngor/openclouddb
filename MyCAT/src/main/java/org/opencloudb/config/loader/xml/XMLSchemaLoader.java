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
/**
 * (created at 2012-6-14)
 */
package org.opencloudb.config.loader.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opencloudb.config.loader.SchemaLoader;
import org.opencloudb.config.model.DBHostConfig;
import org.opencloudb.config.model.DataHostConfig;
import org.opencloudb.config.model.DataNodeConfig;
import org.opencloudb.config.model.SchemaConfig;
import org.opencloudb.config.model.TableConfig;
import org.opencloudb.config.model.rule.TableRuleConfig;
import org.opencloudb.config.util.ConfigException;
import org.opencloudb.config.util.ConfigUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author mycat
 */
@SuppressWarnings("unchecked")
public class XMLSchemaLoader implements SchemaLoader {
	private final static String DEFAULT_DTD = "/schema.dtd";
	private final static String DEFAULT_XML = "/schema.xml";

	private final Map<String, TableRuleConfig> tableRules;
	private final Map<String, DataHostConfig> dataHosts;
	private final Map<String, DataNodeConfig> dataNodes;
	private final Map<String, SchemaConfig> schemas;

	public XMLSchemaLoader(String schemaFile, String ruleFile) {
		XMLRuleLoader ruleLoader = new XMLRuleLoader(ruleFile);
		this.tableRules = ruleLoader.getTableRules();
		ruleLoader = null;
		this.dataHosts = new HashMap<String, DataHostConfig>();
		this.dataNodes = new HashMap<String, DataNodeConfig>();
		this.schemas = new HashMap<String, SchemaConfig>();
		this.load(DEFAULT_DTD, schemaFile == null ? DEFAULT_XML : schemaFile);
	}

	public XMLSchemaLoader() {
		this(null, null);
	}

	@Override
	public Map<String, TableRuleConfig> getTableRules() {
		return tableRules;
	}

	@Override
	public Map<String, DataHostConfig> getDataHosts() {
		return (Map<String, DataHostConfig>) (dataHosts.isEmpty() ? Collections
				.emptyMap() : dataHosts);
	}

	@Override
	public Map<String, DataNodeConfig> getDataNodes() {
		return (Map<String, DataNodeConfig>) (dataNodes.isEmpty() ? Collections
				.emptyMap() : dataNodes);
	}

	@Override
	public Map<String, SchemaConfig> getSchemas() {
		return (Map<String, SchemaConfig>) (schemas.isEmpty() ? Collections
				.emptyMap() : schemas);
	}

	private void load(String dtdFile, String xmlFile) {
		InputStream dtd = null;
		InputStream xml = null;
		try {
			dtd = XMLSchemaLoader.class.getResourceAsStream(dtdFile);
			xml = XMLSchemaLoader.class.getResourceAsStream(xmlFile);
			Element root = ConfigUtil.getDocument(dtd, xml)
					.getDocumentElement();
			loadDataHosts(root);
			loadDataNodes(root);
			loadSchemas(root);
		} catch (ConfigException e) {
			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
			throw new ConfigException(e);
		} finally {
			if (dtd != null) {
				try {
					dtd.close();
				} catch (IOException e) {
				}
			}
			if (xml != null) {
				try {
					xml.close();
				} catch (IOException e) {
				}
			}
		}
	}

	private void loadSchemas(Element root) {
		NodeList list = root.getElementsByTagName("schema");
		for (int i = 0, n = list.getLength(); i < n; i++) {
			Element schemaElement = (Element) list.item(i);
			String name = schemaElement.getAttribute("name");
			String dataNode = schemaElement.getAttribute("dataNode");
			// 在非空的情况下检查dataNode是否存在
			if (dataNode != null && !dataNode.isEmpty()) {
				List<String> dataNodeLst = new ArrayList<String>(1);
				dataNodeLst.add(dataNode);
				checkDataNodeExists(dataNodeLst);
			} else {
				dataNode = null;
			}
			Map<String, TableConfig> tables = loadTables(schemaElement);
			if (schemas.containsKey(name)) {
				throw new ConfigException("schema " + name + " duplicated!");
			}
			schemas.put(name, new SchemaConfig(name, dataNode, tables));
		}
	}

	private Map<String, TableConfig> loadTables(Element node) {
		Map<String, TableConfig> tables = new HashMap<String, TableConfig>();
		NodeList nodeList = node.getElementsByTagName("table");
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element tableElement = (Element) nodeList.item(i);
			String tableName = tableElement.getAttribute("name").toUpperCase();
			String tableTypeStr = tableElement.hasAttribute("type") ? tableElement
					.getAttribute("type") : null;
			int tableType = TableConfig.TYPE_GLOBAL_DEFAULT;
			if ("global".equalsIgnoreCase(tableTypeStr)) {
				tableType = TableConfig.TYPE_GLOBAL_TABLE;
			}
			String dataNode = tableElement.getAttribute("dataNode");
			TableRuleConfig tableRule = null;
			if (tableElement.hasAttribute("rule")) {
				String ruleName = tableElement.getAttribute("rule");
				tableRule = tableRules.get(ruleName);
				if (tableRule == null) {
					throw new ConfigException("rule " + ruleName
							+ " is not found!");
				}
			}
			boolean ruleRequired = false;
			if (tableElement.hasAttribute("ruleRequired")) {
				ruleRequired = Boolean.parseBoolean(tableElement
						.getAttribute("ruleRequired"));
			}

			TableConfig table = new TableConfig(tableName, tableType, dataNode,
					(tableRule != null) ? tableRule.getRule() : null,
					ruleRequired, null, false, null, null);
			checkDataNodeExists(table.getDataNodes());
			if (tables.containsKey(table.getName())) {
				throw new ConfigException("table " + tableName + " duplicated!");
			}
			tables.put(table.getName(), table);
			// process child tables
			processChildTables(tables, table, dataNode, tableElement);
		}
		return tables;
	}

	private void processChildTables(Map<String, TableConfig> tables,
			TableConfig parentTable, String dataNodes, Element tableNode) {
		// parse child tables
		NodeList childNodeList = tableNode.getChildNodes();
		for (int j = 0; j < childNodeList.getLength(); j++) {
			Node theNode = childNodeList.item(j);
			if (!theNode.getNodeName().equals("childTable")) {
				continue;
			}
			Element childTbElement = (Element) theNode;

			String cdTbName = childTbElement.getAttribute("name").toUpperCase();
			// System.out.println("create child table " + cdTbName);
			String joinKey = childTbElement.getAttribute("joinKey")
					.toUpperCase();
			String parentKey = childTbElement.getAttribute("parentKey")
					.toUpperCase();
			TableConfig table = new TableConfig(cdTbName,
					TableConfig.TYPE_GLOBAL_DEFAULT, dataNodes, null, false,
					parentTable, true, joinKey, parentKey);
			if (tables.containsKey(table.getName())) {
				throw new ConfigException("table " + table.getName()
						+ " duplicated!");
			}
			tables.put(table.getName(), table);
			processChildTables(tables, table, dataNodes, childTbElement);
		}
	}

	private void checkDataNodeExists(Collection<String> nodes) {
		if (nodes == null || nodes.size() < 1) {
			return;
		}
		for (String node : nodes) {
			if (!dataNodes.containsKey(node)) {
				throw new ConfigException("dataNode '" + node
						+ "' is not found!");
			}
		}
	}

	private void loadDataNodes(Element root) {
		NodeList list = root.getElementsByTagName("dataNode");
		for (int i = 0, n = list.getLength(); i < n; i++) {
			Element element = (Element) list.item(i);
			String dnNamePre = element.getAttribute("name");

			String databaseStr = element.getAttribute("database");
			String host = element.getAttribute("dataHost");
			if (empty(dnNamePre) || empty(databaseStr) || empty(host)) {
				throw new ConfigException("dataNode " + dnNamePre
						+ " define error ,attribute can't be empty");
			}
			String[] databases = org.opencloudb.util.SplitUtil.split(
					databaseStr, ',', '$', '-', 's', '_');
			if (databases.length > 1) {
				for (int k = 0; k < databases.length; k++) {
					String databaseName = databases[k].substring(0,
							databases[k].length() - 1);
					createDataNode(dnNamePre + '[' + k + ']', databaseName,
							host);
					// System.out.println("dn:"+dnNamePre + '[' + k +
					// ']'+",databse:"+databaseName+",host:"+host);
				}
			} else {
				createDataNode(dnNamePre, databaseStr, host);
			}

		}
	}

	private void createDataNode(String dnName, String database, String host) {
		DataNodeConfig conf = new DataNodeConfig(dnName, database, host);
		if (dataNodes.containsKey(conf.getName())) {
			throw new ConfigException("dataNode " + conf.getName()
					+ " duplicated!");
		}
		if (!dataHosts.containsKey(host)) {
			throw new ConfigException("dataNode " + dnName
					+ " reference dataHost:" + host + " not exists!");
		}
		dataNodes.put(conf.getName(), conf);
	}

	private boolean empty(String dnName) {
		return dnName == null || dnName.length() == 0;
	}

	private DBHostConfig createDBHostConf(String dataHost, Element node,
			String dbType, String dbDriver, int maxCon, int minCon) {
		String nodeHost = node.getAttribute("host");
		String nodeUrl = node.getAttribute("url");
		String user = node.getAttribute("user");
		String password = node.getAttribute("password");
		String ip = null;
		int port = 0;
		if (empty(nodeHost) || empty(nodeUrl) || empty(user)) {
			throw new ConfigException(
					"dataHost "
							+ dataHost
							+ " define error,some attributes of this element is empty: "
							+ nodeHost);
		}
		if ("native".equalsIgnoreCase(dbDriver)) {
			int colonIndex = nodeUrl.indexOf(':');
			ip = nodeUrl.substring(0, colonIndex).trim();
			port = Integer.parseInt(nodeUrl.substring(colonIndex + 1).trim());
		} else {
			URL url;
			try {
				url = new URL(nodeUrl);
			} catch (MalformedURLException e) {
				throw new ConfigException("invalid jdbc url " + nodeUrl
						+ " of " + dataHost);
			}
			ip = url.getHost();
			port = url.getPort();
		}

		DBHostConfig conf = new DBHostConfig(nodeHost, ip, port, nodeUrl, user,
				password);
		conf.setDbType(dbType);
		conf.setMaxCon(maxCon);
		conf.setMinCon(minCon);
		return conf;
	}

	private void loadDataHosts(Element root) {
		NodeList list = root.getElementsByTagName("dataHost");
		for (int i = 0, n = list.getLength(); i < n; ++i) {
			Element element = (Element) list.item(i);
			String name = element.getAttribute("name");
			if (dataHosts.containsKey(name)) {
				throw new ConfigException("dataHost name " + name
						+ "duplicated!");
			}
			int maxCon = Integer.valueOf(element.getAttribute("maxCon"));
			int minCon = Integer.valueOf(element.getAttribute("minCon"));
			int balance = Integer.valueOf(element.getAttribute("balance"));
			String dbDriver = element.getAttribute("dbDriver");
			String dbType = element.getAttribute("dbType");
			String heartbeatSQL = element.getElementsByTagName("heartbeat")
					.item(0).getTextContent();
			NodeList writeNodes = element.getElementsByTagName("writeHost");
			DBHostConfig[] writeDbConfs = new DBHostConfig[writeNodes
					.getLength()];
			Map<Integer, DBHostConfig[]> readHostsMap = new HashMap<Integer, DBHostConfig[]>(
					2);
			for (int w = 0; w < writeDbConfs.length; w++) {
				Element writeNode = (Element) writeNodes.item(w);
				writeDbConfs[w] = createDBHostConf(name, writeNode, dbType,
						dbDriver, maxCon, minCon);
				NodeList readNodes = writeNode.getElementsByTagName("readHost");
				DBHostConfig[] readDbConfs = new DBHostConfig[readNodes
						.getLength()];
				for (int r = 0; r < readDbConfs.length; r++) {
					Element readNode = (Element) readNodes.item(r);
					readDbConfs[r] = createDBHostConf(name, readNode, dbType,
							dbDriver, maxCon, minCon);
				}
				readHostsMap.put(w, readDbConfs);

			}

			DataHostConfig hostConf = new DataHostConfig(name, dbType,
					dbDriver, writeDbConfs, readHostsMap);
			hostConf.setMaxCon(maxCon);
			hostConf.setMinCon(minCon);
			hostConf.setBalance(balance);
			hostConf.setHearbeatSQL(heartbeatSQL);
			dataHosts.put(hostConf.getName(), hostConf);

		}
	}

}