package ru.practicum.shareit.request.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

@Slf4j
@Service
public class ItemRequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    public ResponseEntity<Object> createItemRequest(long requestorId, ItemRequestCreateDto itemRequestCreateDto) {
        log.info("POST /requests create ItemRequest {} with requestorId {}", itemRequestCreateDto, requestorId);
        return post("", requestorId, itemRequestCreateDto);
    }

    public ResponseEntity<Object> getUserItemRequests(long requestorId) {
        log.info("GET /requests requestorId {}", requestorId);
        return get("", requestorId);
    }

    public ResponseEntity<Object> getAllItemRequests(long requestorId) {
        log.info("GET /requests/all requestorId {}", requestorId);
        return get("/all", requestorId);
    }

    public ResponseEntity<Object> getItemRequest(long requestId) {
        log.info("GET /requests/requestId  requestorId {}", requestId);
        return get("/" + requestId);
    }
}