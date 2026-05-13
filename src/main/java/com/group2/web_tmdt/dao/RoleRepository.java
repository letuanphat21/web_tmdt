package com.group2.web_tmdt.dao;

import com.group2.web_tmdt.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByTenQuyen(String tenQuyen);
}
