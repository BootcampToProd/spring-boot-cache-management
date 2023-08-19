package com.bootcamptoprod.cache.management.Entity;

public class CacheEntry {

    private Object key;
    private Object value;

    public CacheEntry() {
    }

    public CacheEntry(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public Object getKey() {
        return key;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
