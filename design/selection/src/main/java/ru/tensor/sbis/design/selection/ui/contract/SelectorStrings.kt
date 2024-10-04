package ru.tensor.sbis.design.selection.ui.contract

import android.os.Parcelable
import java.io.Serializable
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.stubview.StubViewCase
import ru.tensor.sbis.design.stubview.StubViewImageType
import ru.tensor.sbis.design.R as RDesign

/**
 * Комплект строк для компонента выбора, которые специфичны для предметной области
 *
 * @author ma.kolpakov
 */
@Parcelize
data class SelectorStrings(
    /**
     * Сообщение о превышении предела выбранных элементов. Поддерживается вставка предельного количества в строку
     *
     * @see R.string.selection_limit_exceeded
     */
    @StringRes val limitExceeded: Int = R.string.selection_limit_exceeded,
    /**
     * Заголовок сообщения об отсутствии результатов при выбранных параметрах фильтрации
     */
    @StringRes val notFoundTitle: Int = StubViewCase.NO_SEARCH_RESULTS.messageRes,
    /**
     * Описание сообщения об отсутствии результатов при выбранных параметрах фильтрации
     */
    @StringRes val notFoundDescription: Int = StubViewCase.NO_SEARCH_RESULTS.detailsRes,
    /**
     * Иконка сообщения об отсутствии результатов при выбранных параметрах фильтрации
     */
    val notFoundIcon: StubViewImageType = StubViewCase.NO_SEARCH_RESULTS.imageType,
    /**
     * Подсказка в поисковой строке
     */
    @StringRes val searchHint: Int = RDesign.string.design_search_panel_hint,
    /**
     * Заголовок сообщения о выборе всех доступных элементов
     */
    @StringRes val allSelectedTitle: Int = R.string.selection_all_items_selected,
    /**
     * Иконка заглушки при выборе всех доступных элементов
     */
    val allSelectedIcon: StubViewImageType = StubViewCase.NO_SEARCH_RESULTS.imageType,
    /**
     * Описание в заглушке при выборе всех доступных элементов
     */
    @StringRes val allSelectedDescription: Int? = null
) : Parcelable, Serializable