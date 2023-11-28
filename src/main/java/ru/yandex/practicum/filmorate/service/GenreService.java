package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;


    public Genre get(int genreID) {
        if (!genreStorage.contains(genreID)) {
            throw new ObjectNotFoundException("Genre not found");
        }
        return genreStorage.get(genreID);
    }

    public Collection<Genre> getAll() {
        return genreStorage.getAll();
    }
}