package com.arjunren.order.entity;

import com.arjunren.order.exception.DomainException;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.http.HttpStatus;

@Entity
@Table(name = "products")
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true, length = 64) private String sku;
    @Column(nullable = false, length = 180) private String name;
    @Column(nullable = false, length = 1000) private String description;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal price;
    @Column(nullable = false) private int stock;
    @Column(nullable = false) private boolean active = true;
    @Version private long version;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt = Instant.now();
    @Column(name = "updated_at", nullable = false) private Instant updatedAt = Instant.now();

    protected Product() {}
    public Product(String sku, String name, String description, BigDecimal price, int stock) {
        this.sku = sku; this.name = name; this.description = description; this.price = price; this.stock = stock;
    }
    @PreUpdate void touch(){updatedAt=Instant.now();}
    public void reserve(int quantity){if(!active)throw new DomainException(HttpStatus.CONFLICT,"Product is inactive: "+sku);if(quantity>stock)throw new DomainException(HttpStatus.CONFLICT,"Insufficient stock for "+sku);stock-=quantity;}
    public void restock(int quantity){stock=Math.addExact(stock,quantity);}
    public void update(String name,String description,BigDecimal price,boolean active){this.name=name;this.description=description;this.price=price;this.active=active;}
    public Long getId(){return id;} public String getSku(){return sku;} public String getName(){return name;} public String getDescription(){return description;} public BigDecimal getPrice(){return price;} public int getStock(){return stock;} public boolean isActive(){return active;} public Instant getCreatedAt(){return createdAt;}
}
