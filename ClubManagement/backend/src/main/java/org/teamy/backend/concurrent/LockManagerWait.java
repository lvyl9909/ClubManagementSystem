package org.teamy.backend.concurrent;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class LockManagerWait {

    private static LockManagerWait instance;

    // Key: lockable (clubId)
    // Value: owner (thread name)
    private ConcurrentMap<String, String> lockMap;

    public static synchronized LockManagerWait getInstance() {
        if (instance == null) {
            instance = new LockManagerWait();
        }
        return instance;
    }

    private LockManagerWait() {
        lockMap = new ConcurrentHashMap<>();
    }

    public synchronized void acquireLock(String lockable, String owner) {
        System.out.println(owner + " is trying to acquire lock for " + lockable);
        while (lockMap.containsKey(lockable)) {
            try {
                System.out.println(owner + " is waiting for lock on " + lockable);
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        lockMap.put(lockable, owner);
        System.out.println(owner + " acquired lock for " + lockable);
    }

    public synchronized void releaseLock(String lockable, String owner) {
        lockMap.remove(lockable);
        System.out.println(owner + " released lock for " + lockable);
        notifyAll();
    }
}

