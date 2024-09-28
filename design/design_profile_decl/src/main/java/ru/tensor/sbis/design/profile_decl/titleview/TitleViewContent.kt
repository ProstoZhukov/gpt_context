package ru.tensor.sbis.design.profile_decl.titleview

import androidx.annotation.DrawableRes

interface ITitleViewContent

/**
 * Модели данных для отображения контента в `SbisTitleView`
 *
 * @author ns.staricyn, us.bessonov
 */
sealed class TitleViewContent : ITitleViewContent {
    /**
     * Заголовок
     */
    abstract val title: CharSequence

    /**
     * Подзаголовок
     */
    abstract val subtitle: CharSequence

    companion object {
        /**
         * Пустой контент
         */
        val EMPTY = Default("")
    }
}

/**
 * Список содержимого для отображения в `SbisTitleView`
 *
 * @property list список отображаемых элементов, на основе которых формируется заголовок и подзаголовок по умолчанию, а
 * также коллаж фото
 * @property title текст заголовка (по умолчанию перечисление [TitleViewItem.title] через запятую)
 * @property subtitle текст подзаголовка (по умолчанию перечисление [TitleViewItem.subtitle] через запятую)
 * @property hiddenTitleCount число скрытых элементов, учитываемое при формировании счётчика в заголовке
 * @property forceListTitles нужно ли формировать заголовок, используя строки из списка, игнорируя [title]. В этом
 * случае в заголовке может присутствовать счётчик "(+N)", где N = [hiddenTitleCount] + число непоместившихся строк
 * из [list]
 *
 * @author us.bessonov
 */
class ListContent private constructor(
    val list: List<TitleViewItem>,
    override val title: String,
    override val subtitle: String,
    val hiddenTitleCount: Int,
    val forceListTitles: Boolean = false
) : TitleViewContent() {

    /**
     * Заголовок сокращается как обычный текст
     */
    constructor(
        list: List<TitleViewItem> = emptyList(),
        title: String = list.filter { it.title.isNotEmpty() }.joinToString { it.title },
        subtitle: String = list.joinSubtitles()
    ) : this(list, title, subtitle, 0)

    /**
     * Заголовок формируется перечислением [TitleViewItem.title] из [list]. Непоместившиеся строки уходят в счётчик,
     * вдобавок к значению [titleHiddenCount]
     */
    constructor(
        list: List<TitleViewItem>,
        titleHiddenCount: Int,
        subtitle: String = list.joinSubtitles()
    ) : this(list, "", subtitle, titleHiddenCount, true)

    companion object {
        private fun List<TitleViewItem>.joinSubtitles() =
            filter { it.subtitle.isNotEmpty() }.joinToString { it.subtitle }
    }
}

/**
 * Простой тип данных для отображения
 *
 * @property imageUrl ссылка на картинку
 * @property imagePlaceholderRes опциональный ресурс заглушки изображения
 *
 * @author us.bessonov
 */
data class Default(
    override val title: CharSequence,
    override val subtitle: CharSequence = "",
    val imageUrl: String = "",
    @DrawableRes
    val imagePlaceholderRes: Int? = null
) : TitleViewContent()
