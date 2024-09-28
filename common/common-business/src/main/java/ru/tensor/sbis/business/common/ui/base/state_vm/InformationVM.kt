package ru.tensor.sbis.business.common.ui.base.state_vm

import android.content.Context
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat.ID_NULL
import androidx.databinding.BaseObservable
import androidx.databinding.ObservableBoolean
import ru.tensor.sbis.business.common.ui.base.contract.InfoContract
import ru.tensor.sbis.business.common.ui.base.state_vm.UiErrorType.UNKNOWN_ERROR
import ru.tensor.sbis.business.common.ui.utils.isTrue
import ru.tensor.sbis.design.stubview.ImageStubContent
import ru.tensor.sbis.design.stubview.StubViewContent
import ru.tensor.sbis.design.stubview.StubViewImageType
import ru.tensor.sbis.design.stubview.StubViewMode
import ru.tensor.sbis.list.view.item.comparator.ComparableItem

/**
 * Вьюмодель состояния заглушки с ошибкой или доп. информацией
 *
 * @author as.chadov
 *
 * @param type тип ошибки
 * @param imageType Тип изображения заглушки
 * @param headerResId id ресурса заголовка с информацией о причине возникновения заглушки
 * @param commentResId id ресурса комментария с развернутой причиной возникновения заглушки или информацией о дальнейших действиях
 * @param errorText полученное сообщение об ошибки
 * @param activeCommentResId id ресурса с подстрокой активной кликабельной части комментария
 * @param commentAction callback по клику на активную часть комментария
 * @param popupIconResId id ресурса иконки в случае отображения в панели информере
 */
data class InformationVM(
    val type: ErrorType = UNKNOWN_ERROR,
    val imageType: StubViewImageType? = StubViewImageType.ERROR,
    @StringRes val headerResId: Int = ID_NULL,
    @StringRes val commentResId: Int = ID_NULL,
    var errorText: String = "",
    var activeCommentResId: Int = ID_NULL,
    var commentAction: (() -> Unit)? = null,
    @StringRes val popupIconResId: Int = ID_NULL,
) : BaseObservable(),
    ComparableItem<InformationVM>,
    InfoContract {

    override val show = ObservableBoolean(true)
    override var shouldPlaceholderTakeAllAvailableHeightInParent = false
    override val shouldPlaceholderWrapContentByDefault = true
    override var stubMode = StubViewMode.BASE
    override var stubMinHeight = true

    /** Стоит ли отображать иконку заглушки ( картинка с человечком ) */
    val showIcon: ObservableBoolean = ObservableBoolean(false)

    /** Стоит ли соединять описание и комментарий в одну строку */
    var mergeTitleAndComment: Boolean = false

    /** Выполнить конфигурирование экземпляра перед использованием */
    fun configure(change: InformationVM.() -> Unit): InformationVM =
        apply { change() }

    /** Проверить принадлежность к типу ошибки [ErrorType] */
    fun isType(error: ErrorType) = type == error

    /** Установить действие по клику на активную часть комментария, если такая имеется. */
    fun setHighlightedStubTextAction(action: (ErrorType) -> Unit) {
        if (activeCommentResId != ID_NULL) {
            commentAction = { action(type) }
        }
    }

    override fun content(context: Context): StubViewContent =
        getStubContent(context, showIcon.isTrue, mergeTitleAndComment)

    /**
     * Формирует строку из заголовка и комментария
     *
     * @param stringProvider лямбда получения строки по id ресурса
     */
    fun toString(stringProvider: (resId: Int) -> String): String {
        val baseInfo = getCommentContent { resId -> stringProvider.invoke(resId) }
        return StringBuilder(getHeaderMessage { resId -> stringProvider.invoke(resId) })
            .apply { if (headerResId != ID_NULL && baseInfo.isNotBlank()) append(". ") }
            .append(baseInfo)
            .toString()
    }

    override fun dispose() {
        commentAction = null
    }

    /**
     * @param context контекст
     * @param showImage содержит ли заглушка иконку. Сначала ищем в теме, если нет, то светлая картинка по-умолчанию
     * @param mergeTitleAndComment стоит ли соединить заголовок и описание в одной строке с применением единого стиля
     *
     * @return контент заглушки [StubViewContent]
     */
    private fun getStubContent(
        context: Context,
        showImage: Boolean,
        mergeTitleAndComment: Boolean,
    ): StubViewContent {
        val details = if (mergeTitleAndComment) toString(context::getString) else getComment(context).toString()
        return ImageStubContent(
            imageType = if (showImage && imageType != null) imageType else StubViewImageType.EMPTY_STUB_IMAGE,
            message = context.takeUnless { mergeTitleAndComment }?.let(::getHeader).orEmpty(),
            details = details,
            actions = getActions()
        )
    }

    private fun getActions() = with(commentAction) {
        when {
            this == null -> emptyMap()
            activeCommentResId != ID_NULL -> mapOf(activeCommentResId to this)
            commentResId != ID_NULL -> mapOf(commentResId to this)
            else -> emptyMap()
        }
    }

    /**
     * @return строка заголовка по [headerResId]
     */
    private fun getHeader(context: Context) =
        getHeaderMessage { resId -> context.getString(resId) }

    /**
     * @return строка комментария по [commentResId]
     */
    private fun getComment(context: Context) =
        getCommentContent { resId -> context.getString(resId) }

    private fun getHeaderMessage(stringProvider: (resId: Int) -> String): String =
        if (headerResId != ID_NULL) stringProvider.invoke(headerResId) else ""

    private fun getCommentContent(stringProvider: (resId: Int) -> String): CharSequence = when {
        errorText.isNotBlank() -> errorText
        commentResId != ID_NULL -> stringProvider.invoke(commentResId)
        else -> ""
    }

    override fun areTheSame(otherItem: InformationVM): Boolean =
        type == otherItem.type

    override fun hasTheSameContent(otherItem: InformationVM): Boolean =
        imageType == otherItem.imageType &&
            headerResId == otherItem.headerResId &&
            commentResId == otherItem.commentResId &&
            errorText == otherItem.errorText &&
            activeCommentResId == otherItem.activeCommentResId

    companion object {
        val empty = InformationVM()
    }
}