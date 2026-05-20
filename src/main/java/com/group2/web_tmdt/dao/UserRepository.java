package com.group2.web_tmdt.dao;

import com.group2.web_tmdt.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> findByMaKichHoat(String maKichHoat);

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.active = true AND u.daKichHoat = true")
    Page<User> findAllActive(Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.active = true AND u.daKichHoat = true " +
            "AND (LOWER(CONCAT(u.hoDem, ' ', u.ten)) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<User> searchActive(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.active = false AND u.daKichHoat = true")
    Page<User> findAllHidden(Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.active = false AND u.daKichHoat = true " +
            "AND (LOWER(CONCAT(u.hoDem, ' ', u.ten)) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<User> searchHidden(@Param("keyword") String keyword, Pageable pageable);
}
