package ru.tensor.sbis.design.navigation.view.model.content

import android.view.View
import androidx.lifecycle.MutableLiveData
import ru.tensor.sbis.design.navigation.view.ItemContentFactory

/**
 * Реализация [ItemContentViewModel] при отсутствии дополнительного контента в элементах меню.
 *
 * @author ma.kolpakov
 */
internal object EmptyItemContentViewModel : ItemContentViewModel {

    override val contentVisible = MutableLiveData(false)
    override val contentExpanded = MutableLiveData(false)

    override val contentFactory: ItemContentFactory? = null

    override fun onExpandClicked(ignored: View, isExpand: Boolean?) =
        error("Unexpected request to expand empty content")
}