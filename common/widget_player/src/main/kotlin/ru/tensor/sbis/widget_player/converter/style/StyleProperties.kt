package ru.tensor.sbis.widget_player.converter.style

/**
 * @author am.boldinov
 */
class StyleProperties(
    override val fontSize: FontSize,
    override val textColor: TextColor,
    override val linkTextColor: TextColor,
    override val backgroundColor: BackgroundColor? = null,
    override val fontStyle: FontStyle = FontStyle.NORMAL,
    override val fontWeight: FontWeight = FontWeight.NORMAL,
    override val textAlignment: TextAlignment = TextAlignment.LEFT,
    override val textDecoration: TextDecoration = TextDecoration.NORMAL
) : WidgetProperties {

    internal inline fun reduce(block: StylePropertiesBuilder.() -> Unit): StyleProperties {
        val builder = StylePropertiesBuilder()
        block.invoke(builder)
        return StyleProperties(
            fontSize = builder.fontSize ?: fontSize,
            textColor = builder.textColor ?: textColor,
            linkTextColor = builder.linkTextColor ?: linkTextColor,
            backgroundColor = builder.backgroundColor ?: backgroundColor,
            fontStyle = builder.fontStyle ?: fontStyle,
            fontWeight = builder.fontWeight ?: fontWeight,
            textAlignment = builder.textAlignment ?: textAlignment,
            textDecoration = builder.textDecoration ?: textDecoration
        )
    }
}

class StylePropertiesBuilder(
    override var fontSize: FontSize? = null,
    override var textColor: TextColor? = null,
    override var linkTextColor: TextColor? = null,
    override var backgroundColor: BackgroundColor? = null,
    override var fontStyle: FontStyle? = null,
    override var fontWeight: FontWeight? = null,
    override var textAlignment: TextAlignment? = null,
    override var textDecoration: TextDecoration? = null
) : WidgetProperties {

    fun apply(properties: WidgetProperties) {
        fontSize = properties.fontSize
        textColor = properties.textColor
        linkTextColor = properties.linkTextColor
        backgroundColor = properties.backgroundColor
        fontStyle = properties.fontStyle
        fontWeight = properties.fontWeight
        textAlignment = properties.textAlignment
        textDecoration = properties.textDecoration
    }
}