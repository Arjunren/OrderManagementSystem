package com.arjunren.order.security;

import com.arjunren.order.repository.AuthTokenRepository;
import jakarta.servlet.*; import jakarta.servlet.http.*;
import java.io.IOException; import java.time.Instant; import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class BearerTokenFilter extends OncePerRequestFilter {
    private final AuthTokenRepository tokens;
    public BearerTokenFilter(AuthTokenRepository tokens){this.tokens=tokens;}
    @Override protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain chain)throws ServletException,IOException{
        String header=request.getHeader("Authorization");
        if(header!=null&&header.startsWith("Bearer ")&&SecurityContextHolder.getContext().getAuthentication()==null){
            tokens.findByTokenHashAndRevokedAtIsNullAndExpiresAtAfter(TokenHash.sha256(header.substring(7)),Instant.now()).filter(t->t.getUser().isActive()).ifPresent(t->{var u=t.getUser();var auth=new UsernamePasswordAuthenticationToken(u,null,List.of(new SimpleGrantedAuthority("ROLE_"+u.getRole().name())));SecurityContextHolder.getContext().setAuthentication(auth);});
        }
        chain.doFilter(request,response);
    }
}
