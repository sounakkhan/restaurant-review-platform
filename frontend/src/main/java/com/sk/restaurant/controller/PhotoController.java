package com.sk.restaurant.controller;

import com.sk.restaurant.domain.dtos.PhotoDto;
import com.sk.restaurant.domain.entities.Photo;
import com.sk.restaurant.mappers.PhotoMapper;
import com.sk.restaurant.services.PhotoServices;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.file.ConfigurationSource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/photos")
public class PhotoController {
    private final PhotoServices photoServices;
    private final PhotoMapper photoMapper;

    @PostMapping
    public PhotoDto uploadPhoto(@RequestParam("file")MultipartFile file){
        Photo savePhoto= photoServices.uploadPhoto(file);
        return photoMapper.toDto(savePhoto);

    }
    @GetMapping(path = "/{id:.+}")
    public ResponseEntity<Resource> getPhoto(@PathVariable String id){
       return photoServices.getPhotoAsResource(id).map(photo ->
                ResponseEntity.ok()
                        .contentType(MediaTypeFactory.getMediaType(photo).orElse(MediaType.APPLICATION_OCTET_STREAM))
                        .header(HttpHeaders.CONTENT_DISPOSITION,"inline")
                        .body(photo)
                ).orElse(ResponseEntity.notFound().build());
    }
}
