package com.sk.restaurant.domain.dtos;

import com.sk.restaurant.domain.entities.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestaurantDto {

    private String id;

    private String name;

    private String cuisineType;

    private String contactInformation;

    private float averageRating;

    private GeoPointDto geoLocation;

    private AddressDto address;

    private OperatingHourDto operatingHours;

    private List<PhotoDto> photos=new ArrayList<>();

    private List<ReviewDto> reviews=new ArrayList<>();

    private UserDto createdBy;
    private Integer totalReviews;


}
