package com.arjunren.order.entity;

import com.arjunren.order.domain.OrderStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "customer_orders")
public class CustomerOrder {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "customer_id") private AppUser customer;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private OrderStatus status = OrderStatus.PENDING;
    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2) private BigDecimal totalAmount = BigDecimal.ZERO;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true) private List<OrderItem> items = new ArrayList<>();
    @Version private long version;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt = Instant.now();
    @Column(name = "updated_at", nullable = false) private Instant updatedAt = Instant.now();

    protected CustomerOrder() {}
    public CustomerOrder(AppUser customer){this.customer=customer;}
    public void addItem(Product product,int quantity){var item=new OrderItem(this,product,quantity,product.getPrice());items.add(item);totalAmount=totalAmount.add(item.getLineTotal());}
    public void confirm(){status=OrderStatus.CONFIRMED;updatedAt=Instant.now();}
    public void fulfill(){status=OrderStatus.FULFILLED;updatedAt=Instant.now();}
    public void cancel(){status=OrderStatus.CANCELLED;updatedAt=Instant.now();}
    @PreUpdate void touch(){updatedAt=Instant.now();}
    public Long getId(){return id;} public AppUser getCustomer(){return customer;} public OrderStatus getStatus(){return status;} public BigDecimal getTotalAmount(){return totalAmount;} public List<OrderItem> getItems(){return Collections.unmodifiableList(items);} public Instant getCreatedAt(){return createdAt;}
}
