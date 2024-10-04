package ru.tensor.sbis.design.topNavigation.api

import android.content.Context
import android.text.TextUtils
import androidx.annotation.StringRes
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.design.profile.R
import ru.tensor.sbis.design.profile.titleview.SbisTitleView

/**
 * Обработчик текста.
 *
 * @author da.zolotarev
 */
fun interface SbisTopNavigationTitleHandler {

    /**
     * Вернуть обновленный текст заголовка.
     *
     * @param availableWidth - доступная ширина
     * @param availableHeight - доступная высота
     * @param titleText - информация о заголовке
     * @param content - текущий контент шапки
     * @param titleMaxLines - максимальное количество строк в заголовке
     */
    fun format(
        availableWidth: Int,
        availableHeight: Int,
        titleText: SbisTopNavigationTitleText,
        content: SbisTopNavigationContent,
        titleMaxLines: Int
    ): CharSequence
}

/**
 * Обработчик текста, устанавливаемого через [SbisTopNavigationContent.SmallTitleListContent].
 * (Аналог базовой логики [SbisTitleView])
 *
 * @param context - контекст, из которого достается текстовый ресурс для счетчика
 * (используется applicationContext под капотом, поэтому ничего не утечет)
 */
class SbisTopNavigationListContentTextHandler(context: Context) : SbisTopNavigationTitleHandler {

    /**
     * Ресурс счетчика.
     */
    @StringRes
    var counterTextRes = R.string.design_profile_dialog_title_counter

    private val resourceProvider = ResourceProvider(context)

    /**
     * Поддержка отображения списка имен в полную ширину строки (длинное имя уходит в многоточие)
     */
    private var supportNamesFullWidth = false

    override fun format(
        availableWidth: Int,
        availableHeight: Int,
        titleText: SbisTopNavigationTitleText,
        content: SbisTopNavigationContent,
        titleMaxLines: Int
    ): CharSequence {
        if (content !is SbisTopNavigationContent.SmallTitleListContent) return titleText.getTextValue()
        val names = content.model.list.map { it.title }.filter { it.isNotEmpty() }
        val namesCount = names.size
        val defaultCounterText = resourceProvider.getString(counterTextRes, 0)
        var firstLine = true
        var nextIterationTitleText: String?
        val titleMetaData = TextHandlerMetaData(content.model.hiddenTitleCount)
        var firstNameInLine = 0
        for (nameIndex in 0 until namesCount) {
            nextIterationTitleText = TextUtils.join(DELIMITER, names.subList(firstNameInLine, nameIndex + 1))
            if (titleText.getTextWidth(nextIterationTitleText) > availableWidth) {
                if (firstLine && titleMaxLines != 1) {
                    firstNameInLine = nameIndex
                    firstLine = false
                } else {
                    if (supportNamesFullWidth || nameIndex == 0) {
                        // в данном режиме отображаем имена, которые поместились хотя бы частично
                        // поэтому последнее имя (которое отображается не полностью) считаем видимым
                        titleMetaData.firstHiddenNameIndex = nameIndex + 1
                    }
                    titleMetaData.counterValue =
                        namesCount - titleMetaData.firstHiddenNameIndex + content.model.hiddenTitleCount
                    if (titleMetaData.counterValue > 0) {
                        val widthWithoutDefaultCounter = availableWidth - titleText.getTextWidth(defaultCounterText)
                        updateMetaDataToFitCounter(
                            names,
                            titleText,
                            titleMetaData,
                            firstNameInLine,
                            widthWithoutDefaultCounter.toFloat()
                        )
                    }
                    break
                }
            }
            titleMetaData.firstHiddenNameIndex = nameIndex + 1
        }
        if (titleMetaData.firstHiddenNameIndex == namesCount && titleMetaData.counterValue > 0) {
            updateMetaDataToFitCounter(
                names,
                titleText,
                titleMetaData,
                firstNameInLine,
                (availableWidth - titleText.getTextWidth(defaultCounterText)).toFloat()
            )
        }
        var title = TextUtils.join(DELIMITER, names.subList(0, titleMetaData.firstHiddenNameIndex))
        if (titleMetaData.counterValue > 0) {
            if (titleMetaData.lastNameHiddenCharactersCount > 0) {
                title = if (supportNamesFullWidth) {
                    // последнее отображаемое имя не помещается целиком, заменяем последние символы многоточием
                    val length =
                        title.length - titleMetaData.lastNameHiddenCharactersCount - 1
                    title.substring(0, length) + Typography.ellipsis
                } else {
                    title.substring(0, title.length - titleMetaData.lastNameHiddenCharactersCount)
                }
            }
            title += resourceProvider.getString(counterTextRes, titleMetaData.counterValue)
        }
        return title
    }

    private fun updateMetaDataToFitCounter(
        names: List<CharSequence?>,
        titleText: SbisTopNavigationTitleText,
        titleMetaData: TextHandlerMetaData,
        firstNameInLine: Int,
        width: Float
    ) {
        var counterText = titleMetaData.counterValue.toString()
        var counterWidth = titleText.getTextWidth(counterText)
        var freeWidth = width - titleText.getTextWidth(
            TextUtils.join(
                DELIMITER,
                names.subList(firstNameInLine, titleMetaData.firstHiddenNameIndex)
            )
        )
        while (isFitted(titleMetaData, firstNameInLine, counterWidth - freeWidth, names, titleText)) {
            titleMetaData.firstHiddenNameIndex--
            titleMetaData.counterValue++
            titleMetaData.lastNameHiddenCharactersCount = 0
            freeWidth = width - titleText.getTextWidth(
                TextUtils.join(
                    DELIMITER,
                    names.subList(firstNameInLine, titleMetaData.firstHiddenNameIndex)
                )
            )
            counterText = titleMetaData.counterValue.toString()
            counterWidth = titleText.getTextWidth(counterText)
        }
    }

    private fun isFitted(
        titleMetaData: TextHandlerMetaData,
        firstNameInLine: Int,
        width: Float,
        names: List<CharSequence?>,
        titleText: SbisTopNavigationTitleText
    ): Boolean {
        return if (width > 0 && titleMetaData.firstHiddenNameIndex > 0) {
            val lastVisibleName = names[titleMetaData.firstHiddenNameIndex - 1]!!
            titleMetaData.lastNameHiddenCharactersCount =
                getNameHiddenCharactersCount(lastVisibleName, titleText, width)
            firstNameInLine < titleMetaData.firstHiddenNameIndex && (
                titleMetaData.lastNameHiddenCharactersCount == 0 ||
                    lastVisibleName.length - titleMetaData.lastNameHiddenCharactersCount < MIN_VISIBLE_CHARS_COUNT
                )
        } else {
            false
        }
    }

    private fun getNameHiddenCharactersCount(
        name: CharSequence,
        titleText: SbisTopNavigationTitleText,
        neededWidth: Float
    ): Int {
        var newName = name
        val originalWidth = titleText.getTextWidth(newName.toString())
        var removedCharsCount = 0
        while (newName.isNotEmpty()) {
            if (originalWidth - titleText.getTextWidth(newName.toString()) > neededWidth) {
                return removedCharsCount
            }
            newName = newName.substring(0, newName.length - 1)
            removedCharsCount++
        }
        return 0
    }

    private class TextHandlerMetaData(var counterValue: Int) {
        var firstHiddenNameIndex = 0
        var lastNameHiddenCharactersCount = 0
    }

    companion object {
        private const val MIN_VISIBLE_CHARS_COUNT = 4
        private const val DELIMITER = ", "
    }
}