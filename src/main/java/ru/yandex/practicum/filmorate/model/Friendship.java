package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Friendship {
    @NonNull
    private int fromUserId;
    @NonNull
    private int toUserId;
    @NonNull
    private Boolean isMutual;
}