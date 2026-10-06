package com.arjunren.order.service;

import static com.arjunren.order.dto.Dtos.*;
import com.arjunren.order.domain.Role;
import com.arjunren.order.entity.*;
import com.arjunren.order.exception.DomainException;
import com.arjunren.order.repository.*;
import com.arjunren.order.security.TokenHash;
import java.security.SecureRandom;
import java.time.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository users; private final AuthTokenRepository tokens; private final PasswordEncoder encoder; private final long ttl; private final SecureRandom random=new SecureRandom();
    public AuthService(UserRepository users,AuthTokenRepository tokens,PasswordEncoder encoder,@Value("${app.security.token-ttl-minutes}")long ttl){this.users=users;this.tokens=tokens;this.encoder=encoder;this.ttl=ttl;}
    @Transactional public UserResponse register(RegisterRequest request){String email=request.email().trim().toLowerCase(Locale.ROOT);if(users.findByEmailIgnoreCase(email).isPresent())throw new DomainException(HttpStatus.CONFLICT,"Email already exists");var user=users.save(new AppUser(email,encoder.encode(request.password()),request.name().trim(),Role.CUSTOMER));return UserResponse.of(user);}
    @Transactional public TokenResponse login(LoginRequest request){var user=users.findByEmailIgnoreCase(request.email().trim()).orElse(null);String dummy="$2a$12$7ix4QV/fLm5YCYc8FjmwUOe0wIQFkwSoDvgOL1z9uQkC7B/LBkP3q";boolean ok=encoder.matches(request.password(),user==null?dummy:user.getPasswordHash());if(user==null||!user.isActive()||!ok)throw new DomainException(HttpStatus.UNAUTHORIZED,"Email or password is incorrect");byte[]bytes=new byte[32];random.nextBytes(bytes);String raw=Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);Instant expires=Instant.now().plusSeconds(ttl*60);tokens.save(new AuthToken(TokenHash.sha256(raw),user,expires));return new TokenResponse(raw,"Bearer",expires,UserResponse.of(user));}
    @Transactional public void logout(String raw){tokens.findByTokenHash(TokenHash.sha256(raw)).ifPresent(token->{if(token.getRevokedAt()==null)token.revoke();});}
}
