package com.sk.restaurant.domain.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewCreateUpdateDto {
    @NotBlank(message = "review content is required")
    private String content;
    @NotNull(message = "rating is required")
    @Min(value = 1 ,message = "rating is between 1 to 5")
    @Max(value = 5 ,message = "rating is between 1 to 5")
    private Integer rating;
    private List<String> photoIds= new ArrayList<>();
}
