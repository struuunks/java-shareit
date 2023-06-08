package ru.practicum.shareit.gateway.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.gateway.dto.Validated.Create;
import ru.practicum.shareit.gateway.dto.Validated.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    Long id;
    @NotBlank(groups = {Create.class})
    String name;
    @NotNull(groups = {Create.class})
    @Email(groups = {Create.class, Update.class})
    String email;
}
