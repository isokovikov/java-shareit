package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getById_shouldReturnUserNotFoundException() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(1L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void getById_shouldReturnUser() {
        User user = new User(1L, "Alex", "alex.b@yandex.ru");
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));

        UserDto expected = new UserDto(1L, "Alex", "alex.b@yandex.ru");
        UserDto actual = userService.getById(user.getId());

        assertEquals(expected, actual);
    }

    @Test
    void create_shouldSaveUserDto() {
        User user = new User(1L, "Alex", "alex.b@yandex.ru");
        UserDto userDtoWithoutId = new UserDto(null, "Alex", "alex.b@yandex.ru");
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);

        UserDto expected = new UserDto(1L, "Alex", "alex.b@yandex.ru");
        UserDto actual = userService.create(userDtoWithoutId);

        assertEquals(expected, actual);
    }

    @Test
    void createWithEmailNotValid_shouldReturnValidationException() {
        UserDto user = new UserDto(null, "Alex", "email");
        Mockito.when(userRepository.save(Mockito.any())).thenThrow(ValidationException.class);

        assertThatThrownBy(() -> userService.create(user)).isInstanceOf(ValidationException.class);
    }


    @Test
    void update_shouldReturnUserNotFoundException() {
        UserDto dto = new UserDto(999L, "Alex", "alex.b@yandex.ru");
        Mockito.when(userRepository.findById(dto.getId())).thenReturn(Optional.empty());
        Long userId = 999L;

        assertThatThrownBy(() -> userService.update(dto, userId)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void update_shouldUpdateName() {
        UserDto dto = new UserDto(null, "Alex", "john.d@yandex.ru");
        Long userId = 1L;
        User user = new User(1L, "Alex", "alex.b@yandex.ru");
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto expected = new UserDto(1L, "Alex", "john.d@yandex.ru");
        UserDto actual = userService.update(dto, userId);

        assertEquals(expected, actual);
    }

    @Test
    void delete() {
        User user4 = new User(4L, "Mike", "mike.d@yandex.ru");

        userService.delete(user4.getId());

        Mockito.verify(userRepository, Mockito.times(1)).deleteById(4L);
    }

    @Test
    void getAll_shouldReturnListNotEmpty() {
        User user1 = new User(1L, "Alex", "alex.b@yandex.ru");
        User user2 = new User(2L, "Bill", "bill.d@yandex.ru");
        User user3 = new User(3L, "John", "john.d@yandex.ru");
        List<User> page = List.of(user1, user2, user3);

        Mockito.when(userRepository.findAll()).thenReturn(page);

        List<UserDto> expectedList = Stream.of(user1, user2, user3).map(UserMapper::toUserDto).collect(Collectors.toList());
        List<UserDto> actualList = userService.getAll();


        assertEquals(expectedList, actualList);
    }
}