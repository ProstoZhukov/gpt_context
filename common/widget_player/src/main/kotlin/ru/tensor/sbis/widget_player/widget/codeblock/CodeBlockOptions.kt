package ru.tensor.sbis.widget_player.widget.codeblock

import ru.tensor.sbis.widget_player.config.WidgetOptionsBuilder
import ru.tensor.sbis.widget_player.converter.style.BackgroundColor
import ru.tensor.sbis.widget_player.res.DesignAttr
import ru.tensor.sbis.widget_player.res.WidgetColor
import ru.tensor.sbis.widget_player.res.color.ColorRes
import ru.tensor.sbis.widget_player.res.color.attr
import ru.tensor.sbis.widget_player.res.color.id
import ru.tensor.sbis.widget_player.res.dimen.DimenRes
import ru.tensor.sbis.widget_player.res.dimen.attr

/**
 * @author am.boldinov
 */
class CodeBlockOptions(
    val borderRadius: DimenRes,
    val borderThickness: DimenRes,
    val backgroundColor: BackgroundColor,
    val borderColor: ColorRes,
    val padding: DimenRes,
    val colorPalette: Map<String, ColorRes>,
    val cssColorPalette: Map<String, ColorRes>
)

class CodeBlockOptionsBuilder : WidgetOptionsBuilder<CodeBlockOptions>() {

    var borderRadius: DimenRes = DimenRes.attr(DesignAttr.borderRadius_2xs)

    var borderThickness: DimenRes = DimenRes.attr(DesignAttr.borderThickness_s)

    var backgroundColor: BackgroundColor = BackgroundColor.attr(DesignAttr.unaccentedBackgroundColor)

    var borderColor: ColorRes = ColorRes.attr(DesignAttr.borderColor)

    var padding: DimenRes = DimenRes.attr(DesignAttr.offset_xl)

    private val colorPalette = mapOf(
        "cm-comment" to ColorRes.attr(DesignAttr.unaccentedTextColor),
        "cm-keyword" to ColorRes.id(WidgetColor.widget_player_code_block_token_js_keyword),
        "cm-property" to ColorRes.id(WidgetColor.widget_player_code_block_token_js_property),
        "cm-atom" to ColorRes.id(WidgetColor.widget_player_code_block_token_js_bool),
        "cm-number" to ColorRes.id(WidgetColor.widget_player_code_block_token_js_number),
        "cm-def" to ColorRes.id(WidgetColor.widget_player_code_block_token_js_func),
        "cm-variable-2" to ColorRes.id(WidgetColor.widget_player_code_block_token_js_var),
        "cm-variable" to ColorRes.id(WidgetColor.widget_player_code_block_token_js_var),
        "cm-type" to ColorRes.id(WidgetColor.widget_player_code_block_token_type3),
        "cm-variable-3" to ColorRes.id(WidgetColor.widget_player_code_block_token_type3),
        "cm-string" to ColorRes.id(WidgetColor.widget_player_code_block_token_js_string),
        "cm-string-2" to ColorRes.id(WidgetColor.widget_player_code_block_token_string2),
        "cm-qualifier" to ColorRes.id(WidgetColor.widget_player_code_block_token_css_selector),
        "cm-tag" to ColorRes.id(WidgetColor.widget_player_code_block_token_css_selector),
        "cm-attribute" to ColorRes.id(WidgetColor.widget_player_code_block_token_html_attr),
        "cm-meta" to ColorRes.id(WidgetColor.widget_player_code_block_token_html_meta),
        "cm-builtin" to ColorRes.id(WidgetColor.widget_player_code_block_token_css_selector)
    )

    private val cssColorPalette = colorPalette.plus(
        mapOf(
            "cm-property" to ColorRes.id(WidgetColor.widget_player_code_block_token_css_property),
            "cm-keyword" to ColorRes.id(WidgetColor.widget_player_code_block_token_css_keyword),
            "cm-atom" to ColorRes.id(WidgetColor.widget_player_code_block_token_css_keyword),
            "cm-number" to ColorRes.id(WidgetColor.widget_player_code_block_token_css_number),
            "cm-string" to ColorRes.id(WidgetColor.widget_player_code_block_token_css_string)
        )
    )

    override fun build() = CodeBlockOptions(
        borderRadius = borderRadius,
        borderThickness = borderThickness,
        backgroundColor = backgroundColor,
        borderColor = borderColor,
        padding = padding,
        colorPalette = colorPalette,
        cssColorPalette = cssColorPalette
    )

}