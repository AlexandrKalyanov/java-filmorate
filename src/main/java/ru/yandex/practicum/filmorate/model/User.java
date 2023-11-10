package ru.yandex.practicum.filmorate.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private int id;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String login;
    private String name;
    @NotNull
    private LocalDate birthday;
    @Setter(AccessLevel.NONE)
    @JsonIgnore
    private Set<Integer> friends = new HashSet<>();
}
