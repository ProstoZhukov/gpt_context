package ru.tensor.sbis.info_decl.forum

/**
 * Уровень доступа на комментирование.
 *
 * @property value числовое значение, соответствующее уровню доступа
 *
 * @author am.boldinov
 */
enum class ForumCommentAccessLevel(private val value: Short) {

    FULL(0), // полный доступ
    BANNED(1), // пользователь забанен
    GROUP(2), // комментирование доступно только членам группы
    DISABLED(3), // комментирование отключено настройками приватности
    ACCOUNT(4); // недоступно в текущем аккаунте

    companion object {

        /**
         * @param value значение
         * @return уровень доступа
         */
        fun fromValue(value: Short): ForumCommentAccessLevel? {
            return values().find {
                it.value == value
            }
        }
    }
}