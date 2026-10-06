package com.arjunren.order.service;

import static com.arjunren.order.dto.Dtos.*;
import com.arjunren.order.domain.*;
import com.arjunren.order.entity.*;
import com.arjunren.order.exception.DomainException;
import com.arjunren.order.repository.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private final ProductRepository products;
    private final OrderRepository orders;
    public OrderService(ProductRepository products,OrderRepository orders){this.products=products;this.orders=orders;}

    @Transactional public ProductResponse createProduct(ProductCreate request,AppUser actor){requireStaff(actor);String sku=request.sku().trim().toUpperCase(Locale.ROOT);if(products.existsBySkuIgnoreCase(sku))throw conflict("SKU already exists");return ProductResponse.of(products.save(new Product(sku,request.name().trim(),request.description().trim(),request.price(),request.stock())));}
    @Transactional public ProductResponse updateProduct(Long id,ProductUpdate request,AppUser actor){requireStaff(actor);var p=products.findById(id).orElseThrow(()->notFound("Product not found"));p.update(request.name().trim(),request.description().trim(),request.price(),request.active());return ProductResponse.of(p);}
    @Transactional public ProductResponse addStock(Long id,StockAdjustment request,AppUser actor){requireStaff(actor);var p=lockProducts(List.of(id)).getFirst();p.restock(request.quantity());return ProductResponse.of(p);}
    @Transactional(readOnly=true)public PageResponse<ProductResponse> listProducts(String query,int page,int size){var result=products.searchActive(query.trim(),PageRequest.of(page,size,Sort.by("name")));return page(result.map(ProductResponse::of));}

    @Transactional public OrderResponse place(OrderCreate request,AppUser actor){if(actor.getRole()!=Role.CUSTOMER)throw forbidden();var quantities=new LinkedHashMap<Long,Integer>();for(var line:request.items()){if(quantities.putIfAbsent(line.productId(),line.quantity())!=null)throw new DomainException(HttpStatus.UNPROCESSABLE_ENTITY,"Duplicate productId in order");}var locked=lockProducts(quantities.keySet());var byId=locked.stream().collect(Collectors.toMap(Product::getId,Function.identity()));var order=new CustomerOrder(actor);for(var entry:quantities.entrySet()){var product=byId.get(entry.getKey());product.reserve(entry.getValue());order.addItem(product,entry.getValue());}return OrderResponse.of(orders.save(order));}
    @Transactional(readOnly=true)public PageResponse<OrderResponse> list(AppUser actor,int page,int size){Pageable pageable=PageRequest.of(page,size,Sort.by(Sort.Direction.DESC,"createdAt"));Page<CustomerOrder> result=actor.getRole()==Role.CUSTOMER?orders.findByCustomerId(actor.getId(),pageable):orders.findAll(pageable);return page(result.map(OrderResponse::of));}
    @Transactional(readOnly=true)public OrderResponse get(Long id,AppUser actor){var order=orders.findById(id).orElseThrow(()->notFound("Order not found"));checkVisible(order,actor);order.getItems().size();return OrderResponse.of(order);}
    @Transactional public OrderResponse confirm(Long id,AppUser actor){requireStaff(actor);var order=lockOrder(id);if(order.getStatus()!=OrderStatus.PENDING)throw conflict("Only pending orders can be confirmed");order.confirm();return OrderResponse.of(order);}
    @Transactional public OrderResponse fulfill(Long id,AppUser actor){requireStaff(actor);var order=lockOrder(id);if(order.getStatus()!=OrderStatus.CONFIRMED)throw conflict("Only confirmed orders can be fulfilled");order.fulfill();return OrderResponse.of(order);}
    @Transactional public OrderResponse cancel(Long id,AppUser actor){var order=lockOrder(id);checkVisible(order,actor);if(order.getStatus()!=OrderStatus.PENDING&&order.getStatus()!=OrderStatus.CONFIRMED)throw conflict("Order can no longer be cancelled");var quantities=order.getItems().stream().collect(Collectors.toMap(i->i.getProduct().getId(),OrderItem::getQuantity));for(var product:lockProducts(quantities.keySet()))product.restock(quantities.get(product.getId()));order.cancel();return OrderResponse.of(order);}
    @Transactional(readOnly=true)public DashboardResponse dashboard(AppUser actor){requireStaff(actor);return new DashboardResponse(orders.countByStatus(OrderStatus.PENDING),orders.countByStatus(OrderStatus.CONFIRMED),orders.countByStatus(OrderStatus.FULFILLED),orders.countByStatus(OrderStatus.CANCELLED),orders.revenue(OrderStatus.FULFILLED));}

    private List<Product> lockProducts(Collection<Long> ids){var sorted=ids.stream().sorted().toList();var locked=products.findAllByIdForUpdate(sorted);if(locked.size()!=sorted.size())throw notFound("One or more products were not found");return locked;}
    private CustomerOrder lockOrder(Long id){return orders.findByIdForUpdate(id).orElseThrow(()->notFound("Order not found"));}
    private void checkVisible(CustomerOrder order,AppUser actor){if(actor.getRole()==Role.CUSTOMER&&!order.getCustomer().getId().equals(actor.getId()))throw forbidden();}
    private void requireStaff(AppUser actor){if(actor.getRole()!=Role.ADMIN&&actor.getRole()!=Role.STAFF)throw forbidden();}
    private DomainException forbidden(){return new DomainException(HttpStatus.FORBIDDEN,"Insufficient permissions");}
    private DomainException conflict(String message){return new DomainException(HttpStatus.CONFLICT,message);}
    private DomainException notFound(String message){return new DomainException(HttpStatus.NOT_FOUND,message);}
    private <T>PageResponse<T> page(Page<T> page){return new PageResponse<>(page.getContent(),page.getNumber(),page.getSize(),page.getTotalElements(),page.getTotalPages());}
}
