package com.bootcamptoprod.cache.management.controller;

import com.bootcamptoprod.cache.management.Entity.CacheEntry;
import com.bootcamptoprod.cache.management.Entity.CacheResponse;
import com.bootcamptoprod.cache.management.Entity.CacheStatistics;
import com.bootcamptoprod.cache.management.advance.CustomConcurrentMapCache;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/cache")
public class CacheManagementController {

    private final CacheManager cacheManager;

    public CacheManagementController(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @DeleteMapping("/all/clear")
    public ResponseEntity<CacheResponse> clearAllCaches() {
        List<String> clearedCaches = new ArrayList<>();
        cacheManager.getCacheNames().forEach(cacheName -> {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
                clearedCaches.add(cacheName);
                System.out.println("Cleared cache: " + cacheName);
            }
        });

        if (!clearedCaches.isEmpty()) {
            return ResponseEntity.ok(new CacheResponse(200, "All caches cleared successfully. Caches cleared: " + clearedCaches));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CacheResponse(404, "No caches found to clear"));
        }
    }

    @DeleteMapping("/{cacheName}/clear")
    public ResponseEntity<CacheResponse> clearIndividualCache(@PathVariable String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
            System.out.println("Cleared cache: " + cacheName);
            return ResponseEntity.ok(new CacheResponse(200, cacheName + " cache cleared successfully"));
        } else {
            System.out.println("Cache not found: " + cacheName);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CacheResponse(404, cacheName + " cache not found"));
        }
    }

    @DeleteMapping("/{cacheName}/clear/entry")
    public ResponseEntity<CacheResponse> clearIndividualCacheEntry(@PathVariable String cacheName, @RequestBody CacheEntry entry) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            if (entry != null && entry.getKey() != null) {
                if (cache.evictIfPresent(entry.getKey())) {
                    System.out.println("Cleared cache entry in cache: " + cacheName);
                    return ResponseEntity.ok(new CacheResponse(200, "Cache entry cleared successfully"));
                } else {
                    System.out.println("Cache entry not found in cache: " + cacheName);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CacheResponse(404, "Cache entry not found"));
                }
            } else {
                System.out.println("Invalid cache entry request for " + cacheName + " cache. Request body should contain valid cache entry key.");
                return ResponseEntity.badRequest().body(new CacheResponse(400, "Invalid cache entry request for " + cacheName + " cache. Request body should contain valid cache entry key."));
            }
        } else {
            System.out.println("Cache not found: " + cacheName);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CacheResponse(404, cacheName + " cache not found"));
        }
    }

    @GetMapping("/all/entries")
    public ResponseEntity<Object> getAllCachesEntries() {
        List<Object> caches = new ArrayList<>();
        Collection<String> cacheNames = cacheManager.getCacheNames();

        if (cacheNames != null && !cacheNames.isEmpty()) {
            cacheNames.forEach(cacheName -> {
                Cache cache = cacheManager.getCache(cacheName);
                if (cache != null) {
                    caches.add(cache.getNativeCache());
                    System.out.println("Retrieved cache: " + cacheName);
                } else {
                    System.out.println("Cache not found: " + cacheName);
                }
            });
            return ResponseEntity.ok(caches);
        } else {
            System.out.println("No caches found.");
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/all/names")
    public ResponseEntity<Object> getAllCacheNames() {
        List<String> cacheNames = new ArrayList<>(cacheManager.getCacheNames());
        if (!cacheNames.isEmpty()) {
            return ResponseEntity.ok(cacheNames);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CacheResponse(404, "No cache names found"));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllCaches() {
        List<Object> caches = new ArrayList<>();
        Collection<String> cacheNames = cacheManager.getCacheNames();

        if (cacheNames != null && !cacheNames.isEmpty()) {
            cacheNames.forEach(cacheName -> {
                Cache cache = cacheManager.getCache(cacheName);
                if (cache != null) {
                    caches.add(cache);
                    System.out.println("Retrieved cache: " + cacheName);
                }
            });
            return ResponseEntity.ok(caches);
        } else {
            System.out.println("No caches defined.");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new CacheResponse(204, "No caches defined"));
        }
    }

    @GetMapping("/{cacheName}/entries")
    public ResponseEntity<Object> getCacheEntries(@PathVariable String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);

        if (cache != null) {
            Object nativeCache = cache.getNativeCache();
            if (nativeCache != null) {
                return ResponseEntity.ok(nativeCache);
            }
        } else {
            System.out.println("Cache not found: " + cacheName);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CacheResponse(404, cacheName + " cache not found"));
        }
        return null;
    }

    @GetMapping("/{cacheName}")
    public ResponseEntity<Object> getCache(@PathVariable String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            System.out.println("Cache found: " + cacheName);
            return ResponseEntity.ok(cache);
        } else {
            System.out.println("Cache not found: " + cacheName);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CacheResponse(404, cacheName + " cache not found"));
        }
    }

    @PostMapping("/{cacheName}/entry")
    public ResponseEntity<CacheResponse> addNewCacheEntry(@PathVariable String cacheName, @RequestBody CacheEntry cacheEntry) {
        if (cacheEntry == null || cacheEntry.getKey() == null || cacheEntry.getValue() == null) {
            System.out.println("Cache entry key or value is missing");
            return ResponseEntity.badRequest().body(new CacheResponse(400, "Cache entry key or value is missing"));
        }

        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.put(cacheEntry.getKey(), cacheEntry.getValue());
            System.out.println("Cache entry added successfully in cache: " + cacheName);
            return ResponseEntity.ok(new CacheResponse(200, "Cache entry added successfully"));
        } else {
            System.out.println("Cache not found: " + cacheName);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CacheResponse(400, cacheName + " cache not found"));
        }
    }

    @GetMapping("/{cacheName}/statistics")
    public ResponseEntity<Object> getCacheStatistics(@PathVariable String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache instanceof CustomConcurrentMapCache customConcurrentMapCache) {

            CacheStatistics cacheStatistics = new CacheStatistics();
            cacheStatistics.setEvictCount(customConcurrentMapCache.getEvictCount());
            cacheStatistics.setMissCount(customConcurrentMapCache.getMissCount());
            cacheStatistics.setHitCount(customConcurrentMapCache.getHitCount());
            cacheStatistics.setPutCount(customConcurrentMapCache.getPutCount());
            cacheStatistics.setSize(customConcurrentMapCache.getCacheSize());

            System.out.println("Cache found: " + customConcurrentMapCache.getName() + ". Statistics: " + cacheStatistics);

            return new ResponseEntity<>(cacheStatistics, HttpStatus.OK);
        } else {
            System.out.println("Cache not found: " + cacheName);
            return new ResponseEntity<>(new CacheResponse(404, cacheName + " cache not found"), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{cacheName}/hits")
    public ResponseEntity<Object> getCacheHits(@PathVariable String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache instanceof CustomConcurrentMapCache customConcurrentMapCache) {
            CacheStatistics cacheStatistics = new CacheStatistics();
            cacheStatistics.setHitCount(customConcurrentMapCache.getHitCount());

            System.out.println("Cache found: " + customConcurrentMapCache.getName() + ". Hit Count: " + customConcurrentMapCache.getHitCount());

            return new ResponseEntity<>(cacheStatistics, HttpStatus.OK);
        } else {
            System.out.println("Cache not found: " + cacheName);
            return new ResponseEntity<>(new CacheResponse(404, cacheName + " cache not found"), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{cacheName}/miss")
    public ResponseEntity<Object> getCacheMiss(@PathVariable String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache instanceof CustomConcurrentMapCache customConcurrentMapCache) {
            CacheStatistics cacheStatistics = new CacheStatistics();
            cacheStatistics.setMissCount(customConcurrentMapCache.getMissCount());

            System.out.println("Cache found: " + customConcurrentMapCache.getName() + ". Miss Count: " + customConcurrentMapCache.getMissCount());

            return new ResponseEntity<>(cacheStatistics, HttpStatus.OK);
        } else {
            System.out.println("Cache not found: " + cacheName);
            return new ResponseEntity<>(new CacheResponse(404, cacheName + " cache not found"), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{cacheName}/puts")
    public ResponseEntity<Object> getCachePuts(@PathVariable String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache instanceof CustomConcurrentMapCache customConcurrentMapCache) {
            CacheStatistics cacheStatistics = new CacheStatistics();
            cacheStatistics.setPutCount(customConcurrentMapCache.getPutCount());

            System.out.println("Cache found: " + customConcurrentMapCache.getName() + ". Put Count: " + customConcurrentMapCache.getPutCount());

            return new ResponseEntity<>(cacheStatistics, HttpStatus.OK);
        } else {
            System.out.println("Cache not found: " + cacheName);
            return new ResponseEntity<>(new CacheResponse(404, cacheName + " cache not found"), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{cacheName}/evicts")
    public ResponseEntity<Object> getCacheEvicts(@PathVariable String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache instanceof CustomConcurrentMapCache customConcurrentMapCache) {
            CacheStatistics cacheStatistics = new CacheStatistics();
            cacheStatistics.setEvictCount(customConcurrentMapCache.getEvictCount());

            System.out.println("Cache found: " + customConcurrentMapCache.getName() + ". Evict Count: " + customConcurrentMapCache.getEvictCount());

            return new ResponseEntity<>(cacheStatistics, HttpStatus.OK);
        } else {
            System.out.println("Cache not found: " + cacheName);
            return new ResponseEntity<>(new CacheResponse(404, cacheName + " cache not found"), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{cacheName}/size")
    public ResponseEntity<Object> getCacheSize(@PathVariable String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache instanceof CustomConcurrentMapCache customConcurrentMapCache) {
            CacheStatistics cacheStatistics = new CacheStatistics();
            cacheStatistics.setSize(customConcurrentMapCache.getCacheSize());

            System.out.println("Cache found: " + customConcurrentMapCache.getName() + ". Cache size: " + customConcurrentMapCache.getCacheSize());

            return new ResponseEntity<>(cacheStatistics, HttpStatus.OK);
        } else {
            System.out.println("Cache not found: " + cacheName);
            return new ResponseEntity<>(new CacheResponse(404, cacheName + " cache not found"), HttpStatus.NOT_FOUND);
        }
    }
}
