package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.add_link

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.common.di.CommonSingletonComponentProvider
import ru.tensor.sbis.common.util.AdjustResizeHelper.KeyboardEventListener
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.themes_registry.R
import ru.tensor.sbis.communicator.themes_registry.databinding.CommunicatorFragmentLinkAdditionBinding
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.add_link.di.DaggerLinkAdditionComponent
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.add_link.ui.LinkAdditionViewImpl
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.THEME_UUID_KEY
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.data.ConversationLinksListParams
import ru.tensor.sbis.design.header.BaseHeader
import java.util.UUID

/**
 * Экран добавления ссылки.
 *
 * @author dv.baranov
 */
internal class LinkAdditionFragment :
    BaseFragment(),
    KeyboardEventListener {

    companion object {

        /**
         * Создать новый инстанс экрана добавления ссылки.
         *
         * @param themeUuid uuid переписки, по которой был открыт экран информации.
         */
        fun newInstance(
            themeUuid: UUID
        ): LinkAdditionFragment = LinkAdditionFragment().withArgs {
            putSerializable(THEME_UUID_KEY, themeUuid)
        }
    }

    private val fragmentLayout: Int
        get() = R.layout.communicator_fragment_link_addition

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val themeUuid = arguments?.getSerializable(THEME_UUID_KEY)?.castTo<UUID>() ?: UUIDUtils.NIL_UUID
        DaggerLinkAdditionComponent.factory().create(
            CommonSingletonComponentProvider.get(requireContext()),
            ConversationLinksListParams(themeUuid)
        ) {
            LinkAdditionViewImpl(
                CommunicatorFragmentLinkAdditionBinding.bind(
                    requireView(),
                ),
                (it.parent.parent as? ViewGroup)?.findViewById<ViewGroup>(R.id.header_view)?.children?.first() as? BaseHeader
            )
        }.also {
            it.injector().inject(this)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflate(inflater, fragmentLayout, container, false)

    override fun onBackPressed(): Boolean = false

    override fun onKeyboardOpenMeasure(keyboardHeight: Int): Boolean = false

    override fun onKeyboardCloseMeasure(keyboardHeight: Int): Boolean = false
}
