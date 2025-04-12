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
    @Query("SELECT o FROM Order o WHERE o.user.name = :name")
    Page<Order> findByUserName(@Param("name") String name, Pageable pageable);

    @Query(
            value = "SELECT o.id, o.description, o.price, o.user_id "
                    + "FROM orders o INNER JOIN users u ON o.user_id = u.id "
                    + "WHERE u.email = :email",
            nativeQuery = true
    )
    Page<Order> findByUserEmail(@Param("email") String email, Pageable pageable);

    @Query("SELECT o FROM Order o")
    Page<Order> findOrdersPageable(Pageable pageable);

    Page<Order> findByUserId(Long userId, Pageable pageable);
}