package com.bootcamptoprod.cache.management.service;

import com.bootcamptoprod.cache.management.Entity.Product;
import com.bootcamptoprod.cache.management.repository.ProductRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    public static final String PRODUCTS_CACHE_KEY = "products";

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Cacheable(value = "products", key = "#root.target.PRODUCTS_CACHE_KEY")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Cacheable(value = "products", key = "#productId", condition = "#productId > 0")
    public Product getProductById(long productId) {
        return productRepository.findById(productId).orElse(null);
    }

    @CachePut(value = "products", key = "#product.id")
    public Product updateProduct(Product product) {
        Product updatedProduct = productRepository.save(product);
        return updatedProduct;
    }

    @CacheEvict(value = "products", key = "#productId")
    public void deleteProduct(long productId) {
        productRepository.deleteById(productId);
    }

    @Caching(
            cacheable = {@Cacheable(value = "products", key = "#product.id")},
            evict = {@CacheEvict(value = "recentProducts", allEntries = true)}
    )
    public Product getProductDetails(Product product) {
        // Logic to fetch detailed product information
        return productRepository.findById(product.getId()).orElse(null);
    }

    @Cacheable(value = "products", key = "#productId", unless = "#result.price < 100")
    public Product getProductWithPriceCheck(long productId) {
        return productRepository.findById(productId).orElse(null);
    }

    @CacheEvict(value = "products", allEntries = true)
    public void clearProductCache() {
        System.out.println("Product cache was cleared successfully");
    }

    @CacheEvict(value = "products", allEntries = true)
    @Scheduled(fixedRateString = "43200000")
    public void clearProductsCache() {
        System.out.println("Clearing products cache");
    }
}
