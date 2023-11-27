package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.Collection;

@Service
@Slf4j
@AllArgsConstructor
public class MpaService {
    private final MpaStorage mpaStorage;

    public Mpa get(int mpaID) {
        if (!mpaStorage.contains(mpaID)) {
            log.warn("Не удалось вернуть рейтинг MPA.");
            throw new ObjectNotFoundException("MPA not found");
        }
        return mpaStorage.getMpaById(mpaID);
    }

    public Collection<Mpa> getAll() {
        return mpaStorage.getAll();
    }
}

