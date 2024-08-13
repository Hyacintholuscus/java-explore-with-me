package ru.practicum.ewmmain.user.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewmmain.user.dto.CreateUserDto;
import ru.practicum.ewmmain.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(CreateUserDto createUserDto);

    UserDto getUserById(Long id);

    List<UserDto> getUsers(List<Long> ids, Pageable pageable);

    Long deleteUser(Long id);
}
