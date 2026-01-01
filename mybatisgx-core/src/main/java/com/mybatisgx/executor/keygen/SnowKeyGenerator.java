package com.mybatisgx.executor.keygen;

import com.mybatisgx.exception.SnowGenerateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 一句话描述
 * @author 薛承城
 * @date 2025/12/3 9:30
 */
public class SnowKeyGenerator implements KeyGenerator<Long> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SnowKeyGenerator.class);

    /**
     * Bits allocate
     */
    protected int timestampBits = 31;
    protected int workerIdBits = 5;
    protected int sequenceBits = 16;

    /**
     * Customer epoch, unit as second. For example 2016-05-20 (ms: 1463673600000)
     */
    protected String epochStr = "2020-01-01";
    protected long epochSeconds = TimeUnit.SECONDS.toSeconds(1577808000L);

    protected long workerId;
    /**
     * Volatile fields caused by nextId()
     */
    protected long sequence = 0L;
    protected long lastSecond = -1L;
    private final long maxSequence;
    private final long maxDeltaSeconds;
    private final int timestampShift;
    private final int workerIdShift;

    public SnowKeyGenerator() {
        this.maxSequence = ~(-1L << sequenceBits);
        this.maxDeltaSeconds = ~(-1L << timestampBits);
        this.timestampShift = workerIdBits + sequenceBits;
        this.workerIdShift = sequenceBits;
    }

    public Long get() throws SnowGenerateException {
        try {
            return nextId();
        } catch (Exception e) {
            LOGGER.error("Generate unique id exception. ", e);
            throw new SnowGenerateException(e);
        }
    }

    /**
     * Get UID
     *
     * @return UID
     * @throws SnowGenerateException in the case: Clock moved backwards; Exceeds the max timestamp
     */
    protected synchronized long nextId() {
        long currentSecond = getCurrentSecond();

        // Clock moved backwards, refuse to generate uid
        if (currentSecond < lastSecond) {
            long refusedSeconds = lastSecond - currentSecond;
            throw new SnowGenerateException("Clock moved backwards. Refusing for %d seconds", refusedSeconds);
        }

        // At the same second, increase sequence
        if (currentSecond == lastSecond) {
            sequence = (sequence + 1) & this.maxSequence;
            // Exceed the max sequence, we wait the next second to generate uid
            if (sequence == 0) {
                currentSecond = getNextSecond(lastSecond);
            }

            // At the different second, sequence restart from zero
        } else {
            sequence = 0L;
        }

        lastSecond = currentSecond;

        // Allocate bits for UID
        return this.allocate(currentSecond - epochSeconds, workerId, sequence);
    }

    /**
     * Get next millisecond
     */
    private long getNextSecond(long lastTimestamp) {
        long timestamp = getCurrentSecond();
        while (timestamp <= lastTimestamp) {
            timestamp = getCurrentSecond();
        }
        return timestamp;
    }

    /**
     * Get current second
     */
    private long getCurrentSecond() {
        long currentSecond = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        if (currentSecond - epochSeconds > this.maxDeltaSeconds) {
            throw new SnowGenerateException("Timestamp bits is exhausted. Refusing UID generate. Now: " + currentSecond);
        }
        return currentSecond;
    }

    private long allocate(long deltaSeconds, long workerId, long sequence) {
        return (deltaSeconds << timestampShift) | (workerId << workerIdShift) | sequence;
    }
}
