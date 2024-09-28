package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.common.di.CommonSingletonComponentProvider
import ru.tensor.sbis.common.util.AdjustResizeHelper.KeyboardEventListener
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.communication_decl.conversation_information.ConversationInformationSearchableContent
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.themes_registry.R
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.data.ConversationLinksListParams
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.di.DaggerConversationLinksListComponent
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.ui.ConversationLinksListController
import java.util.UUID

/**
 * Экран списка ссылок для информации о диалоге/канале.
 *
 * @author dv.baranov
 */
internal class ConversationLinksListFragment :
    BaseFragment(),
    KeyboardEventListener,
    ConversationInformationSearchableContent {

    companion object {

        /**
         * Создать новый инстанс экрана списка ссылок для информации о диалоге/канале.
         *
         * @param
         */
        fun newInstance(
            themeUUID: UUID
        ): Fragment =
            ConversationLinksListFragment().withArgs {
                putSerializable(THEME_UUID_KEY, themeUUID)
            }
    }

    private lateinit var controller: ConversationLinksListController

    private val fragmentLayout: Int
        get() = R.layout.communicator_fragment_conversation_links_list

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val themeUuid = arguments?.getSerializable(THEME_UUID_KEY)?.castTo<UUID>() ?: UUIDUtils.NIL_UUID
        DaggerConversationLinksListComponent.factory().create(
            CommonSingletonComponentProvider.get(requireContext()),
            ConversationLinksListParams(themeUuid),
            this,
            this.lifecycleScope
        ).also {
            controller = it.injector().inject(this, it.viewFactory)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflate(inflater, fragmentLayout, container, false)

    override fun onBackPressed(): Boolean = controller.onBackPressed()

    override fun onKeyboardOpenMeasure(keyboardHeight: Int): Boolean {
        return false
    }

    override fun onKeyboardCloseMeasure(keyboardHeight: Int): Boolean {
        return false
    }

    override fun setSearchQuery(query: String) {
        controller.setSearchQuery(query)
    }
}

internal const val THEME_UUID_KEY = "THEME_UUID_KEY"
