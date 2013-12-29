package org.opencloudb.backend;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.opencloudb.config.Alarms;
import org.opencloudb.heartbeat.DBHeartbeat;
import org.opencloudb.mysql.nio.handler.GetConnectionHandler;
import org.opencloudb.mysql.nio.handler.ResponseHandler;

public class PhysicalDBPool {
	private static final int BALANCE_NONE = 0;
	private static final int BALANCE_ALL_BACK = 1;
	private static final int BALANCE_ALL = 2;
	protected static final Logger LOGGER = Logger
			.getLogger(PhysicalDBPool.class);
	private final String hostName;
	protected PhysicalDatasource[] sources;
	protected Map<Integer, PhysicalDatasource[]> readSources;
	protected volatile int activedIndex;
	protected volatile boolean initSuccess;
	protected final ReentrantLock switchLock = new ReentrantLock();
	private final Collection<PhysicalDatasource> allDs;
	private final int banlance;
	private final Random random = new Random();

	public PhysicalDBPool(String name, PhysicalDatasource[] writeSources,
			Map<Integer, PhysicalDatasource[]> readSources, int balance) {
		this.hostName = name;
		this.sources = writeSources;
		this.banlance = balance;
		Iterator<Map.Entry<Integer, PhysicalDatasource[]>> entryItor = readSources
				.entrySet().iterator();
		while (entryItor.hasNext()) {
			PhysicalDatasource[] values = entryItor.next().getValue();
			if (values.length == 0) {
				entryItor.remove();
			}
		}
		this.readSources = readSources;
		this.allDs = this.genAllDataSources();
		LOGGER.info("total resouces of dataHost " + this.hostName + " is :"
				+ allDs.size());
		setDataSourceProps();
	}

	private void setDataSourceProps() {
		for (PhysicalDatasource ds : this.allDs) {
			ds.setDbPool(this);
		}
	}

	public PhysicalDatasource findDatasouce(PhysicalConnection exitsCon) {

		for (PhysicalDatasource ds : this.allDs) {
			if (ds.isReadNode() == exitsCon.isFromSlaveDB()) {
				if (ds.isMyConnection(exitsCon)) {
					return ds;
				}
			}
		}
		LOGGER.warn("can't find connection in pool " + this.hostName + " con:"
				+ exitsCon);
		return null;
	}

	public String getHostName() {
		return hostName;
	}

	public PhysicalDatasource[] getSources() {
		return sources;
	}

	public PhysicalDatasource getSource() {
		return sources[activedIndex];
	}

	public int getActivedIndex() {
		return activedIndex;
	}

	public boolean isInitSuccess() {
		return initSuccess;
	}

	public int next(int i) {
		if (checkIndex(i)) {
			return (++i == sources.length) ? 0 : i;
		} else {
			return 0;
		}
	}

	/**
	 * 鍒囨崲鏁版嵁婧�
	 */
	public boolean switchSource(int newIndex, boolean isAlarm, String reason) {
		if (!checkIndex(newIndex)) {
			return false;
		}
		final ReentrantLock lock = this.switchLock;
		lock.lock();
		try {
			int current = activedIndex;
			if (current != newIndex) {
				// write log
				LOGGER.warn(switchMessage(current, newIndex, false, reason));

				return true;
			}
		} finally {
			lock.unlock();
		}
		return false;
	}

	private String switchMessage(int current, int newIndex, boolean alarm,
			String reason) {
		StringBuilder s = new StringBuilder();
		if (alarm) {
			s.append(Alarms.DATANODE_SWITCH);
		}
		s.append("[Host=").append(hostName).append(",result=[").append(current)
				.append("->");
		s.append(newIndex).append("],reason=").append(reason).append(']');
		return s.toString();
	}

	private int loop(int i) {
		return i < sources.length ? i : (i - sources.length);
	}

	public void init(int index) {
		if (!checkIndex(index)) {
			index = 0;
		}
		int active = -1;
		for (int i = 0; i < sources.length; i++) {
			int j = loop(i + index);
			if (initSource(j, sources[j])) {
				active = j;
				break;
			}
		}
		if (checkIndex(active)) {
			activedIndex = active;
			initSuccess = true;
			LOGGER.info(getMessage(active, " init success"));
		} else {
			initSuccess = false;
			StringBuilder s = new StringBuilder();
			s.append(Alarms.DEFAULT).append(hostName).append(" init failure");
			LOGGER.error(s.toString());
		}
	}

	private boolean checkIndex(int i) {
		return i >= 0 && i < sources.length;
	}

	private String getMessage(int index, String info) {
		return new StringBuilder().append(hostName).append(':').append(index)
				.append(info).toString();
	}

	private boolean initSource(int index, PhysicalDatasource ds) {
		int initSize = ds.getConfig().getMinCon();
		LOGGER.info("init backend myqsl source ,create connections total "
				+ initSize + " for " + ds.getName());
		CopyOnWriteArrayList<PhysicalConnection> list = new CopyOnWriteArrayList<PhysicalConnection>();
		GetConnectionHandler getConHandler = new GetConnectionHandler(list,
				initSize);
		// long start=System.currentTimeMillis();
		// long timeOut=start+5000*1000L;
		for (int i = 0; i < initSize; i++) {
			try {
				ds.getConnection(getConHandler, null, null);
			} catch (Exception e) {
				LOGGER.warn(getMessage(index, " init connection error."), e);
			}
		}
		// waiting for finish
		while (!getConHandler.finished()) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		for (PhysicalConnection c : list) {
			c.release();
		}
		return !list.isEmpty();
	}

	public void doHeartbeat() {

		// 妫�煡鍐呴儴鏄惁鏈夎繛鎺ユ睜閰嶇疆淇℃伅
		if (sources == null || sources.length == 0) {
			return;
		}

		for (PhysicalDatasource source : this.allDs) {
			// 鍑嗗鎵ц蹇冭烦妫�祴
			if (source != null) {
				source.doHeartbeat();
			} else {
				StringBuilder s = new StringBuilder();
				s.append(Alarms.DEFAULT).append(hostName)
						.append(" current dataSource is null!");
				LOGGER.error(s.toString());
			}
		}
		// 璇诲簱鐨勫績璺虫娴�
		// todo
	}

	/**
	 * 绌洪棽妫�煡
	 */
	public void idleCheck() {
		for (PhysicalDatasource ds : sources) {
			if (ds != null) {
				ds.idleCheck(ds.getConfig().getIdleTimeout());
			}
		}
	}

	public void startHeartbeat() {
		for (PhysicalDatasource source : this.allDs) {
			source.startHeartbeat();
		}
	}

	public void stopHeartbeat() {
		for (PhysicalDatasource source : this.allDs) {
			source.stopHeartbeat();
		}
	}

	public void clearDataSources(String reason) {
		LOGGER.info("clear datasours of pool " + this.hostName);
		for (PhysicalDatasource source : this.allDs) {
			LOGGER.info("clear datasoure of pool  " + this.hostName + " ds:"
					+ source.getConfig());
			source.clearCons(reason);
			source.stopHeartbeat();
		}

	}

	public Collection<PhysicalDatasource> genAllDataSources() {
		LinkedList<PhysicalDatasource> allSources = new LinkedList<PhysicalDatasource>();
		for (PhysicalDatasource ds : sources) {
			if (ds != null) {
				allSources.add(ds);
			}
		}
		for (PhysicalDatasource[] dataSources : this.readSources.values()) {
			for (PhysicalDatasource ds : dataSources) {
				if (ds != null) {
					allSources.add(ds);
				}
			}
		}
		return allSources;
	}

	public Collection<PhysicalDatasource> getAllDataSources() {
		return this.allDs;
	}

	private ArrayList<PhysicalDatasource> getAllActiveSlaveSources() {
		ArrayList<PhysicalDatasource> okSources = new ArrayList<PhysicalDatasource>(
				this.allDs.size());
		for (PhysicalDatasource[] readsources : this.readSources.values()) {
			for (PhysicalDatasource read : readsources) {
				if (isAlive(read)) {
					okSources.add(read);
				}
			}
		}
		return okSources;
	}

	/**
	 * return connection for read balance
	 * 
	 * @param handler
	 * @param attachment
	 * @param database
	 * @throws Exception
	 */
	public void getRWBanlanceCon(ResponseHandler handler, Object attachment,
			String database) throws Exception {
		PhysicalDatasource theNode = null;
		ArrayList<PhysicalDatasource> okSources = null;
		switch (banlance) {
		case BALANCE_ALL_BACK: {// all read nodes and the standard by masters
			if (sources[this.activedIndex].getHeartbeat().getStatus() == DBHeartbeat.OK_STATUS) {// cur
				okSources = getAllActiveSlaveSources();
			} else {// at least one master alive
				okSources = getAllActiveRWSources(false);
			}
			theNode = randomSelect(okSources);
			break;
		}
		case BALANCE_ALL: {
			okSources = getAllActiveRWSources(true);
			theNode = randomSelect(okSources);
			break;
		}
		case BALANCE_NONE:
		default:
			// return default write data source
			theNode = this.getSource();
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("select read source " + theNode.getName()
					+ " for dataHost:" + this.getHostName());
		}
		theNode.getConnection(handler, attachment, database);
	}

	private PhysicalDatasource randomSelect(
			ArrayList<PhysicalDatasource> okSources) {
		if (okSources.isEmpty()) {
			return this.getSource();
		} else {
			int index = Math.abs(random.nextInt()) % okSources.size();
			return okSources.get(index);
		}

	}

	private boolean isAlive(PhysicalDatasource theSource) {
		return (theSource.getHeartbeat().getStatus() == DBHeartbeat.OK_STATUS);
	}

	/**
	 * return all backup write sources
	 * 
	 * @return
	 */
	private ArrayList<PhysicalDatasource> getAllActiveRWSources(
			boolean includeCurWriteNode) {
		int curActive = activedIndex;
		ArrayList<PhysicalDatasource> okSources = new ArrayList<PhysicalDatasource>(
				this.readSources.size() - 1);
		for (int i = 0; i < this.sources.length; i++) {
			if (i == curActive && includeCurWriteNode == false) {
				// not include cur active source
			} else {
				okSources.add(sources[i]);
			}
			if (isAlive(sources[i])) {// write node is active
				// check all slave nodes
				PhysicalDatasource[] allSlaves = this.readSources.get(i);
				if (allSlaves != null) {
					for (PhysicalDatasource slave : allSlaves) {
						if (isAlive(slave)) {
							okSources.add(slave);
						}
					}
				}
			}

		}
		return okSources;
	}

}
