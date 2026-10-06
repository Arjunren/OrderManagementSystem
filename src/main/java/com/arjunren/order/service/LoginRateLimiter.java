package com.arjunren.order.service;
import com.arjunren.order.exception.DomainException; import java.time.*; import java.util.*; import java.util.concurrent.*; import org.springframework.http.HttpStatus; import org.springframework.stereotype.Component;
@Component public class LoginRateLimiter{private final Map<String,Deque<Instant>> attempts=new ConcurrentHashMap<>();public void check(String key){var now=Instant.now();var q=attempts.computeIfAbsent(key,k->new ArrayDeque<>());synchronized(q){while(!q.isEmpty()&&q.peek().isBefore(now.minusSeconds(60)))q.remove();if(q.size()>=10)throw new DomainException(HttpStatus.TOO_MANY_REQUESTS,"Too many login attempts");q.add(now);}}}

