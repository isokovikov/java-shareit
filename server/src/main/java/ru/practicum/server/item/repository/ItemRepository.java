package ru.practicum.server.item.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.server.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(Long ownerId, Sort sort);

    List<Item> findByRequest_IdOrderById(Long requestId);

    List<Item> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAndAvailable(String text, String str,
                                                                                              Boolean available);

    List<Item> findByRequest_IdIn(List<Long> requestsId);
}