package ru.tensor.sbis.design.folders.view.full

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.withStyledAttributes
import ru.tensor.sbis.design.folders.R
import ru.tensor.sbis.design.folders.data.FolderActionHandler
import ru.tensor.sbis.design.folders.data.MoreClickHandler
import ru.tensor.sbis.design.folders.data.model.AdditionalCommand
import ru.tensor.sbis.design.folders.data.model.Folder
import ru.tensor.sbis.design.folders.databinding.DesignFoldersViewFullBinding
import ru.tensor.sbis.design.folders.view.full.FolderListViewMode.STAND_ALONE
import ru.tensor.sbis.design.folders.view.full.adapter.FolderHolderResourceProvider
import ru.tensor.sbis.design.folders.view.full.adapter.FoldersFullAdapter
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.swipeablelayout.SwipeableViewBinderHelper

/**
 * Развёрнутое отображение папок.
 * Отображаются папки всех уровней вложенности и дополнительные действия
 *
 * @author ma.kolpakov
 */
class FolderListView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.foldersViewTheme,
    @StyleRes defStyleRes: Int = R.style.FoldersDefaultTheme,
) : LinearLayout(
    ThemeContextBuilder(context, attrs, defStyleAttr, defStyleRes).build(),
    attrs,
    defStyleAttr,
    defStyleRes,
) {

    private val viewMode: FolderListViewMode

    init {
        lateinit var mode: FolderListViewMode
        getContext().withStyledAttributes(attrs, R.styleable.FolderListView, defStyleAttr, defStyleRes) {
            val viewModeOrdinal = getInt(R.styleable.FolderListView_FolderListView_viewMode, STAND_ALONE.ordinal)
            mode = FolderListViewMode.values()[viewModeOrdinal]
        }
        viewMode = mode
    }

    private val resourceProvider = FolderHolderResourceProvider(getContext(), viewMode)
    private val viewBinding = DesignFoldersViewFullBinding.inflate(LayoutInflater.from(getContext()), this, true)
    private val swipeHelper = SwipeableViewBinderHelper<String>()
    private val foldersAdapter = FoldersFullAdapter(resourceProvider, swipeHelper)
    private val viewController = FolderListViewController(foldersAdapter, viewBinding.designFoldersFolderIcon)

    /**
     * Флаг принудительного скрытия иконки закрытия полной панели папок.
     */
    internal var isShownLeftFolderIcon: Boolean
        set(value) {
            viewController.isShownLeftFolderIcon = value
            resourceProvider.isShownLeftFolderIcon = value
        }
        get() = viewController.isShownLeftFolderIcon

    init {
        resourceProvider.initStyle(getContext(), attrs, defStyleAttr, defStyleRes)
        viewBinding.designFoldersList.apply {
            itemAnimator = null
            adapter = foldersAdapter
            isNestedScrollingEnabled = viewMode == STAND_ALONE
        }
    }

    /**
     * Установка слушателя действия папки
     *
     * @param handler реализация слушателя действия папки
     */
    fun setActionHandler(handler: FolderActionHandler?) = viewController.setActionHandler(handler)

    /**
     * Установка данных папок
     *
     * @param folders список папок
     */
    fun setFolders(folders: List<Folder>) = viewController.setFolders(folders, isFolderIconVisible = false)

    /**
     * Установка данных папок
     *
     * @param id id папки [Folder.id]
     */
    fun setSelectedFolder(id: String?) = foldersAdapter.setSelectedFolder(id)

    override fun onSaveInstanceState(): Parcelable {
        return FolderListViewSavedState(super.onSaveInstanceState()).apply {
            swipeHelper.saveStates(folderListState)
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        with(state as FolderListViewSavedState) {
            super.onRestoreInstanceState(superState)
            swipeHelper.restoreStates(folderListState)
        }
    }

    /**
     * @see FolderListViewController.setFolders
     */
    internal fun setFolders(folders: List<Folder>, isFolderIconVisible: Boolean) =
        viewController.setFolders(folders, isFolderIconVisible)

    /**
     * @see FolderListViewController.setAdditionalCommand
     */
    internal fun setAdditionalCommand(command: AdditionalCommand?) = viewController.setAdditionalCommand(command)

    /**
     * @see FolderListViewController.onFold
     */
    internal fun onFold(action: () -> Unit) = viewController.onFold(action)

    /**
     * @see FolderListViewController.onMoreClicked
     */
    internal fun onMoreClicked(handler: MoreClickHandler?) = viewController.onMoreClicked(handler)

    /**
     * @see FolderListViewController.closeSwipeMenu
     */
    internal fun closeSwipeMenu() = viewController.closeSwipeMenu()

    /** @SelfDocumented */
    internal fun saveSwipeMenuState(outState: Bundle) {
        swipeHelper.saveStates(outState)
    }

    /** @SelfDocumented */
    internal fun restoreSwipeMenuState(savedInstanceState: Bundle) {
        swipeHelper.restoreStates(savedInstanceState)
    }
}
