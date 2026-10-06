package com.arjunren.order.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "auth_tokens")
public class AuthToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name="token_hash", nullable=false, unique=true, length=64) private String tokenHash;
    @ManyToOne(optional=false, fetch=FetchType.EAGER) @JoinColumn(name="user_id") private AppUser user;
    @Column(name="expires_at", nullable=false) private Instant expiresAt;
    @Column(name="revoked_at") private Instant revokedAt;
    @Column(name="created_at", nullable=false, updatable=false) private Instant createdAt=Instant.now();
    protected AuthToken() {}
    public AuthToken(String tokenHash, AppUser user, Instant expiresAt){this.tokenHash=tokenHash;this.user=user;this.expiresAt=expiresAt;}
    public AppUser getUser(){return user;} public Instant getExpiresAt(){return expiresAt;} public Instant getRevokedAt(){return revokedAt;} public void revoke(){this.revokedAt=Instant.now();}
}

