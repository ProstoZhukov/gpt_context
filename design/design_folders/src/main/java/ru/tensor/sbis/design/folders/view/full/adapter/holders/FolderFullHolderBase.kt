package ru.tensor.sbis.design.folders.view.full.adapter.holders

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.StateListDrawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import ru.tensor.sbis.design.folders.data.model.Folder
import ru.tensor.sbis.design.folders.data.model.FolderItem
import ru.tensor.sbis.design.folders.view.full.adapter.FolderHolderResourceProvider

/**
 * Базовый вьюхолдер для элементов списка папок (сами папки и дополнительные команды)
 *
 * @param viewBinding представление view для отображения
 * @param resourceProvider провайдер ресурсов для доступа к цветам и размерам
 *
 * @author ma.kolpakov
 */
internal abstract class FolderFullHolderBase<T : FolderItem>(
    viewBinding: ViewBinding,
    protected val resourceProvider: FolderHolderResourceProvider,
) : RecyclerView.ViewHolder(viewBinding.root) {

    protected val selectorDrawable = StateListDrawable().apply {
        resourceProvider.pressedItemColor?.let {
            addState(it.first, ColorDrawable(it.second))
        }
    }

    private companion object {
        // Максимальная глубина смещения по спецификации = 3.
        // Так как уровни вложенности считаются от 0 используем индекс [максимальная глубина] - 1
        const val MAX_DEPTH_INDEX = 2
    }

    /**
     * Установка отступа слева в зависимости о глубины вложенности
     */
    protected fun View.setPaddingByDepth(item: FolderItem) {
        val depth = if (item is Folder) item.depthLevel else 0

        var leftPadding = resourceProvider.getFirstItemLeftPaddingPx()
        if (depth != 0) {
            val depthMultiplier = depth.coerceAtMost(MAX_DEPTH_INDEX)
            leftPadding += depthMultiplier * resourceProvider.getItemLeftPaddingPx()
        }
        setPadding(leftPadding, paddingTop, paddingRight, paddingBottom)
    }
}
