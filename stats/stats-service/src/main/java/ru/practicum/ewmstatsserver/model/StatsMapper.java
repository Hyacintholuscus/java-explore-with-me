package ru.practicum.ewmstatsserver.model;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.practicum.ewmstatsserver.view.StatsView;
import ru.practicum.ewmstatsutil.dto.CreateStatsDto;
import ru.practicum.ewmstatsutil.dto.StatsDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StatsMapper {
    StatsModel toModel(CreateStatsDto createStatsDto);

    StatsDto toDto(StatsView statsView);

    CreateStatsDto toCreationDto(StatsModel statsModel);
}
