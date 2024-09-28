package ru.tensor.sbis.communicator.common.data.theme

import android.content.Context
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import ru.tensor.sbis.communicator.common.R
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.text_span.span.TextGravitySpan
import ru.tensor.sbis.richtext.converter.TagAttributes
import ru.tensor.sbis.richtext.converter.handler.base.TagHandler
import ru.tensor.sbis.design.text_span.span.TypefaceTextAppearanceSpan
import timber.log.Timber
import java.lang.Exception
import kotlin.math.roundToInt
import ru.tensor.sbis.design.R as RDesign

/**
 * Обработчик тэгов смайлов
 *
 * @param context - контекст
 */
internal class SmileTagHandler(
        private val context: Context
): TagHandler {
    companion object {
        const val TAG = "i"
        private const val ICON_TYPE_PREFIX = "SbisMobileIcon."
        const val ICON_REGEX = "$ICON_TYPE_PREFIX(.*?)[^\\s]+"
        const val COLOR_REGEX = "[a-zA-Z0-9]+Color"
        private const val ICON_NAME_PREFIX = "smi_"
    }

    object ReactionIconType {
        private const val SUCCESS_ICON_COLOR = "successIconColor"
        private const val DANGER_ICON_COLOR = "dangerIconColor"
        private const val WARNING_ICON_COLOR = "warningIconColor"
        private const val SECONDARY_ICON_COLOR = "secondaryIconColor"

        /** @SelfDocumented */
        fun mapToIconString(style: String): String {
            val index = style.indexOf(ICON_TYPE_PREFIX, startIndex = 0)
            val iconName = if (index == 0) {
                style.replaceRange(index, index + ICON_TYPE_PREFIX.length, ICON_NAME_PREFIX)
            } else {
                throw IllegalArgumentException("Unexpected icon prefix: \"$style\"")
            }
            val icon = SbisMobileIcon.Icon.values().find { it.name.compareTo(iconName, ignoreCase = true) == 0 }
            return icon?.character?.toString() ?: throw IllegalArgumentException("Unknown style in rich text: \"$style\"")
        }

        /**@SelfDocumented */
        fun mapToStyleResource(style: String) = when (style) {
            SUCCESS_ICON_COLOR -> R.style.CommunicatorEmotionText_Like
            SECONDARY_ICON_COLOR -> R.style.CommunicatorEmotionText_Shocked
            DANGER_ICON_COLOR -> R.style.CommunicatorEmotionText_Angry
            WARNING_ICON_COLOR -> R.style.CommunicatorEmotionText_Laugh
            else -> throw IllegalArgumentException("Unknown style in rich text \"$style\"")
        }
    }

    private val iconRegex: Regex by lazy {
        Regex(ICON_REGEX)
    }

    private val colorRegex: Regex by lazy {
        Regex(COLOR_REGEX)
    }

    /**@SelfDocumented */
    override fun onStartTag(stream: Editable, attributes: TagAttributes) {
        val cssClass = attributes.getValue("class")
        try {
            val iconSpannable = SpannableString(
                ReactionIconType.mapToIconString(findIconType(cssClass) ?: return)
            )
            iconSpannable.setSpan(
                TypefaceTextAppearanceSpan(
                    context,
                    ReactionIconType.mapToStyleResource(findIconStyle(cssClass) ?: return),
                    TypefaceManager.getSbisMobileIconTypeface(context)
                ), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            iconSpannable.setSpan(TextGravitySpan(calculateBaselineShift()), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            stream.append(iconSpannable)
        } catch (e: Exception) {
            Timber.e(e, "Full model is: ${attributes.getValue("class")}")
        }
    }

    /**@SelfDocumented */
    override fun onEndTag(stream: Editable) {

    }

    /**@SelfDocumented */
    override fun recycle() {

    }

    /**
     * Вычисляет сдвиг иконки вниз по отношению к тексту для лучшего визуального восприятия
     */
    private fun calculateBaselineShift(): Int = with(context.resources) {
        return (getDimension(RDesign.dimen.answer_emotion_icon_size)  * 0.132f).roundToInt()
    }

    private fun findIconType(tagString: String?): String? = tagString?.let {
        iconRegex.find(tagString)?.value
    }

    private fun findIconStyle(tagString: String?): String? = tagString?.let {
        colorRegex.find(tagString)?.value
    }
}