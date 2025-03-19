package com.example.demo.repository;

import com.example.demo.model.Order;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = "user")
    @NonNull
    List<Order> findAll();

    @EntityGraph(attributePaths = "user")
    @NonNull
    Optional<Order> findById(@NonNull Long id);
}