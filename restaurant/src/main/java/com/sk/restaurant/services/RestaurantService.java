package com.sk.restaurant.services;

import com.sk.restaurant.domain.RestaurantCreateUpdateRequest;
import com.sk.restaurant.domain.entities.Restaurants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface RestaurantService {
    Restaurants createRestaurant(RestaurantCreateUpdateRequest request);
    Page<Restaurants> searchRestaurants(
            String query,
            Float minRating,
            Float  latitude,
            Float  longitude,
            Float  radius,
            Pageable pageable
    );
    Optional <Restaurants> getRestaurant(String id);
    Restaurants updateRestaurant(String id,RestaurantCreateUpdateRequest restaurantCreateUpdateRequest);
    void deleteRestaurant(String id);
}
