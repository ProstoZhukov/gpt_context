package ru.tensor.sbis.design.whats_new.ui

import android.graphics.Rect
import android.graphics.drawable.PaintDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.theme.global_variables.InlineHeight
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.theme.global_variables.OtherColor
import ru.tensor.sbis.design.whats_new.R
import ru.tensor.sbis.design.whats_new.SbisWhatsNewPlugin

/**
 * Класс для создания пунктов списка "Что нового".
 *
 * @author ps.smirnyh
 */
internal class WhatsNewItemListFactory {

    private val textBounds = Rect()

    /** Добавить пункты "Что нового" в [container]. */
    fun setWhatsNewItems(container: ViewGroup) {
        val whatsNewText =
            container.context.getString(SbisWhatsNewPlugin.customizationOptions.whatsNewRes)
        val whatsNewItems = whatsNewText.split("|").filter { it.isNotBlank() }
        if (whatsNewItems.size == 1) {
            fillSingleItemWhatsNew(whatsNewText, container)
        } else {
            fillLotsItemWhatsNew(whatsNewItems, container)
        }
    }

    /**
     * Метод для создания одного пункта "Что нового" из строкового ресурса.
     */
    private fun fillSingleItemWhatsNew(whatsNewText: String, container: ViewGroup) {
        // Без отступов.
        val layoutParamsDefault = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        // Текст новой фичи.
        val descriptionTextView =
            SbisTextView(
                container.context,
                defStyleAttr = R.attr.sbisWhatsNewDescriptionStyle,
                defStyleRes = R.style.SbisWhatsNewDescriptionDefaultStyle
            ).apply {
                id = R.id.whats_new_description_text_id
                layoutParams = layoutParamsDefault
                gravity = Gravity.START
                includeFontPadding = false
                text = whatsNewText.trim()
            }

        container.addView(descriptionTextView)
    }

    /**
     * Метод для создания списка "Что нового" из строкового ресурса с разделителем "|".
     */
    private fun fillLotsItemWhatsNew(
        whatsNewArrayItems: List<String>,
        container: ViewGroup
    ) {
        for (whatsNewItem in whatsNewArrayItems) {
            // С отступом сверху.
            val layoutParamsWithMarginTop = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin =
                    if (whatsNewArrayItems.indexOf(whatsNewItem) == 0) 0 else Offset.L.getDimenPx(container.context)
            }

            // С отступами по бокам.
            val layoutParamsWithMarginLeft = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                leftMargin = Offset.L.getDimenPx(container.context)
            }

            // Контейнер для параграфа текста.
            val descriptionLayout = LinearLayout(container.context).apply {
                layoutParams = layoutParamsWithMarginTop
                orientation = LinearLayout.HORIZONTAL
                isBaselineAligned = false
            }

            // Текст новой фичи.
            val descriptionTextView =
                SbisTextView(
                    container.context,
                    defStyleAttr = R.attr.sbisWhatsNewDescriptionStyle,
                    defStyleRes = R.style.SbisWhatsNewDescriptionDefaultStyle
                ).apply {
                    id = R.id.whats_new_description_text_id
                    layoutParams = layoutParamsWithMarginLeft
                    gravity = Gravity.START
                    includeFontPadding = false
                    text = whatsNewItem.trim()
                }

            // Маркер списка.
            val descriptionViewDot = View(container.context).apply {
                val size = InlineHeight.X7S.getDimenPx(context) / 2
                layoutParams = ViewGroup.MarginLayoutParams(size, size).apply {
                    descriptionTextView.paint.getTextBounds(
                        descriptionTextView.text.toString(),
                        0,
                        descriptionTextView.text?.length ?: 0,
                        textBounds
                    )
                    topMargin = (textBounds.height() - size) / 2
                }
                background = PaintDrawable(OtherColor.BRAND.getValue(context)).apply {
                    setCornerRadius(size / 2f)
                }

            }

            descriptionLayout.addView(descriptionViewDot)
            descriptionLayout.addView(descriptionTextView)

            container.addView(descriptionLayout)
        }
    }
}