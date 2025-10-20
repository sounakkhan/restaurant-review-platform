package com.sk.restaurant.mappers;

import com.sk.restaurant.domain.ReviewCreateUpdateRequest;
import com.sk.restaurant.domain.dtos.ReviewCreateUpdateDto;
import com.sk.restaurant.domain.dtos.ReviewDto;
import com.sk.restaurant.domain.entities.Review;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel ="spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReviewMapper {
    ReviewCreateUpdateRequest toReviewCreateUpdateRequest(ReviewCreateUpdateDto dto);
    ReviewDto toDto(Review review);
}
