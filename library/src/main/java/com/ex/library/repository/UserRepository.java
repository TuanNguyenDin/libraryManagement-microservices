package com.ex.library.repository;

import com.ex.library.entity.Users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, String> {

    Users findOneByName(String name);
    Optional<Users> findByName(String name);
    Users findOneByEmail(String email);
    List<Users> findAllByIsBanned(boolean isBanned);
    List<Users> findAllByIsActive(boolean isActive);

}
