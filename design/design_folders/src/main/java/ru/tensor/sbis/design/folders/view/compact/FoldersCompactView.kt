package ru.tensor.sbis.design.folders.view.compact

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.recyclerview.widget.LinearLayoutManager
import ru.tensor.sbis.design.folders.R
import ru.tensor.sbis.design.folders.databinding.DesignFoldersViewCompactBinding
import ru.tensor.sbis.design.folders.view.compact.adapter.FoldersCompactAdapter
import ru.tensor.sbis.design.utils.ThemeContextBuilder

/**
 * Свёрнутое отображение папок.
 * Отображаются только корневые папки (нулевого уровня вложенности)
 *
 * @see FolderCompactViewController
 * @author ma.kolpakov
 */
internal class FoldersCompactView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.foldersViewTheme,
    @StyleRes defStyleRes: Int = R.style.FoldersDefaultTheme,
    viewController: FolderCompactViewController = FolderCompactViewControllerImpl()
) : FrameLayout(
    ThemeContextBuilder(context, attrs, defStyleAttr, defStyleRes).build(),
    attrs,
    defStyleAttr,
    defStyleRes
),
    FolderCompactViewController by viewController {

    private val viewBinding = DesignFoldersViewCompactBinding.inflate(
        LayoutInflater.from(getContext()), this, true
    )
    private val foldersAdapter = FoldersCompactAdapter()

    /**
     * Признак необходимости заготовить несколько ячеек папок для первого синхронного отображения.
     */
    var prepareFolderItems: Boolean
        get() = foldersAdapter.prepareFolderItems
        set(value) {
            foldersAdapter.prepareFolderItems = value
        }

    /**
     * текущая позиция списка, нужна для сохранения после поворота во viewModel, через saveState не удалось реализовать
     * из- за особенностей встраивание view на экране (как ячейка recycler view)
     */
    var currentPosition: Int
        get() = (viewBinding.designFoldersList.layoutManager as? LinearLayoutManager)
            ?.run { findFirstVisibleItemPosition() }
            ?: 0
        set(value) {
            viewBinding.designFoldersList.post {
                viewBinding.designFoldersList.scrollToPosition(value)
            }
        }

    init {
        viewController.setAdapter(foldersAdapter)
        viewBinding.designFoldersList.apply {
            itemAnimator = null
            adapter = foldersAdapter
        }
    }
}
