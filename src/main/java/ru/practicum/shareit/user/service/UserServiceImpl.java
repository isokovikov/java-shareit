package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.user.dto.UserMapper.toUser;
import static ru.practicum.shareit.user.dto.UserMapper.toUserDto;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAll() {
        return UserMapper.toUserDtoList(userRepository.findAll());
    }

    @Override
    public UserDto getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No user with ID: " + id));

        return toUserDto(user);
    }

    @Override
    public UserDto create(UserDto userDto) {
        User user = toUser(userDto);
        validateEmailUnique(user.getEmail(), user.getId());

        return toUserDto(userRepository.create(user));
    }

    @Override
    public UserDto update(UserDto userDto, Long id) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User data cannot be updated. No user with ID: " + id));

        boolean needUpdate = false;

        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            validateEmailUnique(userDto.getEmail(), id);
            existingUser.setEmail(userDto.getEmail());
            needUpdate = true;
        }

        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            existingUser.setName(userDto.getName());
            needUpdate = true;
        }

        if (needUpdate) {
            userRepository.update(existingUser);
        }

        return UserMapper.toUserDto(existingUser);
    }

    @Override
    public void delete(Long id) {
        getById(id);
        userRepository.delete(id);
    }

    private void validateEmailUnique(String email, Long userId) {
        Optional<User> userWithSameEmail = userRepository.findByEmail(email);
        if (userWithSameEmail.isPresent() && !userWithSameEmail.get().getId().equals(userId)) {
            throw new ValidationException("The user with this email is already registered");
        }
    }
}