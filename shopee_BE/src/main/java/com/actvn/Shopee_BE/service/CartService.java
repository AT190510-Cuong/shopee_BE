package com.actvn.Shopee_BE.service;

import com.actvn.Shopee_BE.dto.response.ApiResponse;

public interface CartService {
    ApiResponse<Object> addProductToCart(String productId, Integer quantity);
    void updateProductQuantityInCarts(String productId, Integer quantity);

    ApiResponse<Object> updateProductInCarts(String productId, Integer quantity);
    ApiResponse<Object> deleteProductFromCart(String CartId, String productId);
    ApiResponse<Object> getCartById();
    ApiResponse<Object> getAllCarts();
}
