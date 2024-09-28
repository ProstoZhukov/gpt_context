package ru.tensor.sbis.common_filters

import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import ru.tensor.sbis.common_filters.R
import ru.tensor.sbis.common_filters.util.NO_RES_ID

/**
 * Заголовок окна выбора для списка фильтров.
 * Содержит заголовок, а также, опционально, функциональную кнопку, стрелку для возврата назад и кнопку с иконкой.
 * Заголовок можно задать как ресурс или строку
 *
 * @property titleRes ресурс текста заголовка
 * @property titleText строка с текстом заголовка
 * @param buttonTextRes ресурс текста кнопки
 * @property hasButton нужно ли отображать кнопку
 * @param isButtonEnabled должна ли кнопка быть активной
 * @property onButtonClick обработчик кликов по кнопке
 * @property hasBackArrow нужно ли отображать стрелку для возврата назад
 * @property onBackClick обработчик возврата назад по клику
 * @property iconButtonRes ресурс иконки
 * @property hasIconButton должна ли отображаться кнопка с иконкой
 * @property onIconButtonClick обработчик нажатий на кнопку с иконкой
 * @property icon иконка, отображаемая справа от заголовка
 * @property iconStyle стиль иконки, заданной в [icon]
 * @property count строка со значением счётчика, отображаемого справа от [icon]
 * @property isTitleScrollable должен ли заголовок иметь возможность прокручиваться, если не помещается полностью
 * (иначе текст будет сокращаться троеточием)
 */
@Suppress("DataClassPrivateConstructor")
data class FilterWindowHeaderItem private constructor(
    @StringRes
    val titleRes: Int,
    val titleText: String?,
    @StringRes
    val buttonTextRes: Int = R.string.common_filters_reset_button_label,
    val hasButton: Boolean = false,
    @Deprecated(
        "https://online.sbis.ru/opendoc.html?guid=f5bbfd61-cb98-4713-a0c2-ad6f856d796a. " +
            "Поле устарело. Вместо enabled нужно использовать visibility.",
        ReplaceWith("hasButton")
    )
    val isButtonEnabled: Boolean? = null,
    val onButtonClick: (() -> Unit)? = null,
    val hasBackArrow: Boolean = false,
    val onBackClick: (() -> Unit)? = null,
    @StringRes
    val iconButtonRes: Int = R.string.common_filters_mobile_icon_add_folder,
    val hasIconButton: Boolean = false,
    val onIconButtonClick: (() -> Unit)? = null,
    val icon: String? = null,
    @StyleRes
    val iconStyle: Int? = null,
    val count: String = "",
    val isTitleScrollable: Boolean = false
) {

    @JvmOverloads constructor(
        @StringRes
        titleRes: Int = R.string.common_filters_filter,
        @StringRes
        buttonTextRes: Int = R.string.common_filters_reset_button_label,
        hasButton: Boolean = false,
        isButtonEnabled: Boolean? = null,
        onButtonClick: (() -> Unit)? = null,
        hasBackArrow: Boolean = false,
        onBackClick: (() -> Unit)? = null,
        @StringRes
        iconButtonRes: Int = R.string.common_filters_mobile_icon_add_folder,
        hasIconButton: Boolean = false,
        onIconButtonClick: (() -> Unit)? = null,
        icon: String? = null,
        @StyleRes
        iconStyle: Int? = null,
        counter: String = "",
        isTitleScrollable: Boolean = false
    ) : this(
        titleRes,
        null,
        buttonTextRes,
        hasButton,
        isButtonEnabled,
        onButtonClick,
        hasBackArrow,
        onBackClick,
        iconButtonRes,
        hasIconButton,
        onIconButtonClick,
        icon,
        iconStyle,
        counter,
        isTitleScrollable
    )

    @JvmOverloads constructor(
        titleText: String,
        @StringRes
        buttonTextRes: Int = R.string.common_filters_reset_button_label,
        hasButton: Boolean = false,
        isButtonEnabled: Boolean? = null,
        onButtonClick: (() -> Unit)? = null,
        hasBackArrow: Boolean = false,
        onBackClick: (() -> Unit)? = null,
        @StringRes
        iconButtonRes: Int = R.string.common_filters_mobile_icon_add_folder,
        hasIconButton: Boolean = false,
        onIconButtonClick: (() -> Unit)? = null,
        icon: String? = null,
        @StyleRes
        iconStyle: Int? = null,
        counter: String = "",
        isTitleScrollable: Boolean = false
    ) : this(
        NO_RES_ID,
        titleText,
        buttonTextRes,
        hasButton,
        isButtonEnabled,
        onButtonClick,
        hasBackArrow,
        onBackClick,
        iconButtonRes,
        hasIconButton,
        onIconButtonClick,
        icon,
        iconStyle,
        counter,
        isTitleScrollable
    )
}

/**
 * Метод упрощенного создания заголовка для окна выбора папки.
 * Предусматривает отображение стандартного текста заголовка и кнопки добавления папки
 */
fun createFoldersHeaderItem(
    onAddFolderClick: () -> Unit,
    hasBackArrow: Boolean = false,
    onBackClick: (() -> Unit)? = null
) = FilterWindowHeaderItem(
    R.string.common_filters_folder_button_label,
    hasButton = false,
    hasBackArrow = hasBackArrow,
    onBackClick = onBackClick,
    hasIconButton = true,
    onIconButtonClick = onAddFolderClick
)