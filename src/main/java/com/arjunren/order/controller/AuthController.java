package com.arjunren.order.controller;

import static com.arjunren.order.dto.Dtos.*;
import com.arjunren.order.entity.AppUser;
import com.arjunren.order.exception.DomainException;
import com.arjunren.order.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/auth")
public class AuthController {
    private final AuthService auth; private final LoginRateLimiter limiter;
    public AuthController(AuthService auth,LoginRateLimiter limiter){this.auth=auth;this.limiter=limiter;}
    @PostMapping("/register") @ResponseStatus(HttpStatus.CREATED) Map<String,Object>register(@Valid @RequestBody RegisterRequest request){return Map.of("data",auth.register(request));}
    @PostMapping("/login") Map<String,Object>login(@Valid @RequestBody LoginRequest request,HttpServletRequest servletRequest){limiter.check(servletRequest.getRemoteAddr());return Map.of("data",auth.login(request));}
    @PostMapping("/logout") @ResponseStatus(HttpStatus.NO_CONTENT) void logout(@AuthenticationPrincipal AppUser user,HttpServletRequest request){String header=request.getHeader("Authorization");if(header==null||!header.startsWith("Bearer "))throw new DomainException(HttpStatus.UNAUTHORIZED,"Bearer token required");auth.logout(header.substring(7));}
}
