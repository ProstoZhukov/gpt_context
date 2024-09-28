package ru.tensor.sbis.folderspanel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import ru.tensor.sbis.base_components.adapter.vmadapter.ViewModelAdapter
import ru.tensor.sbis.base_components.fragment.selection.shadow.RecyclerViewVisibilityDispatcher
import ru.tensor.sbis.base_components.fragment.selection.shadow.ShadowVisibilityDispatcher
import ru.tensor.sbis.common_filters.createFoldersHeaderItem
import ru.tensor.sbis.design.list_utils.decoration.drawer.divider.SolidDividerDrawer
import ru.tensor.sbis.design.list_utils.decoration.dsl.decorate
import ru.tensor.sbis.folderspanel.databinding.FolderspanelFragmentFoldersBinding
import ru.tensor.sbis.modalwindows.dialogalert.PopupConfirmation
import ru.tensor.sbis.mvp.fragment.selection.SelectionWindowFragment
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.common.R as RCommon

/**
 * Общий компонент папок. Используется для вывода списка папок (в задачах, сообщениях, контактах и др.)
 * и операций над ними (создание, удаление, переименовани и др.)
 * Реализован на основе компонента окна выбора [SelectionWindowFragment]
 * Макет: http://axure.tensor.ru/MobileAPP/#p=%D0%BF%D0%B0%D0%BF%D0%BA%D0%B8&g=1
 * Спецификация стилей на основе окна выбора: http://axure.tensor.ru/MobileAPP/#p=%D0%BE%D0%BA%D0%BD%D0%BE_%D0%B2%D1%8B%D0%B1%D0%BE%D1%80%D0%B0&g=1
 */
@Suppress("DIFFERENT_NAMES_FOR_THE_SAME_PARAMETER_IN_SUPERTYPES", "DEPRECATION", "OverridingDeprecatedMember")
@Deprecated("09.12.2020 Не используется. Будет удалено https://online.sbis.ru/opendoc.html?guid=7da62729-916c-424b-bf48-3eb60f3309cf")
abstract class BaseFolderPickPanel<PRESENTER : FolderPanelContract.Presenter<FolderPanelContract.View>> : SelectionWindowFragment<FolderPanelContract.View, PRESENTER>(),
    FolderPanelContract.View,
        PickNameDialogFragment.FolderPickNameDialogListener,
    FolderPickDialogFragment.FolderPickedListener,
        PopupConfirmation.DialogYesNoWithTextListener {

    private lateinit var folderList: FolderspanelFragmentFoldersBinding
    private var adapter = object: ViewModelAdapter() {
        init {
            cell<FolderViewModel>(R.layout.folderspanel_base_folder_list_item, areItemsTheSame = {
                oldItem, newItem -> oldItem.uuid == newItem.uuid
            })
        }
    }

    /**@SelfDocumented*/
    protected var selectedFolderUuid: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHeaderViewModel(createFoldersHeaderItem(mPresenter::onNewFolderClick))
    }

    //region AlertDialogFragment.YesNoListener
    /**
     * Callback подтверждения удаления папки
     * @param requestCode код диалогового окна
     */
    override fun onYes(requestCode: Int, text: String?) {
        if (requestCode == DIALOG_CODE_DELETE_FOLDER) {
            mPresenter.onFolderDeletionAccepted()
        }
    }

    override fun onNo(requestCode: Int, text: String?) {
        //ignore
    }
    //endregion

    //region FolderPickNameDialogListener
    /**
     * Callback подтверждения переименования папки
     * @param name введенное имя папки
     */
    override fun onNameAccepted(name: String) {
        mPresenter.onNameAcceptedFromPickNameDialog(name)
    }

    override fun onDialogClose() {
        //ignore
    }
    //endregion

    /**
     * Callback подтверждения перемещения папки в другую папку
     * @param folder uuid выбранной (целевой) папки
     */
    override fun onFolderPicked(folder: String) {
        mPresenter.onFolderPicked(folder)
    }

    //region SelectionWindowFragment
    override fun inflateContentView(inflater: LayoutInflater, container: ViewGroup) {
        folderList = FolderspanelFragmentFoldersBinding.inflate(inflater, container, true)
        folderList.folderspanelList.layoutManager = LinearLayoutManager(context)
        folderList.folderspanelList.decorate {
            setDrawer(SolidDividerDrawer(
                    ContextCompat.getColor(requireContext(), RDesign.color.palette_alpha_color_black1),
                    false,
                    resources.getDimensionPixelSize(RDesign.dimen.common_separator_size)))
        }
        folderList.folderspanelList.adapter = adapter
        folderList.executePendingBindings()
    }

    override fun getShadowVisibilityDispatcher(): ShadowVisibilityDispatcher? = RecyclerViewVisibilityDispatcher()
    //endregion

    //region BasePresenterFragment
    override fun getPresenterLoaderId(): Int {
        return RCommon.id.folders_presenter_loader_id
    }

    override fun getPresenterView() = this
    //endregion

    //region FolderPanelContract
    /**
     * Отображение списка папок
     * @param folders список папок (ViewModel-ей)
     */
    override fun showFolders(folders: List<FolderViewModel>) {
        adapter.reload(folders)
    }

    /**
     * Отображение диалогового окна для ввода имени при создании/переименовании папки
     * @param folderName текущее имя папки (или пустая строка)
     */
    override fun showPickNameDialog(folderName: String) {
        val dialogFragment = PickNameDialogFragment.newInstance(getString(getPickNameDialogTitle()), folderName, getString(getPickNameDialogHint()))
        dialogFragment.show(childFragmentManager, PickNameDialogFragment::class.java.canonicalName)
    }

    /**
     * Отображение диалогового окна для выбора целевой папки при перемещении
     * @param folders список целевых папок для перемещения
     */
    override fun showFolderPickDialog(folders: List<FolderViewModel>) {
        FolderPickDialogFragment.newInstance(folders)
            .requestTitle(getString(RCommon.string.common_dialogs_move_to_folder_contextual_menu_title))
            .requestPositiveButton(getString(RCommon.string.common_dialogs_move_to_folder_cancel))
            .show(childFragmentManager, FolderPickDialogFragment::class.java.canonicalName)
    }

    /**
     * Отображение диалогового окна удаления папки
     */
    override fun showDeletionAcceptDialog(title: String, message: String?, acceptButtonText: String, cancelButtonText: String) =
        if (message == null) {
            PopupConfirmation.newSimpleInstance(DIALOG_CODE_DELETE_FOLDER)
        } else {
            PopupConfirmation.newMessageInstance(DIALOG_CODE_DELETE_FOLDER, message)
        }
            .requestTitle(title)
            .requestNegativeButton(cancelButtonText)
            .requestPositiveButton(acceptButtonText, true)
            .setEventProcessingRequired(true)
            .show(childFragmentManager, PopupConfirmation::class.java.simpleName)
    //endregion

    //region SelectionWindowFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            mPresenter.setInitFolder(selectedFolderUuid)
        }
    }
    //endregion

    override fun getContentScrollViewId(): Int {
        return R.id.folderspanel_list
    }

    /**@SelfDocumented*/
    @StringRes
    abstract fun getPickNameDialogTitle(): Int

    /**@SelfDocumented*/
    @StringRes
    protected open fun getPickNameDialogHint(): Int = RCommon.string.common_folder_edit_dialog_edittext_hint
}

private const val DIALOG_CODE_DELETE_FOLDER = 0x3210