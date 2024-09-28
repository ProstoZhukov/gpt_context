package ru.tensor.sbis.widget_player.config

import ru.tensor.sbis.plugin_struct.utils.SbisThemedContext
import ru.tensor.sbis.widget_player.WidgetPlayerPlugin
import ru.tensor.sbis.widget_player.widget.audio.AudioMessageOptions
import ru.tensor.sbis.widget_player.widget.audio.AudioMessageOptionsBuilder
import ru.tensor.sbis.widget_player.widget.codeblock.CodeBlockOptions
import ru.tensor.sbis.widget_player.widget.codeblock.CodeBlockOptionsBuilder
import ru.tensor.sbis.widget_player.widget.column.ColumnLayoutOptions
import ru.tensor.sbis.widget_player.widget.column.ColumnLayoutOptionsBuilder
import ru.tensor.sbis.widget_player.widget.embed.youtube.YouTubeOptions
import ru.tensor.sbis.widget_player.widget.embed.youtube.YouTubeOptionsBuilder
import ru.tensor.sbis.widget_player.widget.header.HeaderOptions
import ru.tensor.sbis.widget_player.widget.header.HeaderOptionsBuilder
import ru.tensor.sbis.widget_player.widget.image.ImageOptions
import ru.tensor.sbis.widget_player.widget.image.ImageOptionsBuilder
import ru.tensor.sbis.widget_player.widget.image.icon.IconOptions
import ru.tensor.sbis.widget_player.widget.image.icon.IconOptionsBuilder
import ru.tensor.sbis.widget_player.widget.infoblock.InfoBlockOptions
import ru.tensor.sbis.widget_player.widget.infoblock.InfoBlockOptionsBuilder
import ru.tensor.sbis.widget_player.widget.link.DecoratedLinkOptions
import ru.tensor.sbis.widget_player.widget.link.DecoratedLinkOptionsBuilder
import ru.tensor.sbis.widget_player.widget.list.root.ListViewOptions
import ru.tensor.sbis.widget_player.widget.list.root.ListViewOptionsBuilder
import ru.tensor.sbis.widget_player.widget.paragraph.ParagraphOptions
import ru.tensor.sbis.widget_player.widget.paragraph.ParagraphOptionsBuilder
import ru.tensor.sbis.widget_player.widget.root.layout.RootLayoutOptions
import ru.tensor.sbis.widget_player.widget.root.layout.RootLayoutOptionsBuilder
import ru.tensor.sbis.widget_player.widget.spoiler.SpoilerOptions
import ru.tensor.sbis.widget_player.widget.spoiler.SpoilerOptionsBuilder
import ru.tensor.sbis.widget_player.widget.table.TableOptions
import ru.tensor.sbis.widget_player.widget.table.TableOptionsBuilder
import ru.tensor.sbis.widget_player.widget.tabs.TabOptions
import ru.tensor.sbis.widget_player.widget.tabs.TabOptionsBuilder
import ru.tensor.sbis.widget_player.widget.text.TextOptions
import ru.tensor.sbis.widget_player.widget.text.TextOptionsBuilder
import ru.tensor.sbis.widget_player.widget.video.VideoMessageOptions
import ru.tensor.sbis.widget_player.widget.video.VideoMessageOptionsBuilder

/**
 * @author am.boldinov
 */
class WidgetOptions(
    val rootLayoutOptions: RootLayoutOptions,
    val textOptions: TextOptions,
    val decoratedLinkOptions: DecoratedLinkOptions,
    val paragraphOptions: ParagraphOptions,
    val headerOptions: HeaderOptions,
    val codeBlockOptions: CodeBlockOptions,
    val tableOptions: TableOptions,
    val columnLayoutOptions: ColumnLayoutOptions,
    val youTubeOptions: YouTubeOptions,
    val listViewOptions: ListViewOptions,
    val imageOptions: ImageOptions,
    val iconOptions: IconOptions,
    val infoBlockOptions: InfoBlockOptions,
    val spoilerOptions: SpoilerOptions,
    val tabOptions: TabOptions,
    val audioMessageOptions: AudioMessageOptions,
    val videoMessageOptions: VideoMessageOptions
)

class RTWidgetOptionsBuilder(
    context: SbisThemedContext
) : WidgetOptionsBuilder<WidgetOptions>() {

    private val rootLayoutBuilder = RootLayoutOptionsBuilder()
    private val textBuilder = TextOptionsBuilder()
    private val decoratedLinkBuilder = DecoratedLinkOptionsBuilder(context, WidgetPlayerPlugin.component)
    private val paragraphBuilder = ParagraphOptionsBuilder()
    private val headerBuilder = HeaderOptionsBuilder()
    private val codeBlockBuilder = CodeBlockOptionsBuilder()
    private val tableBuilder = TableOptionsBuilder()
    private val columnLayoutBuilder = ColumnLayoutOptionsBuilder()
    private val youTubeOptionsBuilder = YouTubeOptionsBuilder()
    private val listViewOptionsBuilder = ListViewOptionsBuilder()
    private val imageOptionsBuilder = ImageOptionsBuilder()
    private val iconOptionsBuilder = IconOptionsBuilder()
    private val infoBlockOptionsBuilder = InfoBlockOptionsBuilder()
    private val spoilerOptionsBuilder = SpoilerOptionsBuilder()
    private val tabOptionsBuilder = TabOptionsBuilder()
    private val audioMessageOptionsBuilder = AudioMessageOptionsBuilder()
    private val videoMessageOptionsBuilder = VideoMessageOptionsBuilder()

    fun rootLayout(init: RootLayoutOptionsBuilder.() -> Unit) {
        rootLayoutBuilder.apply(init)
    }

    fun text(init: TextOptionsBuilder.() -> Unit) {
        textBuilder.apply(init)
    }

    fun decoratedLink(init: DecoratedLinkOptionsBuilder.() -> Unit) {
        decoratedLinkBuilder.apply(init)
    }

    fun paragraph(init: ParagraphOptionsBuilder.() -> Unit) {
        paragraphBuilder.apply(init)
    }

    fun header(init: HeaderOptionsBuilder.() -> Unit) {
        headerBuilder.apply(init)
    }

    fun codeBlock(init: CodeBlockOptionsBuilder.() -> Unit) {
        codeBlockBuilder.apply(init)
    }

    fun table(init: TableOptionsBuilder.() -> Unit) {
        tableBuilder.apply(init)
    }

    fun columnLayout(init: ColumnLayoutOptionsBuilder.() -> Unit) {
        columnLayoutBuilder.apply(init)
    }

    fun youTube(init: YouTubeOptionsBuilder.() -> Unit) {
        youTubeOptionsBuilder.apply(init)
    }

    fun listView(init: ListViewOptionsBuilder.() -> Unit) {
        listViewOptionsBuilder.apply(init)
    }

    fun image(init: ImageOptionsBuilder.() -> Unit) {
        imageOptionsBuilder.apply(init)
    }

    fun icon(init: IconOptionsBuilder.() -> Unit) {
        iconOptionsBuilder.apply(init)
    }

    fun infoBlocK(init: InfoBlockOptionsBuilder.() -> Unit) {
        infoBlockOptionsBuilder.apply(init)
    }

    fun spoiler(init: SpoilerOptionsBuilder.() -> Unit) {
        spoilerOptionsBuilder.apply(init)
    }

    fun tab(init: TabOptionsBuilder.() -> Unit) {
        tabOptionsBuilder.apply(init)
    }

    fun audioMessage(init: AudioMessageOptionsBuilder.() -> Unit) {
        audioMessageOptionsBuilder.apply(init)
    }

    fun videoMessage(init: VideoMessageOptionsBuilder.() -> Unit) {
        videoMessageOptionsBuilder.apply(init)
    }

    override fun build(): WidgetOptions {
        return WidgetOptions(
            rootLayoutBuilder.build(),
            textBuilder.build(),
            decoratedLinkBuilder.build(),
            paragraphBuilder.build(),
            headerBuilder.build(),
            codeBlockBuilder.build(),
            tableBuilder.build(),
            columnLayoutBuilder.build(),
            youTubeOptionsBuilder.build(),
            listViewOptionsBuilder.build(),
            imageOptionsBuilder.build(),
            iconOptionsBuilder.build(),
            infoBlockOptionsBuilder.build(),
            spoilerOptionsBuilder.build(),
            tabOptionsBuilder.build(),
            audioMessageOptionsBuilder.build(),
            videoMessageOptionsBuilder.build()
        )
    }
}