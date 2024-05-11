package ru.practicum.server.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.server.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    @Query("select i from ItemRequest i " +
            "where i.requester.id <> ?1 ")
    List<ItemRequest> findAllByRequesterIdNot(Long userId, Pageable pageable);

    List<ItemRequest> findAllByRequesterId(Long userId, Pageable pageable);
}