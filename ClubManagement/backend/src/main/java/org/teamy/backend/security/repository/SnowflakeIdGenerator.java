package org.teamy.backend.security.repository;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SnowflakeIdGenerator {

    private final long twepoch = 1577836800000L;

    private final long datacenterIdBits = 5L;

    private final long workerIdBits = 5L;

    private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);

    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);

    private final long sequenceBits = 12L;

    private final long workerIdShift = sequenceBits;

    private final long datacenterIdShift = sequenceBits + workerIdBits;

    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    private long workerId;
    private long datacenterId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    public SnowflakeIdGenerator(long workerId, long datacenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    public synchronized long nextId() {
        long timestamp = currentTimeMillis();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = waitUntilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - twepoch) << timestampLeftShift) |
                (datacenterId << datacenterIdShift) |
                (workerId << workerIdShift) |
                sequence;
    }

    private long waitUntilNextMillis(long lastTimestamp) {
        long timestamp = currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = currentTimeMillis();
        }
        return timestamp;
    }

    private long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    public static void main(String[] args) throws InterruptedException {
        // 创建雪花ID生成器实例
        SnowflakeIdGenerator idWorker = new SnowflakeIdGenerator(1, 1);

        // 创建线程池，模拟并发环境
        int numberOfThreads = 10; // 设置并发线程数
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        // 使用Set来存储生成的ID，验证是否有重复
        Set<Long> idSet = new HashSet<>();

        // 线程安全的ID生成任务
        Runnable task = () -> {
            for (int i = 0; i < 1000; i++) { // 每个线程生成1000个ID
                long id = idWorker.nextId();
                synchronized (idSet) {
                    if (!idSet.add(id)) {
                        System.out.println("Duplicate ID found: " + id);
                    }
                }
            }
        };

        // 启动多个线程同时生成ID
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(task);
        }

        // 关闭线程池
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        // 输出生成的ID总数
        System.out.println("Total unique IDs generated: " + idSet.size());
    }
}
