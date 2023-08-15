package com.bootcamptoprod.cache.management.repository;


import com.bootcamptoprod.cache.management.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}