package com.actvn.Shopee_BE.service.impl;

import com.actvn.Shopee_BE.dto.response.ApiResponse;
import com.actvn.Shopee_BE.dto.response.CartResponse;
import com.actvn.Shopee_BE.dto.response.ProductItemResponse;
import com.actvn.Shopee_BE.entity.Cart;
import com.actvn.Shopee_BE.entity.CartItem;
import com.actvn.Shopee_BE.entity.Product;
import com.actvn.Shopee_BE.exception.APIException;
import com.actvn.Shopee_BE.exception.NotFoundException;
import com.actvn.Shopee_BE.repository.CartItemRepository;
import com.actvn.Shopee_BE.repository.CartRepository;
import com.actvn.Shopee_BE.repository.ProductRepository;
import com.actvn.Shopee_BE.service.CartService;
import com.actvn.Shopee_BE.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private AuthUtil authUtils;


    @Autowired




    @Override
    public ApiResponse addProductToCart(String productId, Integer quantity) {
        Cart cart = createCart();
        Product product = productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product not found with productId: " + productId));
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), productId);
        if (cartItem != null) {
            throw new APIException("Product with product name: " + product.getProductName() +
                    "Product already exists in cart");
        }
        if(product.getQuantity() == 0){
            throw new APIException("Product with product name: " + product.getProducts() +
                  "is not available");
        }

        if (quantity > product.getQuantity()) {
            throw new APIException("Please, make an order of product name"  + product.getQuantity() + "with quantity less than or equal to quantity: " + product.getQuantity());
        }

        CartItem newCartItem = new CartItem();
        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());
        cartItemRepository.save(newCartItem);

        product.setQuantity(quantity);
        cart.setTotalPrice(cart.getTotalPrice() + product.getSpecialPrice() * quantity);
        synchronized (this) {
            cartRepository.save(cart);
        }

        CartResponse cartResponse = mapper.map(cart, CartResponse.class);
        List<CartItem> cartItems = cart.getCartItems();
        Stream<ProductItemResponse> productItemStream = cartItems.stream().map(
                item -> {
                    ProductItemResponse map = mapper.map(item.getProduct(), ProductItemResponse.class);
                    map.setQuantity(item.getQuantity());
                    return map;
                }
        );

        cartResponse.setProducts(productItemStream.toList());
        return ApiResponse.builder()
                .status(HttpStatus.CREATED)
                .message("create cart successfully")
                .body(cartResponse)
                .build();

    }

    @Override
    public void updateProductQuantityInCarts(String productId, Integer quantity) {

    }




    @Override
    public ApiResponse<Object> deleteProductFromCart(String cartId, String productId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new NotFoundException("Cart not found cart_id" + cartId));
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);
        if (cartItem == null) {
            throw new APIException("Product not found with " + productId +  " in cart");
        }

        cart.setTotalPrice(cart.getTotalPrice() - cartItem.getProductPrice() * cartItem.getQuantity());
        cartItemRepository.deleteCartItemByProductIdAndCartId(cartId, productId);
        cartRepository.save(cart);


        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Product "+ cartItem.getProduct().getProductName() + " with product id: " + productId + "is successfully deleted from cart with cart id: " + cartId)
                .body(true)
                .build();

    }

    @Override
    public ApiResponse getCartById() {
        String email = authUtil.getEmailLogged();
        Cart cart = cartRepository.findCartByEmail(email);
        String cartId = cart.getCartId();

        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("get  cart by id " +cartId + " is successfully")
                .body(getCart(email, cartId))
                .build();
    }

    private CartResponse getCart(String email, String cartId) {
        Cart cart = cartRepository.findCartByEmailAndCartId(email, cartId);
        if (cart == null){
            throw new APIException("Cart" + cartId +  "not found");
        }
        CartResponse cartDTO = mapper.map(cart, CartResponse.class);
        List<ProductItemResponse> productDTOs = cart.getCartItems().stream()
                .map(product ->
                        mapper.map(product.getProduct(), ProductItemResponse.class))
                .toList();
        cartDTO.setProducts(productDTOs);
        return cartDTO;
    }

    @Override
    public ApiResponse<Object> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();
        if(carts.size() == 0){
            return ApiResponse.builder()
                    .status(HttpStatus.OK)
                    .message("Cart is not exist")
                    .body(null)
                    .build();
        }

        List<CartResponse> cartDTOs = carts.stream().map(item -> {
            CartResponse cartDTO = mapper.map(item, CartResponse.class);
            List<ProductItemResponse> productDTOs = item.getCartItems().stream()
                    .map(product ->
                            mapper.map(product.getProduct(), ProductItemResponse.class))
                    .collect(Collectors.toList());
            cartDTO.setProducts(productDTOs);
            return cartDTO;
        }).collect(Collectors.toList());

        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("get all cart successfully")
                .body(cartDTOs)
                .build();
    }

    private Cart createCart() {
        Cart userCart = cartRepository.findCartByEmail(authUtil.getEmailLogged());
        if (userCart != null) {
            return userCart;
        }

        Cart cart = new Cart();

        cart.setUser(authUtil.getUserNameLogged());
        cart.setTotalPrice(0.00);
        cart.setUser(authUtil.getUserNameLogged());
        Cart savedCart = cartRepository.save(cart);
        return savedCart;
    }

    @Override
    public ApiResponse<Object> updateProductInCarts(String productId, Integer quantity) {
        String email = authUtils.getEmailLogged();
        Cart userCart = cartRepository.findCartByEmail(email);
        String cartId = userCart.getCartId();

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new APIException("Cart " + cartId + " not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new APIException("Product " + productId + " not found"));

        if (product.getQuantity() == 0) {
            throw new APIException("Product " + product.getProductName() + " is sold out");
        }


        if (product.getQuantity() < quantity) {
            throw new APIException("Product" + product.getProductName() + "is less or equal than" + product.getQuantity());
        }

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);
        if (cartItem == null) {
            throw new APIException("Product " + product.getProductName() + " not found in cart " + cartId);
        }

        int newQuantity = cartItem.getQuantity() + quantity;
        if (newQuantity <= 0) {
            throw new APIException("The resulting quantity cannot be negative");
        }
        if (newQuantity == 0){
           deleteProductFromCart(cartId, productId);
        }else {
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setQuantity(newQuantity);
            cartItem.setDiscount(product.getDiscount());
            cart.setTotalPrice(cart.getTotalPrice() + product.getSpecialPrice() * quantity);
            cartRepository.save(cart);
        }

        CartItem updateItem = cartItemRepository.save(cartItem);
        if (updateItem.getQuantity() == 0) {
            cartItemRepository.deleteById(updateItem.getCartItemId());
        }

        CartResponse cartDTO = mapper.map(cart, CartResponse.class);
        List<CartItem> cartItems = cart.getCartItems();
        List<ProductItemResponse> productItemDTOs = cartItems.stream()
                .map(item -> {
                    ProductItemResponse dto = mapper.map(item.getProduct(), ProductItemResponse.class);
                    dto.setQuantity(item.getQuantity());
                    return dto;
                }).toList();

        cartDTO.setProducts(productItemDTOs);

        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("updated")
                .body(cartDTO)
                .build();
    }
}
