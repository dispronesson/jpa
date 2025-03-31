package com.example.demo.repository;

import com.example.demo.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o WHERE o.price >= :minPrice")
    Page<Order> findByPriceGreaterOrEqual(Pageable pageable, @Param("minPrice") double minPrice);

    @Query("SELECT o FROM Order o WHERE o.price <= :maxPrice")
    Page<Order> findByPriceLessOrEqual(Pageable pageable, @Param("maxPrice") double maxPrice);

    @Query(value = "SELECT * FROM orders "
           + "WHERE price BETWEEN :minPrice AND :maxPrice", nativeQuery = true)
    Page<Order> findByPriceBetween(
            Pageable pageable,
            @Param("minPrice") double minPrice,
            @Param("maxPrice") double maxPrice);

    @Query("SELECT o FROM Order o")
    Page<Order> findOrdersPageable(Pageable pageable);

    Page<Order> findByUserId(Long userId, Pageable pageable);
}