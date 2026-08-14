package com.app.ecom.controller;

import com.app.ecom.dto.CartItemRequest;
import com.app.ecom.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping
    public ResponseEntity<String> addToCart(@RequestHeader("X-User-ID") String userId, @RequestBody CartItemRequest request){
        boolean saved = cartService.addToCart(userId, request);
        if(!saved) {
            return ResponseEntity.badRequest().body("Product out of stock or User not found");
        }else {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
    }

}
