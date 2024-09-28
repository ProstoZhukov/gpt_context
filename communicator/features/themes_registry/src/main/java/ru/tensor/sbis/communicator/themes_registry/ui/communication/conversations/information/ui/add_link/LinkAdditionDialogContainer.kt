package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.add_link

import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import ru.tensor.sbis.communicator.design.R
import ru.tensor.sbis.design.container.FragmentContent
import ru.tensor.sbis.design.container.SbisContainer
import ru.tensor.sbis.design.container.SbisContainerImpl
import ru.tensor.sbis.design.header.createHeader
import ru.tensor.sbis.design.header.data.HeaderAcceptSettings
import ru.tensor.sbis.design.header.data.HeaderTitleSettings
import java.util.UUID
import ru.tensor.sbis.communicator.themes_registry.R as RThemesRegistry

/**
 * Реализация контента для контейнера [SbisContainer].
 *
 * @author dv.baranov
 */
internal class LinkAdditionDialogContainer(
    private val themeUuid: UUID
) : FragmentContent {

    override fun getFragment(containerFragment: SbisContainerImpl): Fragment =
        LinkAdditionFragment.newInstance(themeUuid)

    override fun onRestoreFragment(containerFragment: SbisContainerImpl, fragment: Fragment) = Unit

    override fun theme(): Int = RThemesRegistry.style.CommunicatorLinkAdditionHeaderStyle

    override fun getHeaderView(context: Context, container: SbisContainer): View {
        return createHeader(
            context,
            HeaderTitleSettings.TextResTitle(R.string.communicator_conversation_link_addition_title),
            HeaderAcceptSettings.IconAccept,
            hasClose = true
        )
    }
}
