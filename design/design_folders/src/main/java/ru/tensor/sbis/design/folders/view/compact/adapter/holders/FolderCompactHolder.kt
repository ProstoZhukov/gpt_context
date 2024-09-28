package ru.tensor.sbis.design.folders.view.compact.adapter.holders

import androidx.core.view.marginLeft
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.chips.models.SbisChipsCaption
import ru.tensor.sbis.design.chips.models.SbisChipsIcon
import ru.tensor.sbis.design.folders.data.model.Folder
import ru.tensor.sbis.design.folders.databinding.DesignFoldersItemViewCompactBinding
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.design.theme.global_variables.FontSize
import ru.tensor.sbis.design.theme.global_variables.IconSize
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.design.R as RDesign

/**
 * Вьюхолдер для отображения компактного вида папки
 *
 * @author ma.kolpakov
 */
internal class FolderCompactHolder(
    private val binding: DesignFoldersItemViewCompactBinding
) : RecyclerView.ViewHolder(binding.root) {
    var onClickListener: (() -> Unit)? = null

    fun bind(folder: Folder, isFirst: Boolean, isLast: Boolean, isFolderIconVisible: Boolean) {
        binding.designFoldersItemCompact.apply {
            accentedCounter = folder.unreadContentCount
            unaccentedCounter = folder.totalContentCount
            title = SbisChipsCaption(folder.title, customSize = FontSize.XL)
            icon = if (folder.type.hasIcon)
                SbisChipsIcon(PlatformSbisString.Res(folder.type.iconRes), customSize = IconSize.M) else null
            updateMargins(isFirst, isLast, isFolderIconVisible, marginLeft)
            setOnClickListener { onClickListener?.invoke() }
        }
    }

    private fun updateMargins(
        isFirst: Boolean,
        isLast: Boolean,
        isFolderIconVisible: Boolean,
        innerHorizontalMargin: Int
    ) {
        val marginLeft =
            when {
                isFirst && isFolderIconVisible -> 0
                isFirst && !isFolderIconVisible -> itemView.context.getDimenPx(RDesign.attr.offset_xs)
                else -> itemView.context.getDimenPx(RDesign.attr.offset_m)
            }

        val marginRight =
            if (isLast) {
                itemView.context.getDimenPx(ru.tensor.sbis.design.R.attr.offset_m)
            } else {
                0
            }
        itemView.updateLayoutParams<RecyclerView.LayoutParams> {
            leftMargin = marginLeft - innerHorizontalMargin
            rightMargin = marginRight - innerHorizontalMargin
        }
    }
}
