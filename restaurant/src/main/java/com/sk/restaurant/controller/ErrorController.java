package com.sk.restaurant.controller;

import com.sk.restaurant.domain.dtos.ErrorDto;
import com.sk.restaurant.ecxeptions.BaseException;
import com.sk.restaurant.ecxeptions.RestaurantNotFoundException;
import com.sk.restaurant.ecxeptions.ReviewNotAllowedException;
import com.sk.restaurant.ecxeptions.StorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@ControllerAdvice
@Slf4j
public class ErrorController {
    @ExceptionHandler(ReviewNotAllowedException.class)
    public ResponseEntity<ErrorDto>handleRReviewNotAllowedException(ReviewNotAllowedException ex){
        log.error("caught ReviewNotAllowedException",ex);
        ErrorDto errorDto=ErrorDto.builder().status(HttpStatus.BAD_REQUEST.value())
                .message("specific review cannot be created or updated").build();
        return  new ResponseEntity<>(errorDto,HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(RestaurantNotFoundException.class)
    public ResponseEntity<ErrorDto>handleRestaurantNotFoundException(RestaurantNotFoundException ex){
        log.error("caught RestaurantNotFoundException",ex);
        ErrorDto errorDto=ErrorDto.builder().status(HttpStatus.NOT_FOUND.value())
                .message("specific restaurant was not found").build();
        return  new ResponseEntity<>(errorDto,HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        log.error("caught MethodArgumentNotValidException",ex);
        String errorMessage=ex
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField()+" :"+error.getDefaultMessage())
                .collect(Collectors.joining(" ,"));
        ErrorDto errorDto=ErrorDto.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(errorMessage)
                .build();
        return new ResponseEntity(errorDto,HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(StorageException.class)
    public ResponseEntity<ErrorDto> handleStorageException(StorageException ex){
        log.error("caught storageException",ex);
        ErrorDto errorDto=ErrorDto.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("unable to save or retrieve resource at this time")
                .build();
        return new ResponseEntity<>(errorDto,HttpStatus.INTERNAL_SERVER_ERROR);

    }
//    catch base unexpected exception
@ExceptionHandler(BaseException.class)
public ResponseEntity<ErrorDto> baseException(BaseException ex){
    log.error("caught storageException",ex);
    ErrorDto error=ErrorDto.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .message("an unexpected error occurred")
            .build();
    return new ResponseEntity<>(error,HttpStatus.INTERNAL_SERVER_ERROR);

}
//caught for all unexpected exception
@ExceptionHandler(Exception.class)
public ResponseEntity<ErrorDto> handleException(Exception ex){
    log.error("caught storageException",ex);
    ErrorDto error=ErrorDto.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .message("an unexpected error occurred")
            .build();
    return new ResponseEntity<>(error,HttpStatus.INTERNAL_SERVER_ERROR);

}

}
