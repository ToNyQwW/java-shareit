package ru.practicum.shareit.item.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentCreateRequestDto;
import ru.practicum.shareit.item.dto.ItemCreateRequestDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequestDto;

import java.util.Map;

@Slf4j
@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    public ResponseEntity<Object> createItem(long userId, ItemCreateRequestDto itemCreateRequestDto) {
        log.info("POST /items create Item {} user with id {}", itemCreateRequestDto, userId);
        return post("", userId, itemCreateRequestDto);
    }

    public ResponseEntity<Object> getItem(long userId, long itemId) {
        log.info("GET /items/{userId} item with id {} user with id {}", itemId, userId);
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> searchItem(long userId, String text) {
        Map<String, Object> parameters = Map.of("text", text);
        log.info("GET /items/search Item with text {} user with id {}", text, userId);
        return get("/search?text={text}", userId, parameters);
    }

    public ResponseEntity<Object> getUserItems(long userId) {
        log.info("GET /items user with id {}", userId);
        return get("", userId);
    }

    public ResponseEntity<Object> updateItem(long userId, long itemId, ItemUpdateRequestDto itemUpdateRequestDto) {
        log.info("PATCH /items/{itemId} item with id {} user with id {}", itemId, userId);
        return patch("/" + itemId, userId, itemUpdateRequestDto);
    }

    public ResponseEntity<Object> createComment(long userId,
                                                long itemId,
                                                CommentCreateRequestDto commentCreateRequestDto) {
        log.info("POST /items/{itemId}/comment create Comment {} user with id {} item with id {}",
                commentCreateRequestDto, userId, itemId);
        return post("/" + itemId + "/comment", userId, commentCreateRequestDto);
    }
}