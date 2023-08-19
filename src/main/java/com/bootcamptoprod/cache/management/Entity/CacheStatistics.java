package com.bootcamptoprod.cache.management.Entity;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CacheStatistics {

    private Long hitCount;
    private Long missCount;
    private Long putCount;
    private Long evictCount;
    private Integer size;

    public Long getHitCount() {
        return hitCount;
    }

    public void setHitCount(Long hitCount) {
        this.hitCount = hitCount;
    }

    public Long getMissCount() {
        return missCount;
    }

    public void setMissCount(Long missCount) {
        this.missCount = missCount;
    }

    public Long getPutCount() {
        return putCount;
    }

    public void setPutCount(Long putCount) {
        this.putCount = putCount;
    }

    public Long getEvictCount() {
        return evictCount;
    }

    public void setEvictCount(Long evictCount) {
        this.evictCount = evictCount;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "CacheStatistics{" +
                "hitCount=" + hitCount +
                ", missCount=" + missCount +
                ", putCount=" + putCount +
                ", evictCount=" + evictCount +
                ", size=" + size +
                '}';
    }
}
