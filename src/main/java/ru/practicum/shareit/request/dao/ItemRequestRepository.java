package ru.practicum.shareit.request.dao;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    @EntityGraph(attributePaths = "items")
    List<ItemRequest> getAllByRequestorId(long requestorId, Sort sort);

    List<ItemRequest> getAllByRequestorIdNot(long requestorId, Sort sort);
}