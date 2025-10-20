package com.sk.restaurant.services.impl;

import com.sk.restaurant.domain.ReviewCreateUpdateRequest;
import com.sk.restaurant.domain.entities.Photo;
import com.sk.restaurant.domain.entities.Restaurants;
import com.sk.restaurant.domain.entities.Review;
import com.sk.restaurant.domain.entities.User;
import com.sk.restaurant.ecxeptions.RestaurantNotFoundException;
import com.sk.restaurant.ecxeptions.ReviewNotAllowedException;
import com.sk.restaurant.repositories.RestaurantRepository;
import com.sk.restaurant.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final RestaurantRepository restaurantRepository;
    @Override
    public Review createReview(User author, String restaurantId, ReviewCreateUpdateRequest review) {
        Restaurants restaurants= getRestaurantOrThrow(restaurantId);
        boolean hsaExistingReview=restaurants.getReviews().stream()
                .anyMatch(r ->r.getWrittenBy().getId().equals(author.getId()));
        if(hsaExistingReview){
            throw new ReviewNotAllowedException("uer already review for this restaurants");
        }
        LocalDateTime now = (LocalDateTime.now());
        List<Photo> photos=review.getPhotoIds().stream().map(url ->{
            return Photo.builder()
                    .url(url)
                    .uploadDate(now).build();
        }).toList();
        String reviewId=UUID.randomUUID().toString();
        Review createReview= Review.builder()
                .id(reviewId)
                .content(review.getContent())
                .rating(review.getRating())
                .photos(photos)
                .datePosted(now).
                lastPosted(now)
                .writtenBy(author)
                .build();
        restaurants.getReviews().add(createReview);
        updateRestaurantAverageRating(restaurants);
        Restaurants saveRestaurant= restaurantRepository.save(restaurants);
        return getReviewFromRestaurant(reviewId, saveRestaurant)
                .orElseThrow(()-> new RuntimeException("error retrieving create review"));
    }

    @Override
    public Page<Review> listReview(String restaurantId, Pageable pageable) {
        Restaurants restaurants= getRestaurantOrThrow(restaurantId);
        List<Review> reviews=restaurants.getReviews();
        Sort sort=pageable.getSort();
        if(sort.isSorted()){
            Sort.Order order =sort.iterator().next();
            String property= order.getProperty();
            boolean isAscending=order.getDirection().isAscending();
            Comparator<Review> comparator=switch (property){
                case "datePosted" -> Comparator.comparing(Review::getDatePosted);
                case "rating" -> Comparator.comparing(Review::getRating);
                default -> Comparator.comparing(Review::getDatePosted);
            };

        }else{
            reviews.sort(Comparator.comparing(Review::getDatePosted).reversed());
        }
        int start= (int) pageable.getOffset();
        if(start>= reviews.size()){
            return new PageImpl<>(Collections.emptyList(),pageable, reviews.size());

        }
        int end=Math.min(start+ pageable.getPageSize(), reviews.size());
        return new PageImpl<>(reviews.subList(start,end),pageable, reviews.size());


    }

    @Override
    public Optional<Review> getReviews(String restaurantId, String reviewId) {
        Restaurants restaurants=getRestaurantOrThrow(restaurantId);
      return getReviewFromRestaurant(reviewId, restaurants);
    }

    private static Optional<Review> getReviewFromRestaurant(String reviewId, Restaurants restaurants) {
        return restaurants.getReviews()
                .stream().
                filter(r -> reviewId.equals(r.getId()))
                .findFirst();
    }

    @Override
    public Review updateReview(User author, String restaurantId, String reviewId, ReviewCreateUpdateRequest review) {
        Restaurants restaurants=getRestaurantOrThrow(restaurantId);
        String authorId=author.getId();
     Review exsistingingReview=   getReviewFromRestaurant(reviewId,restaurants)
                .orElseThrow(() -> new ReviewNotAllowedException("review not exist"));
     if(!authorId.equals(exsistingingReview.getWrittenBy().getId())){
         throw new ReviewNotAllowedException("cannot edit another user's review");

     }
        if(LocalDateTime.now().isAfter(exsistingingReview.getDatePosted().plusHours(48))){
            throw new ReviewNotAllowedException("review no longer be edited");
        }
        exsistingingReview.setContent(review.getContent());
        exsistingingReview.setRating(review.getRating());
        exsistingingReview.setDatePosted(LocalDateTime.now());
        exsistingingReview.setPhotos(review.getPhotoIds().stream()
                .map(photoId -> Photo.builder()
                        .url(photoId)
                        .uploadDate(LocalDateTime.now())
                        .build()).toList());
        updateRestaurantAverageRating(restaurants);
        List<Review> updatedReviews=restaurants.getReviews().stream()
                .filter(r -> !reviewId.equals(r.getId()))
                .collect(Collectors.toList());
        updatedReviews.add(exsistingingReview);
        restaurantRepository.save(restaurants);
        return exsistingingReview;
    }

    @Override
    public void deleteReview(String restaurantId, String reviewId) {
        Restaurants restaurants=getRestaurantOrThrow(restaurantId);
        List<Review> filteredReviews=restaurants.getReviews().stream()
                .filter(r ->reviewId.equals(r.getId())).toList();
        restaurants.setReviews(filteredReviews);
        updateRestaurantAverageRating(restaurants);
        restaurantRepository.save(restaurants);

    }

    private Restaurants getRestaurantOrThrow(String restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException("restaurant with is id not found" + restaurantId));
    }

    private void updateRestaurantAverageRating(Restaurants restaurants){
        List<Review> reviews=restaurants.getReviews();
        if(reviews.isEmpty()){
            restaurants.setAverageRating(0.0f);
        }
        else{
            double averageRating=reviews.stream().mapToDouble(Review::getRating)
                    .average()
                    .orElse(0.0);
            restaurants.setAverageRating((float)averageRating);
        }
    }

}
