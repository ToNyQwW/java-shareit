package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemMapper itemMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemRequestDto createItemRequest(long requestorId, ItemRequestCreateDto itemRequestCreateDto) {
        User user = getUserOrElseThrow(requestorId);

        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestCreateDto);
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());

        ItemRequest createdItemRequest = itemRequestRepository.save(itemRequest);
        log.info("ItemRequest created: {}", createdItemRequest);

        return itemRequestMapper.toItemRequestDto(createdItemRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestWithItemsDto getItemRequest(long requestId) {
        ItemRequest itemRequest = getItemRequestOrElseThrow(requestId);

        ItemRequestWithItemsDto result = itemRequestMapper.toItemRequestWithItemsDto(itemRequest);
        List<Item> items = itemRepository.findAllByItemRequestId(requestId);
        List<ItemDto> itemsDto = mapListToItemDto(items);
        result.setItems(itemsDto);
        log.info(" getItemRequest result: {}", result);

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getAllItemRequests(long requestorId) {
        getUserOrElseThrow(requestorId);

        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        List<ItemRequest> result = itemRequestRepository.getAllByRequestorIdNot(requestorId, sort);
        log.info(" getAllItemRequests result: {}", result);

        return mapListToItemRequestDto(result);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestWithItemsDto> getUserItemRequests(long requestorId) {
        getUserOrElseThrow(requestorId);

        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        List<ItemRequest> itemRequests = itemRequestRepository.getAllByRequestorId(requestorId, sort);
        List<ItemRequestWithItemsDto> result = mapListToItemRequestWithItemsDto(itemRequests);
        log.info("getUserItemRequests result: {}", result);

        return result;
    }

    private List<ItemDto> mapListToItemDto(List<Item> items) {
        if (items == null) {
            return null;
        }
        return items.stream()
                .map(itemMapper::toItemDto)
                .toList();
    }

    private List<ItemRequestDto> mapListToItemRequestDto(List<ItemRequest> itemRequests) {
        if (itemRequests == null) {
            return null;
        }
        return itemRequests.stream()
                .map(itemRequestMapper::toItemRequestDto)
                .toList();
    }

    private List<ItemRequestWithItemsDto> mapListToItemRequestWithItemsDto(List<ItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(itemRequest ->
                {
                    ItemRequestWithItemsDto dto = itemRequestMapper.toItemRequestWithItemsDto(itemRequest);
                    List<ItemDto> itemsDto = mapListToItemDto(itemRequest.getItems());
                    dto.setItems(itemsDto);
                    return dto;
                })
                .toList();
    }

    private User getUserOrElseThrow(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
    }

    private ItemRequest getItemRequestOrElseThrow(long id) {
        return itemRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("ItemRequest with id " + id + " not found"));
    }
}