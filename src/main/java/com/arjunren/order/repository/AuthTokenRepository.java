package com.arjunren.order.repository;
import com.arjunren.order.entity.AuthToken;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AuthTokenRepository extends JpaRepository<AuthToken,Long>{Optional<AuthToken> findByTokenHashAndRevokedAtIsNullAndExpiresAtAfter(String hash, Instant now); Optional<AuthToken> findByTokenHash(String hash);}

