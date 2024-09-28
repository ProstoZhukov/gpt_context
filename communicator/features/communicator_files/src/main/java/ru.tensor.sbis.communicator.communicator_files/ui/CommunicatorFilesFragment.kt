package ru.tensor.sbis.communicator.communicator_files.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.base_components.fragment.FragmentBackPress
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.communication_decl.conversation_information.ConversationInformationFilter
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.communicator_files.CommunicatorFilesPlugin.commonSingletonComponentProvider
import ru.tensor.sbis.communicator.communicator_files.R
import ru.tensor.sbis.communicator.communicator_files.data.CommunicatorFileAction
import ru.tensor.sbis.communicator.communicator_files.data.CommunicatorFileActionData
import ru.tensor.sbis.communicator.communicator_files.di.DaggerCommunicatorFilesComponent
import ru.tensor.sbis.communicator.communicator_files.utils.CommunicatorFilesAttachmentViewPool
import ru.tensor.sbis.communicator.declaration.CommunicatorFilesFragmentFactory
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.container.DimType
import ru.tensor.sbis.design.container.locator.HorizontalAlignment
import ru.tensor.sbis.design.context_menu.MenuItem
import ru.tensor.sbis.design.context_menu.SbisMenu
import ru.tensor.sbis.design.context_menu.showMenuWithScreenAlignment
import ru.tensor.sbis.design.files_picker.decl.SbisPickedItem
import ru.tensor.sbis.design_notification.SbisPopupNotification
import ru.tensor.sbis.design_notification.popup.SbisPopupNotificationStyle
import java.util.UUID

/**
 * Фрагмент списка файлов переписки.
 *
 * @author da.zhukov
 */
internal class CommunicatorFilesFragment : BaseFragment(),
    FragmentBackPress,
    ConversationInformationFilesContent {

    companion object : CommunicatorFilesFragmentFactory {

        private const val ORIENTATION = "ORIENTATION"

        const val THEME_UUID = "THEME_UUID"

        override fun createCommunicatorFilesListFragment(themeId: UUID): Fragment {
            return CommunicatorFilesFragment().withArgs {
                putSerializable(THEME_UUID, themeId)
            }
        }
    }

    private val viewPoolsHolder: CommunicatorFilesAttachmentViewPool by lazy {
        CommunicatorFilesAttachmentViewPool(requireContext())
            .apply { prepareViewPools() }
    }

    private var controller: CommunicatorFilesController? = null

    private val themeId by lazy {
        arguments?.getSerializable(THEME_UUID)?.castTo<UUID>() ?: UUIDUtils.NIL_UUID
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerCommunicatorFilesComponent.factory().create(
            scope = this.lifecycleScope,
            viewModelStoreOwner = this,
            commonSingletonComponent = commonSingletonComponentProvider.get(),
            themeId = themeId,
            viewPool = viewPoolsHolder
        ).also {
            controller = it.injector().inject(this, it.viewFactory)
            viewPoolsHolder.communicatorFileClickListener = controller
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(ORIENTATION, resources.configuration.orientation)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (savedInstanceState != null) {
            val previousOrientation = savedInstanceState.getInt(ORIENTATION)
            val currentOrientation = resources.configuration.orientation

            if (previousOrientation != currentOrientation) {
                controller?.onConfigurationChanged()
            }
        }
        return inflater.inflate(R.layout.communicator_files_fragment, container, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        controller = null
    }

    override fun onBackPressed(): Boolean {
        childFragmentManager.run {
            if (backStackEntryCount == 0) return super.onBackPressed()
            if (fragments.last().castTo<FragmentBackPress>()?.onBackPressed() == false) {
                popBackStack()
                return true
            }
        }
        return false
    }

    /**
     * Отобразить список действий над вложением.
     */
    fun showAttachmentActionList(view: View, actions: List<CommunicatorFileAction>, actionData: CommunicatorFileActionData) {
        val sbisMenu = SbisMenu(children = actions.map { action -> getActionMenuItem(action, actionData) })
        showMenu(sbisMenu, view)
    }

    /**
     * Отобразить уведомление об успешном перемещении файла.
     */
    fun showFileSuccessMoveToFolder() {
        SbisPopupNotification.push(
            SbisPopupNotificationStyle.SUCCESS,
            requireContext().getString(R.string.communicator_files_move_success_message),
            SbisMobileIcon.Icon.smi_Successful.character.toString()
        )
    }

    private fun getActionMenuItem(action: CommunicatorFileAction, actionData: CommunicatorFileActionData): MenuItem {
        with(action) {
            return MenuItem(title = getString(textRes), image = iconRes, destructive = destructive) {
                controller?.onFileActionClick(action, actionData)
            }
        }
    }

    private fun showMenu(
        sbisMenu: SbisMenu,
        anchor: View,
        alignment: HorizontalAlignment = HorizontalAlignment.CENTER,
        dimType: DimType = DimType.NONE
    ) {
        sbisMenu.showMenuWithScreenAlignment(
            fragmentManager = childFragmentManager,
            anchor = anchor,
            screenHorizontalAlignment = alignment,
            dimType = dimType,
        )
    }

    override fun setFilter(filter: List<ConversationInformationFilter>) {
        controller?.setFilter(filter)
    }

    override fun setSearchQuery(query: String) {
        controller?.setSearchQuery(query)
    }

    override fun addFiles(selectedFiles: List<SbisPickedItem>, compressImages: Boolean) {
        controller?.addFiles(selectedFiles, compressImages)
    }

    override fun createFolder(folderName: String) {
        controller?.createFolder(folderName)
    }
}