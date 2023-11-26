package ru.yandex.practicum.filmorate.storage.friendship;

import java.util.Collection;

public interface FriendShipStorage {
    void add(int fromUserID, int toUserID, boolean isMutual);
    void delete(int fromUserID, int toUserID);
    Collection<Integer> getFromUserIDs(int toUserId);
    boolean contains(int fromID, int toID);
}
