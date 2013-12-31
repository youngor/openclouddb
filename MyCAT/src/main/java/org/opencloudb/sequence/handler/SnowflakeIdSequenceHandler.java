package org.opencloudb.sequence.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 本地默认获取的全局ID（用于单机或者测试） <br>
 * java for base on https://github.com/twitter/snowflake
 * 
 * @author <a href="http://www.micmiu.com">Michael</a>
 * @time Create on 2013-12-22 下午4:52:25
 * @version 1.0
 */
public class SnowflakeIdSequenceHandler implements SequenceHandler {

	private final static Logger logger = LoggerFactory
			.getLogger(SequenceHandler.class);

	private final long workerId;
	private final long datacenterId;
	private final long twepoch = 1355285532520L;

	private final long workerIdBits = 5L;
	private final long datacenterIdBits = 5L;
	private final long maxWorkerId = -1L ^ -1L << this.workerIdBits;
	private final long maxDatacenterId = -1L ^ -1L << datacenterIdBits;
	private final long sequenceBits = 12L;
	private final long workerIdShift = this.sequenceBits;
	private final long datacenterIdShift = sequenceBits + workerIdBits;

	private final long timestampLeftShift = this.sequenceBits
			+ this.workerIdBits;
	private final long sequenceMask = -1L ^ -1L << this.sequenceBits;

	private long sequence = 0L;
	private long lastTimestamp = -1L;

	public SnowflakeIdSequenceHandler(long workerId, long datacenterId) {
		super();
		System.out.println("maxWorkerId = " + maxWorkerId);
		System.out.println("maxDatacenterId = " + maxDatacenterId);
		if (workerId > this.maxWorkerId || workerId < 0) {
			throw new IllegalArgumentException(String.format(
					"worker Id can't be greater than %d or less than 0",
					this.maxWorkerId));
		}

		this.workerId = workerId;
		if (datacenterId > maxDatacenterId || datacenterId < 0) {
			throw new IllegalArgumentException(String.format(
					"datacenter Id can't be greater than %d or less than 0",
					maxDatacenterId));

		}
		this.datacenterId = datacenterId;
		logger.info(String
				.format("worker starting. timestamp left shift %d, datacenter id bits %d, worker id bits %d, sequence bits %d, workerid %d",
						timestampLeftShift, datacenterIdBits, workerIdBits,
						sequenceBits, workerId));

	}

	public SnowflakeIdSequenceHandler(long workerId) {
		this(workerId, 13);
	}

	// 默认
	public SnowflakeIdSequenceHandler() {
		this(23, 13);
	}

	@Override
	public synchronized long nextId(String prefixName) {
		long timestamp = this.timeGen();
		if (timestamp < this.lastTimestamp) {
			logger.error(
					"clock is moving backwards.  Rejecting requests until %d.",
					lastTimestamp);
			throw new RuntimeException(
					String.format(
							"Clock moved backwards.  Refusing to generate id for %d milliseconds",
							(this.lastTimestamp - timestamp)));
		}
		if (this.lastTimestamp == timestamp) {
			this.sequence = this.sequence + 1 & this.sequenceMask;
			if (this.sequence == 0) {
				timestamp = this.tilNextMillis(this.lastTimestamp);
			}
		} else {
			this.sequence = 0;
		}

		this.lastTimestamp = timestamp;
		return timestamp - this.twepoch << this.timestampLeftShift
				| this.datacenterId << this.datacenterIdShift
				| this.workerId << this.workerIdShift | this.sequence;
	}

	private long tilNextMillis(long lastTimestamp) {
		long timestamp = this.timeGen();
		while (timestamp <= lastTimestamp) {
			timestamp = this.timeGen();
		}
		return timestamp;
	}

	private long timeGen() {
		return System.currentTimeMillis();
	}

	public static void main(String[] args) {
		SnowflakeIdSequenceHandler gen = new SnowflakeIdSequenceHandler(16);
		System.out.println("nextId = " + gen.nextId(null));

	}
}
