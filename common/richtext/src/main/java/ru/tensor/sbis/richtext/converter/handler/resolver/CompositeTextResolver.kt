package ru.tensor.sbis.richtext.converter.handler.resolver

/**
 * Резолвер, который является оберткой над несколькими резолверами.
 * Используется в случае если к тексту необходимо применить несколько независимых резолверов.
 *
 * @author am.boldinov
 */
internal class CompositeTextResolver(
    private vararg val resolvers: SourceTextResolver
) : SourceTextResolver {

    override fun resolve(text: String): String {
        var result = text
        resolvers.forEach {
            result = it.resolve(result)
        }
        return result
    }
}