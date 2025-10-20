package com.sk.restaurant.services.impl;

import com.sk.restaurant.domain.entities.Photo;
import com.sk.restaurant.services.PhotoServices;
import com.sk.restaurant.services.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PhotoServiceImpl implements PhotoServices {
     private final StorageService storageService;


    @Override
    public Photo uploadPhoto(MultipartFile file) {
        String photoId= UUID.randomUUID().toString();
        String url= storageService.store(file,photoId);
        return Photo
                .builder()
                .url(url)
                .uploadDate(LocalDateTime.now())
                .build();
    }

    @Override
    public Optional<Resource> getPhotoAsResource(String id) {
        return storageService.loadResource( id);
    }
}
