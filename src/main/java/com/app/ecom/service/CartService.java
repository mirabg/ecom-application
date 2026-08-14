package com.app.ecom.service;

import com.app.ecom.dto.CartItemRequest;
import com.app.ecom.model.CartItem;
import com.app.ecom.model.Product;
import com.app.ecom.model.User;
import com.app.ecom.repository.CartItemRepository;
import com.app.ecom.repository.ProductRepository;
import com.app.ecom.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class CartService {
    private ProductRepository productRepository;
    private UserRepository userRepository;
    private CartItemRepository cartItemRepository;

    public CartService(ProductRepository productRepository, UserRepository userRepository, CartItemRepository cartItemRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.cartItemRepository = cartItemRepository;
    }

    public boolean addToCart(String userId, CartItemRequest request) {
        Optional<Product> productOpt = productRepository.findById(request.getProductId());
        if (productOpt.isEmpty()) {
            return false;
        }
        Product product = productOpt.get();
        if(product.getStockQuantity() < request.getQuantity()) {
            return false;
        }
        Optional<User> userOpt = userRepository.findById(Long.parseLong(userId));
        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();
        CartItem existingCartItem = cartItemRepository.findByUserAndProduct(user, product);

        if(existingCartItem != null) {
            existingCartItem.setQuantity(existingCartItem.getQuantity() + request.getQuantity());
            existingCartItem.setPrice(existingCartItem.getPrice().add(product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()))));
            cartItemRepository.save(existingCartItem);
        }else{
            //create new cart item
            CartItem cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());
            cartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
            cartItemRepository.save(cartItem);
        }

        return true;
    }
}
