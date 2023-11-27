package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class Like {
    @NotNull
    private Long filmID;
    @NotNull
    private Long userID;
}