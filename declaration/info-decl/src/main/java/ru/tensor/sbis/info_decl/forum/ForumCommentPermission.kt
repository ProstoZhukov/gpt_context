package ru.tensor.sbis.info_decl.forum

/**
 * Разрешение на работу с комментариями к обсуждению.
 *
 * @author am.boldinov
 */
enum class ForumCommentPermission {
    NONE,
    READ,
    READ_WRITE,
    DISABLED
}