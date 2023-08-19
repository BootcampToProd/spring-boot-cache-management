package com.bootcamptoprod.cache.management.advance;

import org.springframework.cache.concurrent.ConcurrentMapCache;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;

public class CustomConcurrentMapCache extends ConcurrentMapCache {

    private final AtomicLong hitCount = new AtomicLong(0);
    private final AtomicLong missCount = new AtomicLong(0);
    private final AtomicLong putCount = new AtomicLong(0);
    private final AtomicLong evictCount = new AtomicLong(0);

    public CustomConcurrentMapCache(String name) {
        super(name);
    }

    @Override
    public ValueWrapper get(Object key) {
        ValueWrapper wrapper = super.get(key);
        if (wrapper != null) {
            hitCount.incrementAndGet();
        } else {
            missCount.incrementAndGet();
        }
        return wrapper;
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        T value = super.get(key, valueLoader);
        if (value != null) {
            hitCount.incrementAndGet();
        } else {
            missCount.incrementAndGet();
        }
        return value;
    }

    @Override
    public void put(Object key, Object value) {
        super.put(key, value);
        putCount.incrementAndGet();
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        ValueWrapper wrapper = super.putIfAbsent(key, value);
        if (wrapper == null) {
            putCount.incrementAndGet();
        }
        return wrapper;
    }

    @Override
    public void evict(Object key) {
        super.evict(key);
        evictCount.incrementAndGet();
    }

    @Override
    public void clear() {
        super.clear();
    }

    public long getHitCount() {
        return hitCount.get();
    }

    public long getMissCount() {
        return missCount.get();
    }

    public long getPutCount() {
        return putCount.get();
    }

    public long getEvictCount() {
        return evictCount.get();
    }

    public int getCacheSize() {
        return getNativeCache().size();
    }
}
