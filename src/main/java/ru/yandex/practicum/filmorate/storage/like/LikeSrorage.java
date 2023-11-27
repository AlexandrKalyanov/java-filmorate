package ru.yandex.practicum.filmorate.storage.like;

public interface LikeSrorage {
    void add(int filmID, int userID);

    void delete(int filmID, int userID);

    int count(int filmID);

    boolean contains(int filmID, int userID);
}
