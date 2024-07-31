package ru.practicum.ewmmain.compilation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.compilation.dto.CompilationDto;
import ru.practicum.ewmmain.compilation.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/compilations")
public class PublicCompilationController {
    private final CompilationService compilationService;

    @GetMapping("/{compId}")
    public CompilationDto getByIdCompilation(@PathVariable
                                             @Positive(message = "Compilation's id should be positive")
                                             Long compId) {
        return compilationService.getByIdCompilation(compId);
    }

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                @RequestParam(defaultValue = "0")
                                               @PositiveOrZero(message = "Parameter 'from' shouldn't be negative")
                                               int from,
                                                @RequestParam(defaultValue = "10")
                                                   @Positive(message = "Parameter 'size' should be positive")
                                                   int size) {
        int page = from / size;
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        return compilationService.getCompilations(pinned, PageRequest.of(page, size, sort));
    }
}
