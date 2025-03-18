package com.cts.myjobportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cts.myjobportal.entity.Users;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Integer> {
    Optional<Users> findByEmail(String email);
}
