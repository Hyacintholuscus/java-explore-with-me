package ru.practicum.ewmmain.categories.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Value
@Builder
@Jacksonized
public class CreateCategoryDto {
    @NotBlank(message = "Name of category shouldn't be blank")
    @Size(max = 50, message = "Name of category shouldn't be more than 50 characters")
    String name;
}
