package ru.tensor.sbis.design.folders.support.utils.stub_integration

import android.view.View
import android.view.ViewGroup
import androidx.annotation.Px
import androidx.core.view.updateLayoutParams
import ru.tensor.sbis.design.folders.FoldersView
import ru.tensor.sbis.design.utils.getDimenPx

/**
 * Объект для управления размерами заглушки в [container] в дависимости от состояния компонента
 * панель папок [FoldersView]
 *
 * @author ma.kolpakov
 */
class StubViewMediator internal constructor(
    private val container: View
) {

    /**
     * Не самое лучшее решение проблемы динамического размера контейнера. Встречается в случаях,
     * когда контейнер сжимается (при поднятии клавиатуры), но размер заглушки нужно рассчитать
     * для оригинальной высоты (без клавиатуры), чтобы не было смаргиваний
     */
    @Px
    private var containerHeight: Int = 0

    @Px
    private val folderViewCompactHeight: Int

    @Px
    private val folderViewItemHeight: Int

    private var constraints = ViewConstraints()

    /**
     * Заглушка (или другая реализация [View]), высотой которой нужно управлять. Обновлённая
     * высота устанавливаются в [ViewGroup.LayoutParams.height]. В качестве минимальной
     * используется высота из атрибута [View.getMinimumHeight]
     */
    var stubView: View? = null
        set(value) {
            field = value
            value ?: return
            value.applyConstrainsts(constraints)
        }

    init {
        with(container.context) {
            folderViewCompactHeight = getDimenPx(ru.tensor.sbis.design.R.attr.inlineHeight_m)
            folderViewItemHeight = getDimenPx(ru.tensor.sbis.design.R.attr.inlineHeight_m)
        }
    }

    fun setPadding(@Px bottomPadding: Int) {
        constraints = constraints.copy(bottomPadding = bottomPadding)
        stubView?.apply {
            applyConstrainsts(constraints)
        }
    }

    internal fun onContentChanged(isFolderViewCompact: Boolean, foldersCount: Int, isExistAdditionalCommand: Boolean) {
        constraints = constraints.copy(
            isFolderViewCompact = isFolderViewCompact,
            foldersCount = foldersCount,
            isExistAdditionalCommand = isExistAdditionalCommand
        )
        stubView?.apply {
            applyConstrainsts(constraints)
        }
    }

    private fun View.applyConstrainsts(constraints: ViewConstraints) {
        val (isFolderViewCompact, isExistAdditionalCommand, foldersCount, padding) = constraints
        val folderViewHeight = when {
            foldersCount == 0 && !isExistAdditionalCommand -> 0
            foldersCount == 0 && isExistAdditionalCommand -> folderViewCompactHeight
            isFolderViewCompact -> folderViewCompactHeight
            else -> folderViewItemHeight * foldersCount
        }
        containerHeight = maxOf(containerHeight, container.measuredHeight)
        updateLayoutParams {
            height = (containerHeight - folderViewHeight - padding).coerceAtLeast(minimumHeight)
        }
        if (!isInLayout) {
            requestLayout()
        }
    }

    private data class ViewConstraints(
        val isFolderViewCompact: Boolean = true,
        val isExistAdditionalCommand: Boolean = false,
        val foldersCount: Int = 0,
        @Px val bottomPadding: Int = 0
    )
}