package com.sk.restaurant.domain.dtos;

import com.sk.restaurant.domain.entities.Address;
import com.sk.restaurant.domain.entities.OperatingHours;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class RestaurantCreateUpdateDto {
    @NotBlank(message="Restaurant name is required")
    private String name;
    @NotBlank(message="cuisineType  is required")
    private String cuisineType;
    @NotBlank(message="contact Information is required")
    private String contactInformation;
    @Valid
    private AddressDto address;
    @Valid
    private OperatingHourDto operatingHours;
    @Size(min = 1, message = "at least one photo id required")
    private List<String> photoId;

}
