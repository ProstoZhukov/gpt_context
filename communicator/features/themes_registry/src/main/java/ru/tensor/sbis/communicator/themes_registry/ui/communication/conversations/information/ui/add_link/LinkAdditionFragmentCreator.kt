package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.add_link

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.container.ContentCreator
import ru.tensor.sbis.design.container.FragmentContent
import ru.tensor.sbis.design.container.SbisContainer
import java.util.UUID

/**
 * Реализация создателя контента для компонента контейнера [SbisContainer].
 *
 * @author dv.baranov
 */
@Parcelize
internal class LinkAdditionFragmentCreator(
    private val themeUuid: UUID
) : ContentCreator<FragmentContent>, Parcelable {

    override fun createContent(): FragmentContent = LinkAdditionDialogContainer(themeUuid)
}