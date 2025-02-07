package com.ex.library.repository;

import com.ex.library.entity.VerifyCodeChangeEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerifyCodeChangeEmailRepository extends JpaRepository<VerifyCodeChangeEmail,String> {
    Optional<VerifyCodeChangeEmail> findOneByCode(String code);
}
