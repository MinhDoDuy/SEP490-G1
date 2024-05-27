package com.ffood.g1.repository;

import com.ffood.g1.entity.ResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResetTokenRepository extends JpaRepository<ResetToken, Long> {
    ResetToken findByToken(String token);
}
