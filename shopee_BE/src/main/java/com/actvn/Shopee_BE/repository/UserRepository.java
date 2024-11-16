package com.actvn.Shopee_BE.repository;

import com.actvn.Shopee_BE.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String>{

    boolean existsByUserName(String userName);

    boolean existsByEmail(String email);

}