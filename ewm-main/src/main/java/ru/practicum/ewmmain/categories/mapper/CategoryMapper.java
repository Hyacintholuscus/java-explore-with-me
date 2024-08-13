package ru.practicum.ewmmain.categories.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.practicum.ewmmain.categories.dto.CategoryDto;
import ru.practicum.ewmmain.categories.dto.CreateCategoryDto;
import ru.practicum.ewmmain.categories.model.Category;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    Category toModel(CreateCategoryDto createCategoryDto);

    Category toModel(CategoryDto categoryDto);
}
