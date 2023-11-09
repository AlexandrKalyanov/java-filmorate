# java-filmorate
## Техническое задание (sprint 10)
Добавьте методы, позволяющие пользователям добавлять друг друга в друзья, получать список общих друзей и лайкать фильмы. Проверьте, что все они работают корректно.

PUT /users/{id}/friends/{friendId} — добавление в друзья.

DELETE /users/{id}/friends/{friendId} — удаление из друзей.

GET /users/{id}/friends — возвращаем список пользователей, являющихся его друзьями.

GET /users/{id}/friends/common/{otherId} — список друзей, общих с другим пользователем.

PUT /films/{id}/like/{userId} — пользователь ставит лайк фильму.

DELETE /films/{id}/like/{userId} — пользователь удаляет лайк.

GET /films/popular?count={count} — возвращает список из первых count фильмов по количеству лайков. Если значение параметра count не задано, верните первые 10.

Убедитесь, что ваше приложение возвращает корректные HTTP-коды.

400 — если ошибка валидации: ValidationException;

404 — для всех ситуаций, если искомый объект не найден;

500 — если возникло исключение.


