package com.group2.web_tmdt.dao;

import com.group2.web_tmdt.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    java.util.Optional<com.group2.web_tmdt.entity.User> findByMaKichHoat(String maKichHoat);
}
