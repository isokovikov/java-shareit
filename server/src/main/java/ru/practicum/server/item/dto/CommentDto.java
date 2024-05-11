package ru.practicum.server.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class CommentDto {
    private Long id;

    private String text;

    private String authorName;

    private LocalDateTime created;
}