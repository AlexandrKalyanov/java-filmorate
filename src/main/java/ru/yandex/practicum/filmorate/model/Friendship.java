package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Friendship {
    @NotNull
    private int fromUserId;
    @NotNull
    private int toUserId;
    @NotNull
    private Boolean isMutual;
}