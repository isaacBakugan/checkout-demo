package com.farmatodo.checkout.tokenization;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardTokenRepository extends JpaRepository<CardToken, Long> {
    Optional<CardToken> findByToken(String token);
}