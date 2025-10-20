package com.sk.restaurant.manual;

import java.io.IOException;
import java.util.List;
import java.util.Arrays;

import com.sk.restaurant.domain.RestaurantCreateUpdateRequest;
import com.sk.restaurant.domain.entities.Address;
import com.sk.restaurant.domain.entities.OperatingHours;
import com.sk.restaurant.domain.entities.Photo;
import com.sk.restaurant.domain.entities.TimeRange;
import com.sk.restaurant.services.PhotoServices;
import com.sk.restaurant.services.RestaurantService;
import com.sk.restaurant.services.impl.RandomLondonGeoLocation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
public class RestaurantDataLoaderTest {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private RandomLondonGeoLocation geoLocationService;

    @Autowired
    private PhotoServices photoService;

    @Autowired
    private ResourceLoader resourceLoader;

    @Test
    @Rollback(false) // Allow changes to persist
    public void createSampleRestaurants() throws Exception {
        List<RestaurantCreateUpdateRequest> restaurants = createRestaurantData();
        restaurants.forEach(restaurant -> {
            String fileName = restaurant.getPhotoId().getFirst();
            Resource resource = resourceLoader.getResource("classpath:testdata/" + fileName);
            MultipartFile multipartFile = null;
            try {
                multipartFile = new MockMultipartFile(
                        "file", // parameter name
                        fileName, // original filename
                        MediaType.IMAGE_PNG_VALUE,
                        resource.getInputStream()
                );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


            // Call the service method
            Photo uploadedPhoto = photoService.uploadPhoto(multipartFile);

            restaurant.setPhotoId(List.of(uploadedPhoto.getUrl()));

            restaurantService.createRestaurant(restaurant);

            System.out.println("Created restaurant: " + restaurant.getName());
        });
    }

    private List<RestaurantCreateUpdateRequest> createRestaurantData() {
        return Arrays.asList(
                createRestaurant(
                        "The Golden Dragon",
                        "Chinese",
                        "+44 20 7123 4567",
                        createAddress("12", "Gerrard Street", null, "London", "Greater London", "W1D 5PR", "United Kingdom"),
                        createStandardOperatingHours("11:30", "23:00", "11:30", "23:30"),
                        "9.png"
                ),
                createRestaurant(
                        "La Petite Maison",
                        "French",
                        "+44 20 7234 5678",
                        createAddress("54", "Brook Street", null, "London", "Greater London", "W1K 4HR", "United Kingdom"),
                        createStandardOperatingHours("12:00", "22:30", "12:00", "23:00"),
                        "6.png"
                ),
                createRestaurant(
                        "Raj Pavilion",
                        "Indian",
                        "+44 20 7345 6789",
                        createAddress("27", "Brick Lane", null, "London", "Greater London", "E1 6PU", "United Kingdom"),
                        createStandardOperatingHours("12:00", "23:00", "12:00", "23:30"),
                        "5.png"
                ),
                createRestaurant(
                        "Sushi Master",
                        "Japanese",
                        "+44 20 7456 7890",
                        createAddress("8", "Poland Street", null, "London", "Greater London", "W1F 8PR", "United Kingdom"),
                        createStandardOperatingHours("11:30", "22:00", "11:30", "22:30"),
                        "4.png"
                ),
                createRestaurant(
                        "The Rustic Olive",
                        "Italian",
                        "+44 20 7567 8901",
                        createAddress("92", "Dean Street", null, "London", "Greater London", "W1D 3SR", "United Kingdom"),
                        createStandardOperatingHours("11:00", "23:00", "11:00", "23:30"),
                        "3.png"
                ),
                createRestaurant(
                        "Dorian",
                        "Spanish",
                        "+44 20 7678 9012",
                        createAddress("15", "Charlotte Street", null, "London", "Greater London", "W1T 1RH", "United Kingdom"),
                        createStandardOperatingHours("12:00", "23:00", "12:00", "23:30"),
                        "8.png"
                ),
                createRestaurant(
                        "The Greek House",
                        "Greek",
                        "+44 20 7789 0123",
                        createAddress("32", "Store Street", null, "London", "Greater London", "WC1E 7BS", "United Kingdom"),
                        createStandardOperatingHours("12:00", "22:30", "12:00", "23:00"),
                        "2.png"
                ),
                createRestaurant(
                        "Seoul Kitchen",
                        "Korean",
                        "+44 20 7890 1234",
                        createAddress("71", "St John Street", null, "London", "Greater London", "EC1M 4AN", "United Kingdom"),
                        createStandardOperatingHours("11:30", "22:00", "11:30", "22:30"),
                        "7.png"
                ),
                createRestaurant(
                        "Thai Orchid",
                        "Thai",
                        "+44 20 7901 2345",
                        createAddress("45", "Warren Street", null, "London", "Greater London", "W1T 6AD", "United Kingdom"),
                        createStandardOperatingHours("11:00", "22:30", "11:00", "23:00"),
                        "images.png"
                ),
                createRestaurant(
                        "The rizz restaurant",
                        "American",
                        "+44 20 7012 3456",
                        createAddress("88", "Commercial Street", null, "London", "Greater London", "E1 6LY", "United Kingdom"),
                        createStandardOperatingHours("11:00", "23:00", "11:00", "23:30"),
                        "6.png"
                )
        );
    }

    private RestaurantCreateUpdateRequest createRestaurant(
            String name,
            String cuisineType,
            String contactInformation,
            Address address,
            OperatingHours operatingHours,
            String photoId
    ) {
        return RestaurantCreateUpdateRequest.builder()
                .name(name)
                .cuisineType(cuisineType)
                .contactInformation(contactInformation)
                .address(address)
                .operatingHours(operatingHours)
                .photoId(List.of(photoId))
                .build();
    }

    private Address createAddress(
            String streetNumber,
            String streetName,
            String unit,
            String city,
            String state,
            String postalCode,
            String country
    ) {
        Address address = new Address();
        address.setStreetNumber(streetNumber);
        address.setStreetName(streetName);
        address.setUnit(unit);
        address.setCity(city);
        address.setState(state);
        address.setPostalCode(postalCode);
        address.setCountry(country);
        return address;
    }

    private OperatingHours createStandardOperatingHours(
            String weekdayOpen,
            String weekdayClose,
            String weekendOpen,
            String weekendClose
    ) {
        TimeRange weekday = new TimeRange();
        weekday.setOpenTime(weekdayOpen);
        weekday.setCloseTime(weekdayClose);

        TimeRange weekend = new TimeRange();
        weekend.setOpenTime(weekendOpen);
        weekend.setCloseTime(weekendClose);

        OperatingHours hours = new OperatingHours();
        hours.setMonday(weekday);
        hours.setTuesday(weekday);
        hours.setWednesday(weekday);
        hours.setThursday(weekday);
        hours.setFriday(weekend);
        hours.setSaturday(weekend);
        hours.setSunday(weekend);

        return hours;
    }

}
