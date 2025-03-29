package com.example.demo.repository;

import com.example.demo.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o WHERE o.price >= :minPrice")
    public List<Order> findByPriceGreaterOrEqual(@Param("minPrice") double minPrice);

    @Query("SELECT o FROM Order o WHERE o.price <= :maxPrice")
    public List<Order> findByPriceLessOrEqual(@Param("maxPrice") double maxPrice);

    @Query(value = "SELECT * FROM orders "
           + "WHERE price BETWEEN :minPrice AND :maxPrice", nativeQuery = true)
    public List<Order> findByPriceBetween(
            @Param("minPrice") double minPrice,
            @Param("maxPrice") double maxPrice);
}