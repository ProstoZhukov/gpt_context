package ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.adapter.holder

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.tensor.sbis.base_components.adapter.AbstractViewHolder
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.themes_registry.R
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.model.ThemeParticipantListItem
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import java.lang.IllegalArgumentException

/**
 * ViewHolder папки с участниками чата.
 *
 * @param parentView view item'а.
 * @param onFolderItemClick действие по нажатию на папку.
 *
 * @author dv.baranov
 */
internal class ThemeParticipantFolderViewHolder(
    parentView: ViewGroup,
    private val onFolderItemClick: (folder: ThemeParticipantListItem.ThemeParticipantFolder) -> Unit = {},
) : AbstractViewHolder<ThemeParticipantListItem>(
    LayoutInflater.from(parentView.context).inflate(R.layout.communicator_theme_participant_folder_item, parentView, false),
) {

    private val folderTitle = itemView.findViewById<SbisTextView>(R.id.communicator_theme_participant_folder_title)
    private val countParticipantsSubtitle = itemView.findViewById<SbisTextView>(R.id.communicator_theme_participant_folder_counter)

    override fun bind(model: ThemeParticipantListItem) {
        super.bind(model)
        with(model.castTo<ThemeParticipantListItem.ThemeParticipantFolder>() ?: throw IllegalArgumentException()) {
            folderTitle.text = name
            countParticipantsSubtitle.text = if (count > 0) "($count)" else ""
            itemView.setOnClickListener { onFolderItemClick(this) }
        }
    }
}
