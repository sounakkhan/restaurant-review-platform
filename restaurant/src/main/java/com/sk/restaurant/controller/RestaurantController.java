package com.sk.restaurant.controller;

import com.sk.restaurant.domain.RestaurantCreateUpdateRequest;
import com.sk.restaurant.domain.dtos.RestaurantCreateUpdateDto;
import com.sk.restaurant.domain.dtos.RestaurantDto;
import com.sk.restaurant.domain.dtos.RestaurantSummaryDto;
import com.sk.restaurant.domain.entities.Restaurants;
import com.sk.restaurant.mappers.RestaurantMapper;
import com.sk.restaurant.services.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {
    private final RestaurantService restaurantService;
    private final RestaurantMapper restaurantMapper;
    @PostMapping
    public ResponseEntity<RestaurantDto> createRestaurant(@Valid @RequestBody RestaurantCreateUpdateDto request){
        RestaurantCreateUpdateRequest restaurantCreateUpdateRequest=restaurantMapper
                .toRestaurantCreateUpdateRequest(request);
        Restaurants restaurants=restaurantService.createRestaurant(restaurantCreateUpdateRequest);
        RestaurantDto   createRestaurantDto=restaurantMapper.toRestaurantDto(restaurants);
        return  ResponseEntity.ok(createRestaurantDto);
    }
    @GetMapping
    public Page<RestaurantSummaryDto> searchRestaurant(
           @RequestParam(required = false) String q,
           @RequestParam(required = false) Float minRating,
           @RequestParam(required = false)  Float latitude,
           @RequestParam(required = false)  Float longitude,
           @RequestParam(required = false)  Float radius,
           @RequestParam(required = false,defaultValue = "0") Integer page,
           @RequestParam(required = false,defaultValue = "20") Integer size
    ){

      Page<Restaurants> searchResults= restaurantService.searchRestaurants(q,minRating,latitude,longitude,radius, PageRequest.of(page-1,size));
      return searchResults.map(restaurantMapper::toSummaryDto);

    }
    @GetMapping(path = "/{restaurant_id}")
    public ResponseEntity<RestaurantDto> getRestaurant(@PathVariable("restaurant_id") String restaurantId) {
        return restaurantService.getRestaurant(restaurantId)
                .map(restaurant -> ResponseEntity.ok(restaurantMapper.toRestaurantDto(restaurant)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping(path = "/{restaurant_id}")
    public ResponseEntity<RestaurantDto> updateRestaurant(
            @PathVariable("restaurant_id") String restaurantId,
            @Valid @RequestBody RestaurantCreateUpdateDto requestDto
    ) {
        RestaurantCreateUpdateRequest request = restaurantMapper
                .toRestaurantCreateUpdateRequest(requestDto);

        Restaurants updatedRestaurant = restaurantService.updateRestaurant(restaurantId, request);

        return ResponseEntity.ok(restaurantMapper.toRestaurantDto(updatedRestaurant));
    }
    @DeleteMapping(path = "/{restaurant_id}")
    public ResponseEntity<RestaurantDto> updateRestaurant(@PathVariable("restaurant_id") String restaurantId){
        restaurantService.deleteRestaurant(restaurantId);
        return ResponseEntity.noContent().build();
    }
}
