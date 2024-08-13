package ru.practicum.ewmmain.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.practicum.ewmmain.user.dto.CreateUserDto;
import ru.practicum.ewmmain.user.dto.InitiatorDto;
import ru.practicum.ewmmain.user.dto.UserDto;
import ru.practicum.ewmmain.user.model.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    User toModel(CreateUserDto createUserDto);

    UserDto toDto(User user);

    InitiatorDto toInitiator(User user);
}
