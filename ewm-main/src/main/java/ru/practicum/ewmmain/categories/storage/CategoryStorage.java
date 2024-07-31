package ru.practicum.ewmmain.categories.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmmain.categories.model.Category;

public interface CategoryStorage extends JpaRepository<Category, Long> {
}
