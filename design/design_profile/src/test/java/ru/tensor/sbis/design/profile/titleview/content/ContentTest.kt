package ru.tensor.sbis.design.profile.titleview.content

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.tensor.sbis.design.profile_decl.titleview.Default
import ru.tensor.sbis.design.profile_decl.titleview.ListContent
import ru.tensor.sbis.design.profile_decl.titleview.TitleViewItem

/**
 * @author ns.staricyn
 */
class ContentTest {

    @Test
    fun `When content type is ListContent, then its title and subtitle are created by joining non-empty item data`() {
        val titles = listOf("title1", "title2", "title3")
        val subtitles = listOf("subtitle1", "", "subtitle3")
        val items = titles.zip(subtitles) { title, subtitle ->
            TitleViewItem(title = title, subtitle = subtitle)
        }
        val content = ListContent(items)

        assertEquals(titles.joinToString(), content.title)
        assertEquals(subtitles.filter { it.isNotEmpty() }.joinToString(), content.subtitle)
    }

    @Test
    fun `When content type is ListContent, and custom title and subtitle are set, then values in items are ignored`() {
        val titles = listOf("title1", "title2", "title3")
        val subtitles = listOf("subtitle1", "", "subtitle3")
        val items = titles.zip(subtitles) { title, subtitle ->
            TitleViewItem(title = title, subtitle = subtitle)
        }
        val content = ListContent(items, "custom title", "custom subtitle")

        assertEquals("custom title", content.title)
        assertEquals("custom subtitle", content.subtitle)
    }

    @Test
    fun `Given a set of model, when crating an instance of Default, then an actual data of TitleViewContent verified`() {
        val title = "title"
        val subtitle = "subtitle"
        val photoUrl = "url"
        val imagePlaceholderRes = 11

        val default = Default(title, subtitle, photoUrl, imagePlaceholderRes)

        assertEquals(title, default.title)
        assertEquals(subtitle, default.subtitle)
        assertEquals(photoUrl, default.imageUrl)
        assertEquals(imagePlaceholderRes, default.imagePlaceholderRes)
    }
}