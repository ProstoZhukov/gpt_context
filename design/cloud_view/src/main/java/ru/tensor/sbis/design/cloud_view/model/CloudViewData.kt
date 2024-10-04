package ru.tensor.sbis.design.cloud_view.model

import android.text.Spannable
import ru.tensor.sbis.design.cloud_view.CloudView
import java.util.UUID

/**
 * Бизнес модель сообщения для [CloudView]
 *
 * @author ma.kolpakov
 */
interface CloudViewData {

    /**
     * Текст в облачке для отображения в компоненте [RichTextView]. Может быть только один блок текста
     */
    val text: Spannable?

    /**
     * Список блоков типа [CloudContent] для отображения в облачке
     */
    val content: List<CloudContent>

    /**
     * Список индексов элементов верхнего уровня в контенте (для иерархичного отображения)
     */
    val rootElements: List<Int>

    /**
     * Метка для отображения контента в неактивном состоянии
     */
    val isDisabledStyle: Boolean

    /**
     * Признак заблокированности автора сообщения.
     */
    val isAuthorBlocked: Boolean
        get() = false

    /**
     * Индентификатор сообщения.
     */
    val messageUuid: UUID?
        get() = null
}