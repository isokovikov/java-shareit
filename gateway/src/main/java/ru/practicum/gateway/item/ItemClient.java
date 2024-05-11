package ru.practicum.gateway.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.gateway.client.BaseClient;
import ru.practicum.gateway.item.dto.CommentRequestDto;
import ru.practicum.gateway.item.dto.ItemRequestDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getItems(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getItem(Long itemId, long userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> createItem(long userId, ItemRequestDto requestDto) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> updateItem(ItemRequestDto requestDto, Long itemId, long userId) {
        return patch("/" + itemId, userId, requestDto);
    }

    public ResponseEntity<Object> deleteItem(Long itemId) {
        return delete("/" + itemId);
    }

    public ResponseEntity<Object> searchItem(long userId, String text) {
        Map<String, Object> parameters = Map.of(
                "text", text
        );
        return get("/search?text={text}", userId, parameters);
    }

    public ResponseEntity<Object> createComment(Long itemId, long userId, CommentRequestDto requestDto) {
        return post("/" + itemId + "/comment", userId, requestDto);
    }
}
