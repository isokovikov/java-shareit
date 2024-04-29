package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Slf4j
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();

    private Long id = 1L;

    @Override
    public List<User> findAll() {
        log.info("All users was provided.");
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> findById(Long id) {
        User user = users.get(id);
        if (user == null) {
            log.info("User with ID {} was not found.", id);
            return Optional.empty();
        } else {
            log.info("User with ID {} was found.", id);
            return Optional.of(user);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        log.info("Attempting to find user by email: {}", email);
        return users.values().stream()     // Получаем коллекцию пользователей из Map
                .filter(user -> user.getEmail().equalsIgnoreCase(email))  // Фильтруем по email
                .findFirst(); // Возвращаем первое совпадение, если оно есть
    }

    @Override
    public User create(User user) {
        user.setId(id);
        id++;
        users.put(user.getId(), user);
        log.info("User with ID {} was created.", user.getId());
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        log.info("User with ID {} was updated.", user.getId());
        return user;
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
        log.info("User with ID {} was remove.", id);
    }

}