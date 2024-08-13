package ru.practicum.ewmmain.categories.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmmain.categories.dto.CategoryDto;
import ru.practicum.ewmmain.categories.dto.CreateCategoryDto;
import ru.practicum.ewmmain.categories.mapper.CategoryMapper;
import ru.practicum.ewmmain.categories.model.Category;
import ru.practicum.ewmmain.categories.storage.CategoryStorage;
import ru.practicum.ewmmain.exception.ConflictException;
import ru.practicum.ewmmain.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryStorage categoryStorage;
    private final CategoryMapper mapper;

    @Override
    public CategoryDto createCategory(CreateCategoryDto creationDto) {
        log.info("Запрос создать категорию.");

        try {
            Category savedCategory = categoryStorage.save(mapper.toModel(creationDto));
            return mapper.toDto(savedCategory);
        } catch (DataIntegrityViolationException e) {
            log.error("Duplicate. Создание категории с используемым названием {}.", creationDto.getName());
            throw new ConflictException(e.getMessage());
        }
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        log.info("Запрос обновить категорию с id {}.", categoryDto.getId());

        try {
            Category savedCategory = categoryStorage.saveAndFlush(mapper.toModel(categoryDto));
            return mapper.toDto(savedCategory);
        } catch (DataIntegrityViolationException e) {
            log.error("Duplicate. Обновление категории с используемым названием {}.", categoryDto.getName());
            throw new ConflictException(e.getMessage());
        }
    }

    @Override
    public Long deleteCategory(Long id) {
        log.info("Запрос удалить категорию с id {}.", id);

        // Проверка на существование категории
        getCategoryById(id);

        categoryStorage.deleteById(id);
        return id;
    }

    @Transactional(readOnly = true)
    @Override
    public List<CategoryDto> getCategories(Pageable pageable) {
        List<Category> categories = categoryStorage.findAll(pageable).getContent();
        return categories.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public CategoryDto getCategoryById(Long id) {
        log.info("Запрос найти категорию с id {}.", id);

        Category category = categoryStorage.findById(id).orElseThrow(() -> {
            log.error("NotFound. Категории с id {} не существует.", id);
            return new NotFoundException(String.format("Category with id %d is not exist.", id));
                });
        return mapper.toDto(category);
    }
}
