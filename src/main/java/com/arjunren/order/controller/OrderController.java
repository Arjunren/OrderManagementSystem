package com.arjunren.order.controller;

import static com.arjunren.order.dto.Dtos.*;
import com.arjunren.order.entity.AppUser;
import com.arjunren.order.service.OrderService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api")
public class OrderController {
    private final OrderService service;
    public OrderController(OrderService service){this.service=service;}
    @PostMapping("/products") @ResponseStatus(HttpStatus.CREATED) Map<String,Object>createProduct(@Valid @RequestBody ProductCreate request,@AuthenticationPrincipal AppUser actor){return Map.of("data",service.createProduct(request,actor));}
    @PatchMapping("/products/{id}") Map<String,Object>updateProduct(@PathVariable Long id,@Valid @RequestBody ProductUpdate request,@AuthenticationPrincipal AppUser actor){return Map.of("data",service.updateProduct(id,request,actor));}
    @PostMapping("/products/{id}/stock") Map<String,Object>addStock(@PathVariable Long id,@Valid @RequestBody StockAdjustment request,@AuthenticationPrincipal AppUser actor){return Map.of("data",service.addStock(id,request,actor));}
    @GetMapping("/products") Map<String,Object>products(@RequestParam(defaultValue="")String q,@RequestParam(defaultValue="0")int page,@RequestParam(defaultValue="20")int size){return Map.of("data",service.listProducts(q,page,bounded(size)));}
    @PostMapping("/orders") @ResponseStatus(HttpStatus.CREATED) Map<String,Object>place(@Valid @RequestBody OrderCreate request,@AuthenticationPrincipal AppUser actor){return Map.of("data",service.place(request,actor));}
    @GetMapping("/orders") Map<String,Object>orders(@AuthenticationPrincipal AppUser actor,@RequestParam(defaultValue="0")int page,@RequestParam(defaultValue="20")int size){return Map.of("data",service.list(actor,page,bounded(size)));}
    @GetMapping("/orders/{id}") Map<String,Object>order(@PathVariable Long id,@AuthenticationPrincipal AppUser actor){return Map.of("data",service.get(id,actor));}
    @PatchMapping("/orders/{id}/confirm") Map<String,Object>confirm(@PathVariable Long id,@AuthenticationPrincipal AppUser actor){return Map.of("data",service.confirm(id,actor));}
    @PatchMapping("/orders/{id}/fulfill") Map<String,Object>fulfill(@PathVariable Long id,@AuthenticationPrincipal AppUser actor){return Map.of("data",service.fulfill(id,actor));}
    @PatchMapping("/orders/{id}/cancel") Map<String,Object>cancel(@PathVariable Long id,@AuthenticationPrincipal AppUser actor){return Map.of("data",service.cancel(id,actor));}
    @GetMapping("/dashboard") Map<String,Object>dashboard(@AuthenticationPrincipal AppUser actor){return Map.of("data",service.dashboard(actor));}
    private int bounded(int size){return Math.min(Math.max(size,1),100);}
}
