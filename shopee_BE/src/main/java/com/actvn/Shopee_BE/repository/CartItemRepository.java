package com.actvn.Shopee_BE.repository;

import com.actvn.Shopee_BE.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, String> {
    @Query("SELECT ci from CartItem ci where ci.cart.cartId = ?1 and ci.product.id = ?2" )
    CartItem findCartItemByProductIdAndCartId(String cartId, String productId);

    @Modifying
    @Transactional
    @Query("DELETE from CartItem ci where ci.cart.cartId = ?1 and ci.product.id = ?2")
    void deleteCartItemByProductIdAndCartId(String cartId, String productId);
}
