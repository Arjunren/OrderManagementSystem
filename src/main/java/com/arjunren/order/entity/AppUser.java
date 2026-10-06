package com.arjunren.order.entity;

import com.arjunren.order.domain.Role;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "users")
public class AppUser {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true, length = 255) private String email;
    @Column(name = "password_hash", nullable = false, length = 255) private String passwordHash;
    @Column(nullable = false, length = 120) private String name;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private Role role;
    @Column(nullable = false) private boolean active = true;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt = Instant.now();
    @Column(name = "updated_at", nullable = false) private Instant updatedAt = Instant.now();
    protected AppUser() {}
    public AppUser(String email, String passwordHash, String name, Role role) { this.email=email; this.passwordHash=passwordHash; this.name=name; this.role=role; }
    @PreUpdate void updateTimestamp() { updatedAt = Instant.now(); }
    public Long getId(){return id;} public String getEmail(){return email;} public String getPasswordHash(){return passwordHash;} public String getName(){return name;} public Role getRole(){return role;} public boolean isActive(){return active;} public Instant getCreatedAt(){return createdAt;}
}

