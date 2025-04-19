package com.example.demo.repository;

import com.example.demo.model.User;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = "orders")
    @Query("SELECT u FROM User u WHERE u.orders IS NOT EMPTY ORDER BY u.id ASC")
    List<User> findByOrdersIsNotEmpty();

    @Query("SELECT u FROM User u WHERE u.orders IS EMPTY ORDER BY u.id ASC")
    List<User> findByOrdersIsEmpty();

    @Query("SELECT u FROM User u")
    Page<User> findUsersPageable(Pageable pageable);

    boolean existsByEmail(String email);

    @EntityGraph(attributePaths = "orders")
    @NonNull
    Optional<User> findById(@NonNull Long id);

    List<User> findAllByEmailIn(List<String> emails);
}
