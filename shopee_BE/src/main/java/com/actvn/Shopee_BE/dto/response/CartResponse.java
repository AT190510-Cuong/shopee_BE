package com.actvn.Shopee_BE.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
    private String cartId;
    private double totalPrice = 0.0;
    private List<ProductItemResponse> products = new ArrayList<>();


}
