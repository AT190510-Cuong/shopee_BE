package com.actvn.Shopee_BE.service;

import com.actvn.Shopee_BE.entity.AppRole;
import com.actvn.Shopee_BE.entity.Role;

import java.util.Optional;

public interface RoleService {
    Optional<Role> findByRoleName(AppRole roleName);
}
