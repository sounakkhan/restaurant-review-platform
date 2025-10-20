package com.sk.restaurant.repositories;

import com.sk.restaurant.domain.entities.Restaurants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantRepository extends ElasticsearchRepository<Restaurants,String> {

    Page<Restaurants> findByAverageRatingGreaterThanEqual( float minRating,Pageable pageable);

    @Query("{" +
            "  \"bool\": {" +
            "    \"must\": [" +
            "      {\"range\": {\"averageRating\": {\"gte\": ?1}}}" +
            "    ]," +
            "    \"should\": [" +
            "      {\"fuzzy\": {\"name\": {\"value\": \"?0\", \"fuzziness\": \"AUTO\"}}}," +
            "      {\"fuzzy\": {\"cuisineType\": {\"value\": \"?0\", \"fuzziness\": \"AUTO\"}}}" +
            "    ]," +
            "    \"minimum_should_match\": 1" +
            "  }" +
            "}" +
            "}")
    Page<Restaurants> findByQueryAndMinRating(String query,float minRating,Pageable pageable);
    @Query("{" +
            "  \"bool\": {" +
            "    \"must\": [" +
            "      {\"geo_distance\": {" +
            "        \"distance\": \"{{#radius}}km\"," +
            "        \"geoLocation\": {" +
            "          \"lat\": {{#lat}}," +
            "          \"lon\": {{#lon}}" +
            "        }" +
            "      }}" +
            "    ]" +
            "  }" +
            "}")
    Page<Restaurants> findByLocationNear(
            @Param("lat")  float latitude,
            @Param("lon")  float longtitude,
            @Param("radius")  float radiusKm,
            Pageable pageable
    );
}
