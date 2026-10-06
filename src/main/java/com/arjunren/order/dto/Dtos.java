package com.arjunren.order.dto;

import com.arjunren.order.domain.*;
import com.arjunren.order.entity.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public final class Dtos {
    private Dtos() {}
    public record LoginRequest(@Email @NotBlank String email,@NotBlank String password){}
    public record RegisterRequest(@Email @NotBlank String email,@Size(min=10,max=72) String password,@NotBlank @Size(max=120)String name){}
    public record UserResponse(Long id,String email,String name,Role role){public static UserResponse of(AppUser u){return new UserResponse(u.getId(),u.getEmail(),u.getName(),u.getRole());}}
    public record TokenResponse(String accessToken,String tokenType,Instant expiresAt,UserResponse user){}
    public record ProductCreate(@NotBlank @Size(max=64)String sku,@NotBlank @Size(max=180)String name,@NotNull @Size(max=1000)String description,@NotNull @DecimalMin("0.01") @Digits(integer=17,fraction=2)BigDecimal price,@Min(0)int stock){}
    public record ProductUpdate(@NotBlank @Size(max=180)String name,@NotNull @Size(max=1000)String description,@NotNull @DecimalMin("0.01") @Digits(integer=17,fraction=2)BigDecimal price,boolean active){}
    public record StockAdjustment(@Min(1)int quantity){}
    public record ProductResponse(Long id,String sku,String name,String description,BigDecimal price,int stock,boolean active){public static ProductResponse of(Product p){return new ProductResponse(p.getId(),p.getSku(),p.getName(),p.getDescription(),p.getPrice(),p.getStock(),p.isActive());}}
    public record OrderLineRequest(@NotNull Long productId,@Min(1) @Max(10000)int quantity){}
    public record OrderCreate(@NotEmpty @Size(max=100)List<@Valid OrderLineRequest> items){}
    public record OrderItemResponse(Long productId,String sku,String name,int quantity,BigDecimal unitPrice,BigDecimal lineTotal){public static OrderItemResponse of(OrderItem i){return new OrderItemResponse(i.getProduct().getId(),i.getProduct().getSku(),i.getProduct().getName(),i.getQuantity(),i.getUnitPrice(),i.getLineTotal());}}
    public record OrderResponse(Long id,UserResponse customer,OrderStatus status,BigDecimal totalAmount,List<OrderItemResponse>items,Instant createdAt){public static OrderResponse of(CustomerOrder o){return new OrderResponse(o.getId(),UserResponse.of(o.getCustomer()),o.getStatus(),o.getTotalAmount(),o.getItems().stream().map(OrderItemResponse::of).toList(),o.getCreatedAt());}}
    public record PageResponse<T>(List<T>content,int page,int size,long totalElements,int totalPages){}
    public record DashboardResponse(long pending,long confirmed,long fulfilled,long cancelled,BigDecimal fulfilledRevenue){}
}
