package com.arjunren.order.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items", uniqueConstraints = @UniqueConstraint(columnNames = {"order_id","product_id"}))
public class OrderItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "order_id") private CustomerOrder order;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "product_id") private Product product;
    @Column(nullable = false) private int quantity;
    @Column(name = "unit_price", nullable = false, precision = 19, scale = 2) private BigDecimal unitPrice;
    @Column(name = "line_total", nullable = false, precision = 19, scale = 2) private BigDecimal lineTotal;

    protected OrderItem() {}
    OrderItem(CustomerOrder order,Product product,int quantity,BigDecimal unitPrice){this.order=order;this.product=product;this.quantity=quantity;this.unitPrice=unitPrice;this.lineTotal=unitPrice.multiply(BigDecimal.valueOf(quantity));}
    public Long getId(){return id;} public Product getProduct(){return product;} public int getQuantity(){return quantity;} public BigDecimal getUnitPrice(){return unitPrice;} public BigDecimal getLineTotal(){return lineTotal;}
}
