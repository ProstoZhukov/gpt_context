package ru.tensor.sbis.design.tabs.util

import android.content.Context
import android.view.View
import ru.tensor.sbis.design.tabs.api.SbisTabViewItemContent
import ru.tensor.sbis.design.tabs.tabItem.ContentView
import ru.tensor.sbis.design.tabs.tabItem.SbisTabItemStyleHolder
import ru.tensor.sbis.design.theme.res.SbisColor

/**
 * Фабрика создания [ContentView] из модели [SbisTabViewItemContent].
 *
 * @author da.zolotarev
 */
internal class SbisTabItemContentViewInflater(
    private val context: Context,
    private val styleHolder: SbisTabItemStyleHolder,
    private val parent: View,
    private val onEmitNewValue: (Boolean) -> Unit
) {
    private fun mapText(textContent: SbisTabViewItemContent.Text, customTitleColor: SbisColor?): ContentView.Text {
        val string = textContent.text.getCharSequence(context)

        return ContentView.Text(string, styleHolder, parent, customTitleColor?.getColor(context))
    }

    private fun mapAdditionalText(
        textContent: SbisTabViewItemContent.AdditionalText,
        customTitleColor: SbisColor?
    ): ContentView.AdditionalText {
        val string = textContent.text.getString(context)

        return ContentView.AdditionalText(string, customTitleColor?.getColor(context), styleHolder)
    }

    private fun mapCounter(counterContent: SbisTabViewItemContent.Counter): ContentView.Counter {
        return ContentView.Counter(
            context,
            counterContent.accentedCounter,
            counterContent.unaccentedCounter,
            styleHolder,
            onEmitNewValue
        )
    }

    private fun mapImage(imageDrawable: SbisTabViewItemContent.Image): ContentView.Image {
        return ContentView.Image(imageDrawable.image, styleHolder)
    }

    private fun mapIcon(iconContent: SbisTabViewItemContent.Icon, customIconColor: SbisColor?): ContentView.Icon {
        val icon = iconContent.textIcon.getString(context)
        val customSize = iconContent.customDimen?.getDimen(context)

        return ContentView.Icon(icon, customSize, customIconColor?.getColor(context), styleHolder)
    }

    private fun mapIconCounter(
        iconContent: SbisTabViewItemContent.IconCounter,
        customIconColor: SbisColor?
    ): ContentView.IconCounter {
        val icon = iconContent.textIcon.getString(context)
        val customSize = iconContent.customDimen?.getDimen(context)

        return ContentView.IconCounter(
            context,
            icon,
            customSize,
            customIconColor?.getColor(context),
            iconContent.counterValue,
            iconContent.counterStyle,
            styleHolder,
            onEmitNewValue
        )
    }

    /**
     * Создать [ContentView] из модели [SbisTabViewItemContent].
     */
    fun inflate(
        content: SbisTabViewItemContent,
        customTitleColor: SbisColor?,
        customIconColor: SbisColor?
    ): ContentView {
        return when (content) {
            is SbisTabViewItemContent.AdditionalText -> mapAdditionalText(content, customTitleColor)
            is SbisTabViewItemContent.Counter -> mapCounter(content)
            is SbisTabViewItemContent.Icon -> mapIcon(content, customIconColor)
            is SbisTabViewItemContent.IconCounter -> mapIconCounter(content, customIconColor)
            is SbisTabViewItemContent.Image -> mapImage(content)
            is SbisTabViewItemContent.Text -> mapText(content, customTitleColor)
        }
    }
}