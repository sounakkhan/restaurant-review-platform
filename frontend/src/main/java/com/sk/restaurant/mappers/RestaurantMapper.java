package com.sk.restaurant.mappers;

import com.sk.restaurant.domain.RestaurantCreateUpdateRequest;
import com.sk.restaurant.domain.dtos.GeoPointDto;
import com.sk.restaurant.domain.dtos.RestaurantCreateUpdateDto;
import com.sk.restaurant.domain.dtos.RestaurantDto;
import com.sk.restaurant.domain.dtos.RestaurantSummaryDto;
import com.sk.restaurant.domain.entities.Restaurants;
import com.sk.restaurant.domain.entities.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import java.util.List;

@Mapper(componentModel ="spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RestaurantMapper {
    RestaurantCreateUpdateRequest  toRestaurantCreateUpdateRequest(RestaurantCreateUpdateDto dto);
    @Mapping(source = "reviews", target = "totalReviews", qualifiedByName = "populateTotalReviews")
    RestaurantDto  toRestaurantDto(Restaurants restaurants);
    @Mapping(source = "reviews", target = "totalReviews", qualifiedByName = "populateTotalReviews")
    RestaurantSummaryDto toSummaryDto(Restaurants restaurants);
    @Named("populateTotalReviews")
    default Integer populateTotalReviews(List<Review> reviews) {
        return reviews.size();
    }

    @Mapping(target = "latitude",expression = "java(geoPoint.getLat())")
    @Mapping(target = "longitude",expression = "java(geoPoint.getLon())")
    GeoPointDto  toGeoPointDto(GeoPoint geoPoint);
}
