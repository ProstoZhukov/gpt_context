package ru.tensor.sbis.widget_player.widget.infoblock

import ru.tensor.sbis.widget_player.config.WidgetOptionsBuilder
import ru.tensor.sbis.widget_player.converter.style.BackgroundColor
import ru.tensor.sbis.widget_player.res.DesignAttr
import ru.tensor.sbis.widget_player.res.WidgetDimen
import ru.tensor.sbis.widget_player.res.color.ColorRes
import ru.tensor.sbis.widget_player.res.color.attr
import ru.tensor.sbis.widget_player.res.dimen.DimenRes
import ru.tensor.sbis.widget_player.res.dimen.attr
import ru.tensor.sbis.widget_player.res.dimen.id

/**
 * @author am.boldinov
 */
class InfoBlockOptions(
    val marginTop: DimenRes,
    val marginBottom: DimenRes,
    val paddingTop: DimenRes,
    val paddingBottom: DimenRes,
    val paddingLeft: DimenRes,
    val paddingRight: DimenRes,
    val backgroundColor: BackgroundColor,
    val borderColor: ColorRes,
    val borderRadius: DimenRes,
    val borderThickness: DimenRes,
    val emojiSize: DimenRes,
    val emojiPadding: DimenRes,
    val emojiChar: String
)

class InfoBlockOptionsBuilder : WidgetOptionsBuilder<InfoBlockOptions>() {

    var marginTop: DimenRes = DimenRes.attr(DesignAttr.offset_m)
    var marginBottom: DimenRes = marginTop
    var paddingTop: DimenRes = DimenRes.attr(DesignAttr.offset_l)
    var paddingBottom: DimenRes = paddingTop
    var paddingLeft: DimenRes = DimenRes.attr(DesignAttr.offset_l)
    var paddingRight: DimenRes = paddingLeft

    var backgroundColor: BackgroundColor = BackgroundColor.attr(DesignAttr.contrastBackgroundColor)
    var borderColor: ColorRes = ColorRes.attr(DesignAttr.borderColor)
    var borderRadius: DimenRes = DimenRes.attr(DesignAttr.borderRadius_2xs)
    var borderThickness: DimenRes = DimenRes.attr(DesignAttr.borderThickness_s)

    var emojiSize: DimenRes = DimenRes.id(WidgetDimen.widget_player_info_block_emoji_size)
    var emojiPadding: DimenRes = DimenRes.id(WidgetDimen.widget_player_info_block_emoji_padding)
    var emojiChar: String = "\uD83D\uDCA1"

    override fun build() = InfoBlockOptions(
        marginTop = marginTop,
        marginBottom = marginBottom,
        paddingTop = paddingTop,
        paddingBottom = paddingBottom,
        paddingLeft = paddingLeft,
        paddingRight = paddingRight,
        backgroundColor = backgroundColor,
        borderColor = borderColor,
        borderRadius = borderRadius,
        borderThickness = borderThickness,
        emojiSize = emojiSize,
        emojiPadding = emojiPadding,
        emojiChar = emojiChar
    )
}
