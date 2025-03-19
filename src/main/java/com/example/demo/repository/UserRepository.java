package com.example.demo.repository;

import com.example.demo.model.User;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = "orders")
    List<User> findByOrdersIsNotEmpty();

    List<User> findByOrdersIsEmpty();

    boolean existsByEmail(String email);

    @EntityGraph(attributePaths = "orders")
    @NonNull
    Optional<User> findById(@NonNull Long id);
}
