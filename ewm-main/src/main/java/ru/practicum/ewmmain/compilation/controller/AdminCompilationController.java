package ru.practicum.ewmmain.compilation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.compilation.dto.CompilationDto;
import ru.practicum.ewmmain.compilation.dto.CreateCompilationDto;
import ru.practicum.ewmmain.compilation.dto.UpdateCompilationDto;
import ru.practicum.ewmmain.compilation.service.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/compilations")
public class AdminCompilationController {
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public CompilationDto createCompilation(@Valid @RequestBody CreateCompilationDto createCompilationDto) {
        return compilationService.createCompilation(createCompilationDto);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@PathVariable
                                                @Positive(message = "Compilation's id should be positive")
                                                Long compId,
                                            @Valid @RequestBody UpdateCompilationDto updateDto) {
        return compilationService.updateCompilation(compId, updateDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Long deleteCompilation(@PathVariable
                                  @Positive(message = "Compilation's id should be positive")
                                  Long compId) {
        return compilationService.deleteCompilation(compId);
    }
}
