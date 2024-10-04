package ru.tensor.sbis.design.header.data

import android.content.Context
import android.view.View
import android.widget.FrameLayout.LayoutParams

/**
 * Настройки контента в шапке между заголовком и кнопками принятия/закрытия.
 *
 * @author ai.korolev
 */
sealed class RightCustomContent {

    /**
     * Прикладной контент, создаёт View с помощью функции [creator].
     *
     * Если передать [contentIsResponsibleForEndPadding]=true, то
     * отступ между контентом и кнопками принятия/закрытия будет равен нулю,
     * в таком случае возвращаемая View должна сама выставить нужный отступ внутри себя.
     * Это бывает нужно в случае, если у View есть какая-то часть, которая заходит
     * в этот отступ, например, тень кнопки.
     */
    class Content(
        internal val contentIsResponsibleForEndPadding: Boolean = false,
        private val creator: (Context) -> View
    ) : RightCustomContent() {
        fun getView(context: Context) = creator(context)
    }

    /**
     * Прикладной контент, создаёт View с помощью функции [creator],
     * также можно передать параметры родителя через [layoutParams].
     *
     * Если передать [contentIsResponsibleForEndPadding]=true, то
     * отступ между контентом и кнопками принятия/закрытия будет равен нулю,
     * в таком случае возвращаемая View должна сама выставить нужный отступ внутри себя.
     * Это бывает нужно в случае, если у View есть какая-то часть, которая заходит
     * в этот отступ, например, тень кнопки.
     */
    class ContentWithLayoutParams(
        internal val layoutParams: LayoutParams,
        internal val contentIsResponsibleForEndPadding: Boolean = false,
        private val creator: (Context) -> View
    ) : RightCustomContent() {
        fun getView(context: Context) = creator(context)
    }

    /**
     * Контент между заголовком и кнопками не требуется.
     */
    object NoneContent : RightCustomContent()
}
