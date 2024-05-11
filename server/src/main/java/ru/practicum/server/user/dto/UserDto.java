package ru.practicum.server.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.server.util.Create;
import ru.practicum.server.util.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@EqualsAndHashCode
public class UserDto {
    private Long id;

    @NotBlank(groups = {Create.class})
    private String name;

    @Email(groups = {Create.class, Update.class})
    @NotBlank(groups = {Create.class})
    private String email;
}