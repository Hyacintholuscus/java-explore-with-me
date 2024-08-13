package ru.practicum.ewmmain.categories.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewmmain.categories.dto.CategoryDto;
import ru.practicum.ewmmain.categories.dto.CreateCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(CreateCategoryDto creationDto);

    CategoryDto updateCategory(CategoryDto categoryDto);

    Long deleteCategory(Long id);

    List<CategoryDto> getCategories(Pageable pageable);

    CategoryDto getCategoryById(Long id);
}
