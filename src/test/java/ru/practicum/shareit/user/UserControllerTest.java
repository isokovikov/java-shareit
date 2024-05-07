package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @SneakyThrows
    @Test
    void getAll() {
        Integer from = 0;
        Integer size = 20;
        mockMvc.perform(get("/users")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(userService).getAll();
    }

    @SneakyThrows
    @Test
    void getAll_whenRequestParamIsDefault() {
        mockMvc.perform(get("/users"))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(userService).getAll();
    }

    @SneakyThrows
    @Test
    void getById() {
        Long userId = 1L;
        mockMvc.perform(get("/users/{id}", userId))
                .andDo(print())
                .andExpect(status().isOk());

        Mockito.verify(userService).getById(userId);
    }

    @SneakyThrows
    @Test
    void create_whenUserNameIsNotValid_shouldReturnedBadRequest() {
        Long userId = 1L;
        UserDto createUserDto = new UserDto(null, null, "alex.b@yandex.ru");

        mockMvc.perform(post("/users", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isBadRequest());

        createUserDto.setName(" ");
        mockMvc.perform(post("/users", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isBadRequest());

        Mockito.verify(userService, Mockito.never()).create(createUserDto);
    }

    @SneakyThrows
    @Test
    void create_whenUserEmailIsNotValid_shouldReturnedBadRequest() {
        Long userId = 1L;
        UserDto createUserDto = new UserDto(null, "Alex", "hello");

        mockMvc.perform(post("/users", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isBadRequest());

        createUserDto.setEmail(" ");
        mockMvc.perform(post("/users", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isBadRequest());

        createUserDto.setEmail(null);
        mockMvc.perform(post("/users", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isBadRequest());

        Mockito.verify(userService, Mockito.never()).create(createUserDto);
    }

    @SneakyThrows
    @Test
    void update_whenUserNameIsNotValid_shouldReturnedBadRequest() {
        Long userId = 1L;
        UserDto updateUserDto = new UserDto(null, null, "alex");

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateUserDto)))
                .andExpect(status().isBadRequest());

        Mockito.verify(userService, Mockito.never()).update(updateUserDto, userId);
    }

    @SneakyThrows
    @Test
    void update_whenUserEmailIsNotValid_shouldReturnedBadRequest() {
        Long userId = 1L;
        UserDto updateUserDto = new UserDto(null, "Alex", "hello");

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateUserDto)))
                .andExpect(status().isBadRequest());

        Mockito.verify(userService, Mockito.never()).update(updateUserDto, userId);
    }

    @SneakyThrows
    @Test
    void create_whenUserIdNotFound() {
        Long userId = 2L;
        UserDto createUserDto = new UserDto(null, null, "alex.b@yandex.ru");

        mockMvc.perform(post("/users", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isBadRequest());

        createUserDto.setName(" ");
        mockMvc.perform(post("/users", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isBadRequest());
        Mockito.verify(userService, Mockito.never()).create(createUserDto);
    }

    @SneakyThrows
    @Test
    void deleteById() {
        Long userId = 1L;
        mockMvc.perform(delete("/users/{id}", userId))
                .andDo(print())
                .andExpect(status().isOk());

        Mockito.verify(userService).delete(userId);
    }

    @SneakyThrows
    @Test
    void create() {
        Long userId = 1L;
        UserDto createUserDto = new UserDto(1L, "Alex", "alex.b@yandex.ru");
        mockMvc.perform(post("/users")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Mockito.verify(userService).create(createUserDto);
    }
}