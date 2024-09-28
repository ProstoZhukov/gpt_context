package ru.tensor.sbis.design.folders

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.withStyledAttributes
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.folders.databinding.DesignFoldersViewMainBinding
import ru.tensor.sbis.design.folders.view.compact.FoldersCompactView
import ru.tensor.sbis.design.utils.ThemeContextBuilder

/**
 * Компонент для отображения папок.
 *
 * Два состояния:
 * * Свёрнутое - одна строка с горизонтальной прокруткой
 * * Развёрнутое - вертикальный список
 *
 * Ссылки:
 * * [Стандарт](http://axure.tensor.ru/MobileStandart8/#p=%D0%BF%D0%B0%D0%BF%D0%BA%D0%B8_%D1%82%D0%B5%D1%85%D1%80%D0%B5%D1%88%D0%B5%D0%BD%D0%B8%D0%B5&g=1)
 * * [API](https://n.sbis.ru/shared/disk/f37b6257-2214-4583-a946-be0c5a396804)
 *
 * @author ma.kolpakov
 */
class FoldersView private constructor(
    context: Context,
    attrs: AttributeSet?,
    @AttrRes defStyleAttr: Int,
    @StyleRes defStyleRes: Int,
    private val viewController: FoldersViewControllerImpl = FoldersViewControllerImpl()
) : FrameLayout(
    ThemeContextBuilder(context, attrs, defStyleAttr, defStyleRes).build(),
    attrs,
    defStyleAttr,
    defStyleRes
),
    FoldersViewController by viewController {

    /**
     * @see [FoldersCompactView.currentPosition]
     */
    var compactFoldersViewListPosition: Int
        set(value) {
            viewBinding.designFoldersCompact.currentPosition = value
        }
        get() = viewBinding.designFoldersCompact.currentPosition

    /**
     * Включено ли отображение проваливания в папку
     */
    var isShownCurrentFolder: Boolean = true

    /**
     * Флаг принудительного скрытия иконки показа компактной/полной панели папок.
     */
    var isShownLeftFolderIcon: Boolean
        get() = viewBinding.designFoldersCompact.isShownLeftFolderIcon
        set(value) {
            viewBinding.designFoldersCompact.isShownLeftFolderIcon = value
            viewBinding.designFoldersFull.isShownLeftFolderIcon = value
        }

    /**
     * Флаг наличия нижней границы у панели папок
     */
    var isBorderHidden: Boolean = false
        set(value) {
            viewBinding.bottomBorder.isVisible = !value
            field = value
        }

    /**
     * Признак необходимости заготовить несколько ячеек папок для первого синхронного отображения.
     */
    var prepareFolderItems: Boolean
        get() = viewBinding.designFoldersCompact.prepareFolderItems
        set(value) {
            viewBinding.designFoldersCompact.prepareFolderItems = value
        }

    internal fun onlyRootFolder() = viewController.onlyRootFolder

    private val viewBinding = DesignFoldersViewMainBinding.inflate(LayoutInflater.from(getContext()), this)

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes defStyleAttr: Int = R.attr.foldersViewTheme,
        @StyleRes defStyleRes: Int = R.style.FoldersDefaultTheme,
    ) : this(context, attrs, defStyleAttr, defStyleRes, FoldersViewControllerImpl())

    init {
        viewController.setViews(
            viewBinding.root,
            viewBinding.designFoldersCompact,
            viewBinding.designFoldersFull,
            viewBinding.designFoldersCurrentFolder
        )
        getContext().withStyledAttributes(attrs, R.styleable.FoldersView, defStyleAttr, defStyleRes) {
            viewController.isExpandable = getBoolean(R.styleable.FoldersView_FoldersView_isExpandable, true)
            isBorderHidden = getBoolean(R.styleable.FoldersView_FoldersView_isBorderHidden, false)
            isShownCurrentFolder = getBoolean(R.styleable.FoldersView_FoldersView_isShownCurrentFolder, true)
            isShownLeftFolderIcon = getBoolean(R.styleable.FoldersView_FoldersView_isShownLeftFolderIcon, true)
        }
    }

    /**
     * При использовании в [RecyclerView] необходимо явно вызывать этот метод для сохранения открытого свайп-меню
     */
    fun onSaveInstanceState(outState: Bundle) {
        viewBinding.designFoldersFull.saveSwipeMenuState(outState)
    }

    /**
     * При использовании в [RecyclerView] необходимо явно вызывать этот метод для восстановления открытого свайп-меню
     */
    fun onRestoreInstanceState(savedInstanceState: Bundle) {
        viewBinding.designFoldersFull.restoreSwipeMenuState(savedInstanceState)
    }

    /**@see FoldersViewControllerImpl.showCompactFolders */
    fun showCompactFolders() = viewController.showCompactFolders()

    /**@see FoldersViewControllerImpl.showFullFolders */
    internal fun showFullFolders() = viewController.showFullFolders()

    /**@see FoldersViewControllerImpl.showCurrentFolder */
    internal fun showCurrentFolder(folderName: String) =
        if (isShownCurrentFolder) viewController.showCurrentFolder(folderName) else Unit

    /**@see FoldersViewControllerImpl.onCurrentFolderClicked */
    internal fun onCurrentFolderClicked(handler: () -> Unit) = viewController.onCurrentFolderClicked(handler)

    /**@see FoldersViewControllerImpl.clearListeners */
    internal fun clearListeners() = viewController.clearListeners()
}
