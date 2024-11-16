package com.actvn.Shopee_BE.service.impl;

import com.actvn.Shopee_BE.entity.AppRole;
import com.actvn.Shopee_BE.entity.Role;
import com.actvn.Shopee_BE.repository.RoleRepository;
import com.actvn.Shopee_BE.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;


    @Override
    public Optional<Role> findByRoleName(AppRole appRole) {
        return roleRepository.findByRoleName( appRole);
    }
}
