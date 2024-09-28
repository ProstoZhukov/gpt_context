package ru.tensor.sbis.design.navigation.view.model.content

import android.view.View
import androidx.lifecycle.LiveData
import ru.tensor.sbis.design.navigation.view.ItemContentFactory

/**
 * Вью модель дополнительного контента в элементах меню.
 *
 * @author ma.kolpakov
 */
internal interface ItemContentViewModel {

    /** Видимость дополнительного контента. */
    val contentVisible: LiveData<Boolean>

    /** Состояние "раскрытия" дополнительного контента. */
    val contentExpanded: LiveData<Boolean>

    /** Фабрика создания дополнительного контента. */
    val contentFactory: ItemContentFactory?

    /** Действие при нажатии на кнопку "раскрытия" дополнительного контента. */
    fun onExpandClicked(ignored: View, isExpand: Boolean? = null)
}