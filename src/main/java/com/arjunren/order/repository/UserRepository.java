package com.arjunren.order.repository;
import com.arjunren.order.entity.AppUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
public interface UserRepository extends JpaRepository<AppUser,Long>{Optional<AppUser> findByEmailIgnoreCase(String email);}

