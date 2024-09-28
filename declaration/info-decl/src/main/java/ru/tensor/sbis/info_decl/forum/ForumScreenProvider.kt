package ru.tensor.sbis.info_decl.forum

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.*

/**
 * Поставщик экранов обсуждения (статьи).
 *
 * @author am.boldinov
 */
interface ForumScreenProvider : Feature {

    /**
     * Возвращает [Intent] для открытия активити с обсуждением
     *
     * @param context контекст экрана или приложения
     * @param documentUuid идентификатор статьи
     * @param themeId идентификатор темы обсуждения
     * @param messageId идентификатор сообщения для автоматической подстановки получателя и ответа
     */
    fun getForumCommentIntent(
        context: Context,
        documentUuid: UUID,
        themeId: UUID,
        messageId: UUID?,
        fileId: UUID? = null,
        commentPermission: ForumCommentPermission = ForumCommentPermission.NONE,
        commentAccessLevel: ForumCommentAccessLevel = ForumCommentAccessLevel.FULL
    ): Intent

    /**
     * Возвращает экземпляр [Fragment] для открытия обсуждения
     *
     * @param documentUuid идентификатор статьи
     * @param themeId идентификатор темы обсуждения
     * @param messageId идентификатор сообщения для автоматической подстановки получателя и ответа
     */
    fun getForumCommentFragment(
        documentUuid: UUID,
        themeId: UUID,
        messageId: UUID?,
        fileId: UUID? = null,
        commentPermission: ForumCommentPermission = ForumCommentPermission.NONE,
        commentAccessLevel: ForumCommentAccessLevel = ForumCommentAccessLevel.FULL
    ): Fragment
}