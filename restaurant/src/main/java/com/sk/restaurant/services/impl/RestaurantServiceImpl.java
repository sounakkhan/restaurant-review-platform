package com.sk.restaurant.services.impl;

import com.sk.restaurant.domain.GeoLocation;
import com.sk.restaurant.domain.RestaurantCreateUpdateRequest;
import com.sk.restaurant.domain.entities.Address;
import com.sk.restaurant.domain.entities.Photo;
import com.sk.restaurant.domain.entities.Restaurants;
import com.sk.restaurant.ecxeptions.RestaurantNotFoundException;
import com.sk.restaurant.repositories.RestaurantRepository;
import com.sk.restaurant.services.GeoLocationService;
import com.sk.restaurant.services.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final GeoLocationService geoLocationService;

    @Override
    public Restaurants createRestaurant(RestaurantCreateUpdateRequest request) {
        Address address = request.getAddress();
        GeoLocation geoLocation = geoLocationService.geoLocate(address);
        GeoPoint geoPoint = new GeoPoint(geoLocation.getLatitude(), geoLocation.getLongitude());
        List<String> photoIds = request.getPhotoId();
        List<Photo> photos =(photoIds == null ? List.of() : photoIds).stream().map(photoUrl -> Photo.builder()
                .url((String) photoUrl)
                .uploadDate(LocalDateTime.now())
                .build()).toList();
        Restaurants restaurants = Restaurants.builder()
                .name(request.getName())
                .cuisineType(request.getCuisineType())
                .contactInformation(request.getContactInformation())
                .address(address)
                .geoLocation(geoPoint)
                .operatingHours(request.getOperatingHours())
                .averageRating(0f)
                .photos(photos)
                .reviews(new ArrayList<>())
                .build();
        return restaurantRepository.save(restaurants);

    }

    @Override
    public Page<Restaurants> searchRestaurants(String query, Float minRating, Float latitude, Float longitude, Float radius, Pageable pageable) {
        if (null != minRating && (query == null || query.isEmpty())) {
            return restaurantRepository.findByAverageRatingGreaterThanEqual(minRating, pageable);

        }
        Float searchMinRating = null == minRating ? 0f : minRating;
        if (null != query && query.trim().isEmpty()) {
            return restaurantRepository.findByQueryAndMinRating(query, searchMinRating, pageable);
        }
        if (null != latitude && null != longitude && null != radius) {
            return restaurantRepository.findByLocationNear(latitude, longitude, radius, pageable);
        }
        return restaurantRepository.findAll(pageable);
    }

    @Override
    public Optional<Restaurants> getRestaurant(String id) {
       return restaurantRepository.findById(id);
    }

    @Override
    public Restaurants updateRestaurant(String id, RestaurantCreateUpdateRequest request) {
        Restaurants restaurants=getRestaurant(id)
                .orElseThrow(() -> new RestaurantNotFoundException("restaurant with ID does not exist" +id));
        GeoLocation newGeoLocation=geoLocationService.geoLocate(request.getAddress());
        GeoPoint newGeoPoint = new GeoPoint(newGeoLocation.getLatitude(), newGeoLocation.getLongitude());
        List<String> photoIds = request.getPhotoId();
        List<Photo> photos =(photoIds == null ? List.of() : photoIds).stream().map(photoUrl -> Photo.builder()
                .url((String) photoUrl)
                .uploadDate(LocalDateTime.now())
                .build()).toList();
        restaurants.setName(request.getName());
        restaurants.setCuisineType(request.getCuisineType());
        restaurants.setContactInformation(request.getContactInformation());
        restaurants.setAddress(request.getAddress());
        restaurants.setGeoLocation(newGeoPoint);
        restaurants.setOperatingHours(request.getOperatingHours());
        restaurants.setPhotos(photos);
       return restaurantRepository.save(restaurants);

    }

    @Override
    public void deleteRestaurant(String id) {
        restaurantRepository.deleteById(id);
    }
}
