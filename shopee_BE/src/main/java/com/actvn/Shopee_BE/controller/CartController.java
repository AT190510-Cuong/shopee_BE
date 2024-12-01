package com.actvn.Shopee_BE.controller;

import com.actvn.Shopee_BE.dto.response.ApiResponse;
import com.actvn.Shopee_BE.service.CartService;
import com.actvn.Shopee_BE.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    @Autowired
    private AuthUtil authUtil;
    @Autowired
    private CartService cartService;

    @PostMapping("/products/{productId}/quantity/{quantity}")
    public ResponseEntity<ApiResponse<Object>> addProductToCart(@PathVariable String productId, @PathVariable Integer quantity) {
//        CartResponse cartResponse = ;
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.addProductToCart(productId, quantity)) ;
    }

    @PutMapping("/products/{productId}/quantity/{quantity}")
    public void updateProductQuantity(@PathVariable String productId
            , @PathVariable Integer quantity) {


    }

    @DeleteMapping("{CartId}/product/{productId}")
    public void deleteProductFromCart( @PathVariable String CartId, @PathVariable String productI) {

    }

    @GetMapping("/users/cart")
    public ResponseEntity<ApiResponse<Object>> getCartById() {
        // code here
        return ResponseEntity.status(HttpStatus.OK)
                .body( cartService.getCartById()) ;
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse> getAllCart( ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body( cartService.getAllCarts()) ;
    }

    @DeleteMapping("{cartId}/product/{productId}")
    public ResponseEntity<ApiResponse<Object>> deleteCart(@PathVariable String CartId, @PathVariable String productId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body( cartService.deleteProductFromCart(CartId, productId)) ;
    }

}
