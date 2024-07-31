package ru.practicum.ewmmain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmmain.exception.ConflictException;
import ru.practicum.ewmmain.user.dto.CreateUserDto;
import ru.practicum.ewmmain.user.dto.UserDto;
import ru.practicum.ewmmain.exception.NotFoundException;
import ru.practicum.ewmmain.user.mapper.UserMapper;
import ru.practicum.ewmmain.user.model.User;
import ru.practicum.ewmmain.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(CreateUserDto createUserDto) {
        log.info("Запрос создать пользователя.");

        try {
            User user = userStorage.save(userMapper.toModel(createUserDto));
            return userMapper.toDto(user);
        } catch (DataIntegrityViolationException e) {
            log.error("Duplicate. Создание пользователя с занятым адресом эл. почты {}", createUserDto.getEmail());
            throw new ConflictException(e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto getUserById(Long id) {
        log.info("Запрос получить пользователя с id {}", id);

        User user = userStorage.findById(id).orElseThrow(() -> {
            log.error("NotFound. Пользователя с id {} не существует.", id);
            return new NotFoundException(
                    String.format("User with id %d is not exist.", id)
            );
        });
        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getUsers(List<Long> ids, Pageable pageable) {
        log.info("Запрос получить список пользователей с id {}.", ids);

        List<User> users = (ids == null || ids.isEmpty())
                ? userStorage.findAll(pageable).getContent() : userStorage.findByIdIn(ids, pageable);
        return users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Long deleteUser(Long id) {
        log.info("Запрос удалить пользователя с id {}", id);

        // Проверка на существование пользователя
        getUserById(id);

        userStorage.deleteById(id);
        return id;
    }
}
