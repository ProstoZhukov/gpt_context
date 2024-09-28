package ru.tensor.sbis.crud4.view.items

import android.text.TextUtils
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import ru.tensor.sbis.crud4.view.viewmodel.ItemActionDelegate
import ru.tensor.sbis.crud4.R
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.theme.global_variables.FontSize
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.theme.global_variables.StyleColor
import ru.tensor.sbis.design.theme.global_variables.TextColor
import ru.tensor.sbis.service.DecoratedProtocol

/**
 * Контент для базовой папки.
 * @param folderDataProvider провайдер данных для папки из прикладной модели.
 *
 * @author ma.kolpakov
 */
class DefaultFolderContent<DATA : DecoratedProtocol<IDENTIFIER>, IDENTIFIER>(
    private val folderDataProvider: FolderDataProvider<DATA>
) :
    ViewHolderDelegate<DATA, IDENTIFIER> {

    private lateinit var folderNameView: SbisTextView
    private lateinit var folderCountView: SbisTextView
    private lateinit var expanderView: SbisTextView

    override fun onBind(item: DATA, itemActionDelegate: ItemActionDelegate<DATA, IDENTIFIER>) {
        folderNameView.text = folderDataProvider.provideName(item)
        folderDataProvider.provideCount(item).let { countText ->
            folderCountView.apply {
                text = countText
                isVisible = !countText.isNullOrEmpty()
            }
        }
        expanderView.rotation = getExpanderRotation(item)
        expanderView.setOnClickListener {
            itemActionDelegate.expandFolderClick(item)
        }
        folderNameView.setOnClickListener {
            itemActionDelegate.openFolderClick(item)
        }
    }

    override fun createView(parentView: ViewGroup) = ConstraintLayout(parentView.context).apply {
        addView(
            SbisTextView(parentView.context, RDesign.style.MobileFontStyle).apply {
                id = R.id.crud4_item_expander

                textSize = FontSize.M.getScaleOnDimenPx(parentView.context).toFloat()
                setTextColor(StyleColor.SECONDARY.getTextColor(parentView.context))
                text = parentView.context.getString(RDesign.string.design_mobile_icon_video_play)

                expanderView = this
            },
            ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                topToTop = R.id.crud4_item_text
                bottomToBottom = R.id.crud4_item_text
                startToStart = ConstraintSet.PARENT_ID
            }
        )
        addView(
            SbisTextView(parentView.context).apply {
                isSingleLine = true
                ellipsize = TextUtils.TruncateAt.END

                id = R.id.crud4_item_text
                textSize = FontSize.M.getScaleOnDimenPx(parentView.context).toFloat()
                setTextColor(TextColor.DEFAULT.getValue(parentView.context))

                folderNameView = this
            },
            ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                .apply {
                    val offset = Offset.XS.getDimenPx(parentView.context)
                    marginStart = offset
                    marginEnd = offset
                    goneStartMargin = 0
                    goneEndMargin = 0
                    startToEnd = R.id.crud4_item_expander
                    endToStart = R.id.crud4_item_count
                    constrainedWidth = true
                }
        )
        addView(
            SbisTextView(parentView.context).apply {
                isSingleLine = true

                id = R.id.crud4_item_count
                textSize = FontSize.M.getScaleOnDimenPx(parentView.context).toFloat()
                setTextColor(TextColor.LABEL.getValue(parentView.context))

                folderCountView = this
            },
            ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                .apply {
                    marginStart = Offset.XS.getDimenPx(parentView.context)
                    endToEnd = ConstraintSet.PARENT_ID
                    baselineToBaseline = R.id.crud4_item_text
                }
        )
    }

    private fun getExpanderRotation(item: DATA) = if (item.isExpanded) 90f else 0f

    /**
     * Провайдер данных для папки из прикладной модели.
     */
    interface FolderDataProvider<DATA> {

        /**@SelfDocumented**/
        fun provideName(data: DATA): String

        /**@SelfDocumented**/
        fun provideCount(data: DATA): String? = null
    }
}