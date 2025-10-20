package com.sk.restaurant.controller;

import com.sk.restaurant.domain.RestaurantCreateUpdateRequest;
import com.sk.restaurant.domain.ReviewCreateUpdateRequest;
import com.sk.restaurant.domain.dtos.RestaurantCreateUpdateDto;
import com.sk.restaurant.domain.dtos.RestaurantDto;
import com.sk.restaurant.domain.dtos.ReviewCreateUpdateDto;
import com.sk.restaurant.domain.dtos.ReviewDto;
import com.sk.restaurant.domain.entities.Review;
import com.sk.restaurant.domain.entities.User;
import com.sk.restaurant.mappers.ReviewMapper;
import com.sk.restaurant.services.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/restaurants/{restaurantId}/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewMapper reviewMapper;
    private final ReviewService reviewService;
    @PostMapping
    public ResponseEntity<ReviewDto> createRestaurant(@PathVariable String restaurantId,
                                                      @Valid @RequestBody ReviewCreateUpdateDto review,
                                                      @AuthenticationPrincipal Jwt jwt) {
        ReviewCreateUpdateRequest reviewCreateUpdateRequest = reviewMapper.toReviewCreateUpdateRequest(review);
        User user = jwtToUser(jwt);
        Review createdReview=reviewService.createReview(user,restaurantId,reviewCreateUpdateRequest);
        return ResponseEntity.ok(reviewMapper.toDto(createdReview));

    }
    @GetMapping
    public Page<ReviewDto> listReviews(@PathVariable String restaurantId,
                                       @PageableDefault(size = 20,
                                               page = 0,
                                               sort = "datePosted",
                                               direction = Sort.Direction.DESC )Pageable pageable


                                       ){
        return reviewService.listReview(restaurantId,pageable)
                .map(reviewMapper::toDto);

    }
    @GetMapping(path = "/{reviewId}")
    public ResponseEntity<ReviewDto> createRestaurant(@PathVariable String restaurantId,
                                                      @PathVariable String reviewId
                                                      ){
      return reviewService.getReviews(restaurantId,reviewId)
                .map(reviewMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() ->ResponseEntity.noContent().build());

    }
    @PutMapping (path = "/{reviewId}")
    public ResponseEntity<ReviewDto> updateReview(@PathVariable String restaurantId,
                                                      @PathVariable String reviewId,
                                                      @Valid @RequestBody ReviewCreateUpdateDto review,
                                                  @AuthenticationPrincipal Jwt jwt
    ){
        ReviewCreateUpdateRequest reviewCreateUpdateRequest=reviewMapper.toReviewCreateUpdateRequest(review);
        User user=jwtToUser(jwt);
        Review updatedReview=reviewService.updateReview(user,restaurantId,reviewId,reviewCreateUpdateRequest);
        return ResponseEntity.ok(reviewMapper.toDto(updatedReview));
    }
    @DeleteMapping (path = "/{reviewId}")
    public ResponseEntity<ReviewDto> deleteReview(@PathVariable String restaurantId,
                                                  @PathVariable String reviewId){
        reviewService.deleteReview(restaurantId,reviewId);
        return ResponseEntity.noContent().build();

    }



    private User jwtToUser(Jwt jwt){
        return  User.builder()
                .id(jwt.getSubject())
                .username(jwt.getClaimAsString("preferred_username"))
                .givenName(jwt.getClaimAsString("given_name"))
                .familyName(jwt.getClaimAsString("family_name"))
                .build();
    }
}
