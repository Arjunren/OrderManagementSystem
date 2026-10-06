package com.arjunren.order.config;

import com.arjunren.order.domain.Role;
import com.arjunren.order.entity.*;
import com.arjunren.order.repository.*;
import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component @Profile("dev")
public class DevDataSeeder implements CommandLineRunner {
    private final UserRepository users; private final ProductRepository products; private final PasswordEncoder encoder;
    public DevDataSeeder(UserRepository users,ProductRepository products,PasswordEncoder encoder){this.users=users;this.products=products;this.encoder=encoder;}
    public void run(String...args){seed("admin@example.com","Development Admin","AdminPassword123!",Role.ADMIN);seed("staff@example.com","Development Staff","StaffPassword123!",Role.STAFF);seed("customer@example.com","Development Customer","CustomerPassword123!",Role.CUSTOMER);product("DEV-LAPTOP","Developer Laptop",new BigDecimal("1499.00"),25);product("MECH-KEYBOARD","Mechanical Keyboard",new BigDecimal("129.00"),100);}
    private AppUser seed(String email,String name,String password,Role role){return users.findByEmailIgnoreCase(email).orElseGet(()->users.save(new AppUser(email,encoder.encode(password),name,role)));}
    private void product(String sku,String name,BigDecimal price,int stock){if(!products.existsBySkuIgnoreCase(sku))products.save(new Product(sku,name,"Development seed product",price,stock));}
}
