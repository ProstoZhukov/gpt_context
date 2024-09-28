package ru.tensor.sbis.richtext.converter.sabydoc

import android.content.Context
import ru.tensor.sbis.common.util.CommonUtils
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.jsonconverter.generated.SabyDocParser
import ru.tensor.sbis.richtext.converter.BaseRichTextConverter
import ru.tensor.sbis.richtext.converter.cfg.Configuration
import ru.tensor.sbis.richtext.converter.cfg.RenderOptions
import ru.tensor.sbis.richtext.converter.cfg.ShrinkTableConfiguration
import ru.tensor.sbis.richtext.converter.handler.view.BlockTagHandler
import ru.tensor.sbis.richtext.converter.json.JsonTagStreamProcessor
import ru.tensor.sbis.richtext.util.FileUtil
import ru.tensor.sbis.richtext.util.HtmlCssClass
import ru.tensor.sbis.richtext.util.HtmlTag
import ru.tensor.sbis.richtext.R
import ru.tensor.sbis.richtext.RichTextPlugin
import ru.tensor.sbis.richtext.converter.handler.NameTagHandler
import ru.tensor.sbis.richtext.converter.handler.base.IgnoreTextHandler

private const val ROOT_FOLDER = "sabydoc"
private const val IMAGE_FOLDER = "$ROOT_FOLDER/images"

/**
 * Конвертер файлов с расширением .sabydoc в модель, содержащую стилизованный spannable текст
 * для рендера в [ru.tensor.sbis.richtext.view.RichViewLayout] и опциональное оглавление.
 * Содержимое sabydoc файла основано на json markup-model
 * https://wi.sbis.ru/doc/platform/developmentapl/interface-development/ws4/markup-model/
 *
 * По умолчанию установлена конфигурация: расстояние между параграфами 6dp,
 * отображение таблиц (в ограниченном режиме просмотра с возможностью открытия на весь экран), изображений,
 * цитат, инфоблоков, заголовка документа. Ссылки будут декорироваться только при наличии источника данных в приложении
 * [ru.tensor.sbis.toolbox_decl.linkopener.service.LinkDecoratorServiceRepository]
 *
 * @author am.boldinov
 */
class SabyDocRichTextConverter @JvmOverloads constructor(
    context: Context, configuration: Configuration? = null
) : BaseRichTextConverter(context, configuration), SabyDocConverter {

    private val contentsProcessor = SabyDocContentsProcessor()
    private val tagStreamProcessor = JsonTagStreamProcessor(mTagHandlerDelegate)

    override fun setConfiguration(configuration: Configuration?) {
        val config = configuration ?: getDefaultConfiguration()
        super.setConfiguration(config)
        registerHandlers(config)
    }

    @Synchronized
    override fun convertFromFile(filePath: String): SabyDoc {
        try {
            tagStreamProcessor.onDocumentStart()
            SabyDocParser.create(
                tagStreamProcessor, contentsProcessor,
                getImageFolderPath(), true
            ).apply {
                addSafeTags()
                parseFromFile(filePath)
            }
        } catch (e: Exception) {
            CommonUtils.handleException(e)
        } finally {
            tagStreamProcessor.onDocumentEnd()
        }
        return SabyDoc(tagStreamProcessor.buildResult(), contentsProcessor.buildResult())
    }

    override fun parse(source: String) {
        SabyDocParser.create(
            tagStreamProcessor, getImageFolderPath(), true
        ).apply {
            addSafeTags()
            parse(source)
        }
    }

    override fun getTagStreamProcessor() = tagStreamProcessor

    private fun registerHandlers(configuration: Configuration) {
        val blockQuoteTagHandler = BlockTagHandler(mContext, SbisMobileIcon.Icon.smi_Quote)
        setCustomTagHandler(HtmlTag.BLOCKQUOTE, blockQuoteTagHandler)
        setCssClassTagHandler(HtmlTag.DIV, HtmlCssClass.BLOCKQUOTE, blockQuoteTagHandler)
        val infoBlockTagHandler = BlockTagHandler(mContext, SbisMobileIcon.Icon.smi_Info)
        setCssClassTagHandler(HtmlTag.DIV, HtmlCssClass.INFO_BLOCK_OLD, infoBlockTagHandler)
        setCssClassTagHandler(HtmlTag.DIV, HtmlCssClass.INFO_BLOCK, infoBlockTagHandler)
        val cssClassConverter = configuration.cssConfiguration.provideClassConverter(mContext)
        setCustomTagHandler(HtmlTag.SabyDoc.NAME, NameTagHandler(cssClassConverter))
        setCustomTagHandler(HtmlTag.SabyDoc.VERSION, IgnoreTextHandler())
    }

    private fun getDefaultConfiguration(): Configuration {
        val decorateLinks = RichTextPlugin.component.decoratedLinkServiceRepository != null
        return Configuration.Builder(
            RenderOptions().drawLinkAsDecorated(decorateLinks)
                .drawWrappedImages(true)
                .paragraphLineSpacing(mContext.resources.getDimensionPixelSize(R.dimen.richtext_default_paragraph_spacing))
        ).tableConfiguration(ShrinkTableConfiguration()).build()
    }

    private fun getImageFolderPath(): String {
        return FileUtil.getFolderCachePath(mContext, IMAGE_FOLDER)
    }

    private fun SabyDocParser.addSafeTags() {
        SAFE_TAGS.forEach {
            addSafeTag(it)
        }
    }
}

