package ru.tensor.sbis.richtext.converter.json

import ru.tensor.sbis.richtext.converter.handler.resolver.SourceTextResolver
import ru.tensor.sbis.richtext.util.HtmlTag

/**
 * Резолвер, добавляющий br в пустые теги для добавления элементам минимальной высоты.
 * https://git.sbis.ru/sbis/engine/-/blob/rc-23.3225/client/RichEditor/_base/ViewerResolver.ts#L158
 *
 * @author am.boldinov
 */
internal class JsonEmptyTagResolver(
    private vararg val tags: String
) : SourceTextResolver {

    override fun resolve(text: String): String {
        if (text.isNotEmpty()) {
            var result = text
            tags.forEach { tag ->
                result = result.replace(
                    "[\"$tag\"]",
                    "[\"$tag\", [\"${HtmlTag.BR}\"]]"
                )
            }
            return result
        }
        return text
    }
}