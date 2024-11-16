package com.actvn.Shopee_BE.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    String uploadImage(String path, MultipartFile file);

}
