package ru.tensor.sbis.widget_player

import ru.tensor.sbis.richtext.span.decoratedlink.DecoratedLinkType
import ru.tensor.sbis.widget_player.contract.WidgetPlayerDependency
import ru.tensor.sbis.widget_player.contract.WidgetPlayerStoreInitializer
import ru.tensor.sbis.widget_player.contract.WidgetStoreBuilder
import ru.tensor.sbis.widget_player.converter.internal.ConverterParams
import ru.tensor.sbis.widget_player.widget.audio.AudioMessageWidgetComponent
import ru.tensor.sbis.widget_player.widget.blockquote.BlockQuoteWidgetComponent
import ru.tensor.sbis.widget_player.widget.codeblock.CodeBlockWidgetComponent
import ru.tensor.sbis.widget_player.widget.column.ColumnLayoutWidgetComponent
import ru.tensor.sbis.widget_player.widget.embed.EmbedWidgetComponent
import ru.tensor.sbis.widget_player.widget.header.HeaderLevel
import ru.tensor.sbis.widget_player.widget.header.HeaderWidgetComponent
import ru.tensor.sbis.widget_player.widget.image.ImageWidgetComponent
import ru.tensor.sbis.widget_player.widget.image.icon.IconWidgetComponent
import ru.tensor.sbis.widget_player.widget.infoblock.InfoBlockWidgetComponent
import ru.tensor.sbis.widget_player.widget.link.DecoratedLinkWidgetComponent
import ru.tensor.sbis.widget_player.widget.list.item.ListItemWidgetComponent
import ru.tensor.sbis.widget_player.widget.list.root.ListViewWidgetComponent
import ru.tensor.sbis.widget_player.widget.paragraph.ParagraphLevel
import ru.tensor.sbis.widget_player.widget.paragraph.ParagraphWidgetComponent
import ru.tensor.sbis.widget_player.widget.root.frame.FrameRootWidgetComponent
import ru.tensor.sbis.widget_player.widget.root.layout.RootLayoutWidgetComponent
import ru.tensor.sbis.widget_player.widget.spoiler.SpoilerWidgetComponent
import ru.tensor.sbis.widget_player.widget.table.TableWidgetComponent
import ru.tensor.sbis.widget_player.widget.tabs.TabContainerWidgetComponent
import ru.tensor.sbis.widget_player.widget.text.FormattedTextWidgetComponent
import ru.tensor.sbis.widget_player.widget.textlayout.TextLayoutWidgetComponent
import ru.tensor.sbis.widget_player.widget.video.VideoMessageWidgetComponent

/**
 * Регистрирует базовый обязательный набор виджетов.
 *
 * @author am.boldinov
 */
internal class WidgetPlayerDefaultInitializer(
    private val dependency: WidgetPlayerDependency
) : WidgetPlayerStoreInitializer {

    override fun WidgetStoreBuilder.initialize() {
        widget(
            ConverterParams.ReservedTag.TEXT_WIDGET,
            "ModuleEditor/formattedText:View",
            component = FormattedTextWidgetComponent()
        )
        widget(
            ConverterParams.ReservedTag.FRAME_ROOT,
            component = FrameRootWidgetComponent()
        )
        widget(
            "RootLayout", "FrameControls/rootLayout:RootLayout",
            component = RootLayoutWidgetComponent()
        )
        widget(
            "Emoji", "ModuleEditor/emoji:View",
            component = FormattedTextWidgetComponent()
        )
        widget(
            "TextLayout", "ModuleEditor/textLayout:View",
            component = TextLayoutWidgetComponent()
        )
        widget(
            "Paragraph", "ModuleEditor/paragraph:View",
            "Paragraph1", "ModuleEditor/paragraph1:View",
            component = ParagraphWidgetComponent(ParagraphLevel.P1)
        )
        widget(
            "Paragraph2", "ModuleEditor/paragraph2:View",
            component = ParagraphWidgetComponent(ParagraphLevel.P2)
        )
        widget(
            "Paragraph3", "ModuleEditor/paragraph3:View",
            component = ParagraphWidgetComponent(ParagraphLevel.P3)
        )
        widget(
            "Paragraph4", "ModuleEditor/paragraph4:View",
            component = ParagraphWidgetComponent(ParagraphLevel.P4)
        )
        widget(
            "Paragraph5", "ModuleEditor/paragraph5:View",
            component = ParagraphWidgetComponent(ParagraphLevel.P5)
        )
        widget(
            "Paragraph6", "ModuleEditor/paragraph6:View",
            component = ParagraphWidgetComponent(ParagraphLevel.P6)
        )
        widget(
            "Paragraph7", "ModuleEditor/paragraph7:View",
            component = ParagraphWidgetComponent(ParagraphLevel.P7)
        )
        widget(
            "Paragraph8", "ModuleEditor/paragraph8:View",
            component = ParagraphWidgetComponent(ParagraphLevel.P8)
        )
        widget(
            "Paragraph9", "ModuleEditor/paragraph9:View",
            component = ParagraphWidgetComponent(ParagraphLevel.P9)
        )
        widget(
            "Paragraph10", "ModuleEditor/paragraph10:View",
            component = ParagraphWidgetComponent(ParagraphLevel.P10)
        )
        widget(
            "ModuleEditor/header:View",
            component = HeaderWidgetComponent()
        )
        widget(
            "DocsEditor/documentHeader:View", "ModuleEditor/header0:View",
            component = HeaderWidgetComponent(HeaderLevel.H0)
        )
        widget(
            "Header", "Header1", "ModuleEditor/header1:View",
            component = HeaderWidgetComponent(HeaderLevel.H1)
        )
        widget(
            "Header2", "ModuleEditor/header2:View",
            component = HeaderWidgetComponent(HeaderLevel.H2)
        )
        widget(
            "Header3", "ModuleEditor/header3:View",
            component = HeaderWidgetComponent(HeaderLevel.H3)
        )
        widget(
            "Header4", "ModuleEditor/header4:View",
            component = HeaderWidgetComponent(HeaderLevel.H4)
        )
        widget(
            "Header5", "ModuleEditor/header5:View",
            component = HeaderWidgetComponent(HeaderLevel.H5)
        )
        widget(
            "Header6", "ModuleEditor/header6:View",
            component = HeaderWidgetComponent(HeaderLevel.H6)
        )
        widget(
            "Link", "ModuleEditor/link:View",
            component = DecoratedLinkWidgetComponent()
        )
        widget(
            "InlineLink", "ModuleEditor/inlineLink:View",
            component = DecoratedLinkWidgetComponent(DecoratedLinkType.SMALL)
        )
        widget(
            "Infoblock", "ModuleEditor/infoblock:View",
            component = InfoBlockWidgetComponent()
        )
        widget(
            "Image", "ModuleEditor/image:View",
            component = ImageWidgetComponent(dependency.viewerSliderIntentFactory)
        )
        widget(
            "Icon", "ModuleEditor/image:Icon",
            component = IconWidgetComponent()
        )
        widget(
            "Blockquote", "ModuleEditor/blockquote:View",
            component = BlockQuoteWidgetComponent()
        )
        widget(
            "List", "ModuleEditor/list:View",
            component = ListViewWidgetComponent()
        )
        widget(
            "ListItem", "ModuleEditor/list:Item",
            component = ListItemWidgetComponent()
        )
        widget(
            "ListItemContent", "ModuleEditor/list:ListItemContent",
            component = ParagraphWidgetComponent()
        )
        widget(
            "CodeBlock", "ModuleEditor/codeBlock:View",
            component = CodeBlockWidgetComponent()
        )
        widget(
            "Table", "ModuleEditor/table:View",
            component = TableWidgetComponent()
        )
        widget(
            "Columns", "FrameControls/columnsLayout:View",
            component = ColumnLayoutWidgetComponent()
        )
        widget(
            "Embed", "ModuleEditor/embed:View",
            component = EmbedWidgetComponent()
        )
        widget(
            "Spoiler", "ModuleEditor/spoiler:View",
            component = SpoilerWidgetComponent()
        )
        widget(
            "Controls-Containers/Tabs",
            component = TabContainerWidgetComponent()
        )
        if (BuildConfig.DEBUG) {
            dependency.audioMessageViewDataFactory?.let { viewDataFactory ->
                widget(
                    "ModuleEditor/media:audioMessage",
                    component = AudioMessageWidgetComponent(viewDataFactory)
                )
            }
            dependency.videoMessageViewDataFactory?.let { viewDataFactory ->
                widget(
                    "ModuleEditor/media:videoMessage",
                    component = VideoMessageWidgetComponent(viewDataFactory)
                )
            }
        }
    }
}