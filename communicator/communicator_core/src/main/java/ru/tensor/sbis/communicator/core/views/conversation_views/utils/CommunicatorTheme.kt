package ru.tensor.sbis.communicator.core.views.conversation_views.utils

import android.content.Context
import android.graphics.Paint
import android.text.TextPaint
import android.text.TextPaint.ANTI_ALIAS_FLAG
import androidx.annotation.ColorInt
import androidx.annotation.Px
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.custom_view_tools.utils.PAINT_MAX_ALPHA
import ru.tensor.sbis.design.custom_view_tools.utils.SimpleTextPaint
import ru.tensor.sbis.design.utils.extentions.getColorFromAttr
import ru.tensor.sbis.design.utils.getDimen
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.design.utils.getThemeColorInt
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.communicator.design.R as RCommunicatorDesign

/**
 * Тема модуля communicator для инициализации и переиспользования [TextPaint],
 * использующихся для рисования ячеек реестра диалогов/каналов.
 * Также хранит строки используемых иконок и цвета, полученные единожды из ресурсов.
 * Подход позаимствован из telegram для избежания возможных задержек при обращении к ресурсам во время создания
 * и отрисовки кастомных слоев, используемых в ячейках диалогов/каналов.
 * ВНИМАНИЕ: не хранить здесь TextPaint или сущности ресурсов, которые могут быть изменены в рантайме,
 * или при изменении конфигурации. Например: [TextPaint], который может иметь разные цвета или размеры,
 * использующий сразу в нескольких view - изменение цвета или размера приведет к неправильно отрисовке другой элементов,
 * использующих этот же [TextPaint]
 *
 * @author vv.chekurda
 */
object CommunicatorTheme {

    /** Краска текста названия заголовка у ячеек диалогов/каналов */
    lateinit var theme_titlePaint: TextPaint
        private set
    /** Краска текста названия заголовка у ячеек диалогов/каналов для заблокированных пользователей */
    lateinit var theme_authorBlockTitlePaint: TextPaint
        private set
    /** Краска текста количества непрочитанных в контенте ячеек диалогов/каналов */
    lateinit var theme_contrastUnreadCountPaint: TextPaint
        private set
    /** Краска текста количества непрочитанных в контенте ячеек диалогов/каналов */
    lateinit var theme_unaccentedUnreadCountPaint: TextPaint
        private set
    /** Краска фона количества непрочитанных в контенте ячеек диалогов/каналов */
    lateinit var theme_unreadCountOrangePaint: TextPaint
        private set
    /** Краска фона для второстепенного количества непрочитанных в контенте ячеек диалогов/каналов */
    lateinit var theme_unreadCountSecondaryPaint: TextPaint
        private set
    /** Краска текста времени и даты в заголовке ячеек диалогов/каналов */
    lateinit var theme_timePaint: TextPaint
        private set
    /** Краска текста времени для непросмотренных в заголовке ячеек диалогов */
    lateinit var theme_unviewedTimePaint: TextPaint
        private set
    /** Краска темы диалога контенте ячеек диалогов */
    lateinit var theme_dialogTitlePaint: TextPaint
        private set
    /** Краска текста сообщения в контенте ячеек диалогов/каналов */
    lateinit var theme_messagePaint: TextPaint
        private set
    /** Краска текста для типа сервисных сообщений в контенте ячеек диалогов */
    lateinit var theme_serviceTypePaint: TextPaint
        private set
    /** Краска текста сервисных сообщений в контенте ячеек диалогов */
    lateinit var theme_serviceMessagePaint: TextPaint
        private set
    /** Краска текста разметки "Я:" в контенте ячеек диалогов */
    lateinit var theme_iAmAuthorPaint: TextPaint
        private set
    /** Краска текста иконки документа в контенте ячеек диалогов */
    lateinit var theme_documentIconPaint: TextPaint
        private set
    /** Краска текста иконки иконки для названия диалога в контенте ячеек диалогов */
    lateinit var theme_dialogNameIconPaint: TextPaint
        private set
    /** Краска текста иконки документа в контенте ячеек диалогов */
    lateinit var theme_chatDocumentIconPaint: TextPaint
        private set
    /** Краска текста названия документа в контенте ячеек диалогов */
    lateinit var theme_documentNamePaint: TextPaint
        private set
    /** Краска текста иконки персоны внутри компании в заголовке ячеек диалогов */
    lateinit var theme_personCompanyIconPaint: TextPaint
        private set
    /** Краска текста иконки непрочитанного исходящего в заголовке ячеек диалогов */
    lateinit var theme_unreadIconPaint: TextPaint
        private set
    /** Краска текста иконки драфта сообщения в заголовке ячеек диалогов */
    lateinit var theme_draftIconPaint: TextPaint
        private set
    /** Краска текста иконки статуса ошибки отправки сообщения в заголовке ячеек диалогов */
    lateinit var theme_errorIconPaint: TextPaint
        private set
    /** Краска для разделителей между ячейками реестра диалогов/каналов */
    lateinit var theme_itemSeparatorTheme: Paint
        private set

    /** Краска текста иконки закрепленного канала */
    lateinit var chats_pinnedIconPaint: TextPaint
        private set
    /** Краска фона иконки закрепленного канала */
    lateinit var chats_pinnedBackgroundPaint: TextPaint
        private set

    /** Текст разметки "Я:" для контента ячеек диалогов */
    lateinit var theme_iAmAuthorText: String
        private set
    /** Текст иконки документа для контента ячеек диалогов */
    private lateinit var theme_documentIconText: String
    /** Текст иконки персоны внутри компанни для заголовка ячеек диалогов */
    lateinit var theme_personCompanyIconText: String
        private set
    /** Текст иконки драфта сообщения для заголовка ячеек диалогов */
    lateinit var theme_draftIconText: String
        private set
    /** Текст иконки статуса ошибки отправки сообщения для заголовка ячеек диалогов */
    lateinit var theme_errorSendingIconText: String
        private set
    /** Текст иконки непрочитанности исходящего сообщения для заголовка ячеек диалогов */
    lateinit var theme_unreadIconText: String
        private set
    /** Текст иконки статуса отправки сообщения для заголовка ячеек диалогов */
    lateinit var theme_sendingIconText: String
        private set
    /** Текст иконки закрепленного канала для ячеек каналов */
    lateinit var chats_pinnedIconText: String
        private set

    @get:Px
    var offsetXS: Int = 0
        private set

    @get:Px
    var offset2XS: Int = 0
        private set

    @get:Px
    var offset3XS: Int = 0
        private set

    @get:Px
    var offsetS: Int = 0
        private set

    @get:Px
    var offsetM: Int = 0
        private set

    @get:Px
    var iconSize2XS: Int = 0
        private set

    @get:Px
    var iconSizeM: Float = 0f
        private set

    /** Цвет выделения текста при поиске для ячеек диалогов/каналов */
    @ColorInt
    var theme_highlightTextColor: Int = 0
        private set

    /** Цвет  иконки статуса отправки сообщения для заголовка ячеек диалогов */
    @ColorInt
    var theme_sendingClockColor: Int = 0
        private set

    @ColorInt
    var textBackgroundColorDecoratorHighlight: Int = 0
        private set

    private var iconSizeXS: Float = 0f
    private var iconSizeXL: Float = 0f
    private var fontSizeLScaleOn: Float = 0f
    private var fontSizeXSScaleOff: Float = 0f
    private var fontSizeMScaleOn: Float = 0f
    private var fontSize3XSScaleOff: Float = 0f
    private var fontSize3XSScaleOn: Float = 0f

    /**
     * Создать ресурсы, испольщующиеся в реестре диалогов
     */
    fun createThemeItemsResources(context: Context) {
        synchronized(this) {
            if (CommunicatorTheme::theme_titlePaint.isInitialized) return
            iconSizeXS = context.getDimen(RDesign.attr.iconSize_xs)
            iconSizeM = context.getDimen(RDesign.attr.iconSize_m)
            offsetXS = context.getDimenPx(RDesign.attr.offset_xs)
            offset2XS = context.getDimenPx(RDesign.attr.offset_2xs)
            offset3XS = context.getDimenPx(RDesign.attr.offset_3xs)
            offsetS = context.getDimenPx(RDesign.attr.offset_s)
            offsetM = context.getDimenPx(RDesign.attr.offset_m)
            iconSize2XS = context.getDimenPx(RDesign.attr.iconSize_2xs)
            iconSizeXL = context.getDimen(RDesign.attr.iconSize_xl)
            fontSizeLScaleOn = context.getDimen(RDesign.attr.fontSize_l_scaleOn)
            fontSizeXSScaleOff = context.getDimen(RDesign.attr.fontSize_xs_scaleOff)
            fontSizeMScaleOn = context.getDimen(RDesign.attr.fontSize_m_scaleOn)
            fontSize3XSScaleOff = context.getDimen(RDesign.attr.fontSize_3xs_scaleOff)
            fontSize3XSScaleOn = context.getDimen(RDesign.attr.fontSize_3xs_scaleOn)

            val robotoRegFont = TypefaceManager.getRobotoRegularFont(context)
            val robotoMediumFont = TypefaceManager.getRobotoMediumFont(context)
            val mobileIconFont = TypefaceManager.getSbisMobileIconTypeface(context)

            val unaccentedIconColor = context.getThemeColorInt(RDesign.attr.unaccentedIconColor)
            val textColor = context.getThemeColorInt(RDesign.attr.textColor)
            val unaccentedTextColor = context.getThemeColorInt(RDesign.attr.unaccentedTextColor)
            val contrastTextColor = context.getThemeColorInt(RDesign.attr.contrastTextColor)
            val labelContrastTextColor = context.getThemeColorInt(RDesign.attr.labelContrastTextColor)
            val labelTextColor = context.getThemeColorInt(RDesign.attr.labelTextColor)
            val primaryColor = context.getThemeColorInt(RDesign.attr.primaryColor)
            val secondarySameBackgroundColor = context.getThemeColorInt(RDesign.attr.secondarySameBackgroundColor)
            val primaryTextColor = context.getThemeColorInt(RDesign.attr.primaryTextColor)
            val linkTextColor = context.getThemeColorInt(RDesign.attr.linkTextColor)
            val primaryIconColor = context.getThemeColorInt(RDesign.attr.primaryIconColor)
            val dangerIconColor = context.getThemeColorInt(RDesign.attr.dangerIconColor)
            val separatorColor = context.getThemeColorInt(RDesign.attr.separatorColor)
            textBackgroundColorDecoratorHighlight = context.getThemeColorInt(RDesign.attr.textBackgroundColorDecoratorHighlight)

            theme_titlePaint = SimpleTextPaint {
                typeface = robotoMediumFont
                textSize = fontSizeLScaleOn
                color = textColor
            }
            theme_authorBlockTitlePaint = SimpleTextPaint {
                typeface = robotoRegFont
                color = labelTextColor
                textSize = fontSizeLScaleOn
                alpha = (PAINT_MAX_ALPHA * 0.6).toInt()
            }
            theme_contrastUnreadCountPaint = SimpleTextPaint {
                color = contrastTextColor
                typeface = robotoRegFont
                textSize = fontSize3XSScaleOff
            }
            theme_unaccentedUnreadCountPaint = SimpleTextPaint {
                color = unaccentedTextColor
                typeface = robotoRegFont
                textSize = fontSize3XSScaleOff
            }
            theme_unreadCountOrangePaint = SimpleTextPaint {
                color = primaryColor
                typeface = robotoRegFont
            }
            theme_unreadCountSecondaryPaint = SimpleTextPaint {
                color = secondarySameBackgroundColor
                typeface = robotoRegFont
            }
            theme_timePaint = SimpleTextPaint {
                typeface = robotoRegFont
                color = unaccentedTextColor
                textSize = fontSizeXSScaleOff
            }
            theme_unviewedTimePaint = SimpleTextPaint {
                typeface = robotoRegFont
                color = primaryTextColor
                textSize = fontSizeXSScaleOff
            }
            theme_dialogTitlePaint = SimpleTextPaint {
                typeface = robotoRegFont
                color = labelContrastTextColor
                textSize = fontSizeMScaleOn
            }
            theme_messagePaint = SimpleTextPaint {
                typeface = robotoRegFont
                color = textColor
                textSize = fontSizeMScaleOn
                alpha = (PAINT_MAX_ALPHA * 0.6).toInt()
            }
            theme_serviceTypePaint = SimpleTextPaint {
                typeface = robotoRegFont
                linkColor = linkTextColor
                textSize = fontSizeMScaleOn
            }
            theme_serviceMessagePaint = SimpleTextPaint {
                typeface = robotoRegFont
                textSize = fontSizeMScaleOn
            }
            theme_iAmAuthorPaint = SimpleTextPaint {
                typeface = robotoRegFont
                color = labelContrastTextColor
                textSize = fontSizeMScaleOn
            }
            theme_documentIconPaint = SimpleTextPaint {
                typeface = mobileIconFont
                color = unaccentedIconColor
                textSize = fontSize3XSScaleOn
            }
            theme_dialogNameIconPaint = SimpleTextPaint {
                typeface = mobileIconFont
                color = unaccentedIconColor
                textSize = iconSizeXS
            }
            theme_chatDocumentIconPaint = SimpleTextPaint {
                typeface = mobileIconFont
                color = unaccentedIconColor
                textSize = fontSizeMScaleOn
            }
            theme_documentNamePaint = SimpleTextPaint {
                typeface = robotoRegFont
                color = textColor
                alpha = (PAINT_MAX_ALPHA * 0.6).toInt()
                textSize = fontSizeMScaleOn
            }
            theme_personCompanyIconPaint = SimpleTextPaint {
                typeface = mobileIconFont
                color = unaccentedIconColor
                textSize = iconSizeXL
            }
            theme_unreadIconPaint = SimpleTextPaint {
                typeface = mobileIconFont
                color = unaccentedIconColor
                textSize = iconSizeM
            }
            theme_draftIconPaint = SimpleTextPaint {
                typeface = mobileIconFont
                color = primaryIconColor
                textSize = iconSizeM
            }
            theme_errorIconPaint = SimpleTextPaint {
                typeface = mobileIconFont
                color = dangerIconColor
                textSize = iconSizeXS
            }
            theme_itemSeparatorTheme = Paint(ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.FILL
                color = separatorColor
            }

            theme_highlightTextColor = textBackgroundColorDecoratorHighlight
            theme_sendingClockColor = unaccentedIconColor
            context.resources.run {
                theme_iAmAuthorText = getString(RCommunicatorDesign.string.communicator_conversation_relevant_message_i_am_author)
                theme_documentIconText = getString(RDesign.string.design_mobile_icon_document)
                theme_personCompanyIconText = getString(RDesign.string.design_mobile_icon_menu_contractors)
                theme_draftIconText = getString(RDesign.string.design_mobile_icon_edited)
                theme_errorSendingIconText = getString(RDesign.string.design_mobile_icon_alert_null)
                theme_unreadIconText = getString(RDesign.string.design_mobile_icon_message_was_read_lower)
                theme_sendingIconText = getString(RDesign.string.design_mobile_icon_clock)
            }
        }
    }

    /**
     * Создать ресурсы, испольщующиеся в реестре каналов
     */
    fun createChatItemsResources(context: Context) {
        createThemeItemsResources(context)
        if (!CommunicatorTheme::chats_pinnedIconPaint.isInitialized) {
            val mobileIconFont = TypefaceManager.getSbisMobileIconTypeface(context)

            chats_pinnedIconPaint = SimpleTextPaint {
                typeface = mobileIconFont
                color = context.getColorFromAttr(RDesign.attr.secondaryIconColor)
            }
            chats_pinnedBackgroundPaint = SimpleTextPaint {
                color = context.getColorFromAttr(com.google.android.material.R.attr.backgroundColor)
            }
            chats_pinnedIconText = context.resources.getString(RDesign.string.design_mobile_icon_pinned)
        }
    }

    /**
     * Обновить ресурсы, зависимые от конфигурации
     */
    fun updateThemeResources(context: Context) {
        if (!CommunicatorTheme::theme_titlePaint.isInitialized) return
        theme_iAmAuthorText = context.resources.getString(RCommunicatorDesign.string.communicator_conversation_relevant_message_i_am_author)
    }
}