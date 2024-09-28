package ru.tensor.sbis.common_views.notification

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewStub
import android.widget.RelativeLayout
import androidx.annotation.AttrRes
import androidx.annotation.DrawableRes
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.request.ImageRequestBuilder
import ru.tensor.sbis.common_views.R
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.theme.global_variables.StyleColor
import ru.tensor.sbis.design.utils.getDimen
import ru.tensor.sbis.design.utils.getThemeColorInt

private const val ICON_TYPE_NONE = 0
private const val ICON_TYPE_TEXT = 1
private const val ICON_TYPE_IMAGE = 2
private const val ICON_TYPE_STATIC_IMAGE = 3

/**
 * Реализация компонента заголовка уведомления. Включает в себя иконку (опционально),
 * заголовок и дату. При использовании в верстке указывается один из трех типов иконки
 * в параметре [R.styleable.CommonViewsNotificationHeaderView_iconType]:
 *  - [ICON_TYPE_NONE] - иконка отсутствует
 *  - [ICON_TYPE_TEXT] - иконка в текстовом формате, указывается методом [setIconText]
 *  - [ICON_TYPE_IMAGE] - иконка представлена изображением, ссылка указывается методом [setIconUrl]
 *  - [ICON_TYPE_STATIC_IMAGE] - иконка представлена статичным изображением из ресурсов
 *
 * @author am.boldinov
 */
class NotificationHeaderView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    /**
     * Вью иконки. Может отсутствовать в случае если указан тип иконки [ICON_TYPE_NONE].
     */
    private val iconView: View?

    /**
     * Вью для отображения даты.
     */
    private val dateView: NotificationDateView

    /**
     * Вью для отображения заголовка.
     */
    private val headerView: SbisTextView

    /**
     * Допустимый тип иконки.
     */
    private val iconType: Int

    /**
     * Учитывалась ли иконка при последнем построении макета.
     */
    private var layoutWithIcon = false

    init {
        var headerText: String? = null
        var iconText: String? = null
        var iconColor: Int? = null
        var iconUrl: String? = null
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.CommonViewsNotificationHeaderView)
            iconType = a.getInt(R.styleable.CommonViewsNotificationHeaderView_NotificationHeaderView_iconType, ICON_TYPE_NONE)
            headerText = a.getString(R.styleable.CommonViewsNotificationHeaderView_NotificationHeaderView_headerText)
            iconText = a.getString(R.styleable.CommonViewsNotificationHeaderView_NotificationHeaderView_iconText)
            if (a.hasValue(R.styleable.CommonViewsNotificationHeaderView_NotificationHeaderView_iconColor)) {
                iconColor = a.getColor(
                    R.styleable.CommonViewsNotificationHeaderView_NotificationHeaderView_iconColor,
                    StyleColor.SECONDARY.getIconColor(context)
                )
            }
            iconUrl = a.getString(R.styleable.CommonViewsNotificationHeaderView_NotificationHeaderView_iconUrl)
            a.recycle()
        } else {
            iconType = ICON_TYPE_NONE
        }

        View.inflate(context, R.layout.view_notification_header, this)
        headerView = findViewById(R.id.common_views_header_text_view)
        dateView = findViewById(R.id.common_views_date_view)
        iconView = when (iconType) {
            ICON_TYPE_NONE -> null
            ICON_TYPE_TEXT -> {
                val iconStub = findViewById<ViewStub>(R.id.stub_icon)
                iconStub.layoutResource = R.layout.view_notification_header_icon_text
                iconStub.inflate()
            }
            ICON_TYPE_IMAGE -> {
                val iconStub = findViewById<ViewStub>(R.id.stub_icon)
                iconStub.layoutResource = R.layout.view_notification_header_icon_image
                iconStub.inflate()
            }
            ICON_TYPE_STATIC_IMAGE -> {
                val iconStub = findViewById<ViewStub>(R.id.stub_icon)
                iconStub.layoutResource = R.layout.view_notification_header_icon_static_image
                iconStub.inflate()
            }
            else -> throw IllegalArgumentException("Unknown icon type $iconType.")
        }

        if (headerText != null) {
            // Есть предзаданный заголовок
            setHeaderText(headerText)
        }
        var preassignedIcon = false
        if (iconText != null) {
            // Есть предзаданная текстовая иконка
            setIconText(iconText)
            preassignedIcon = true
        }
        if (iconColor != null) {
            // Есть предзаданный цвет иконки
            setIconColor(iconColor)
        }
        if (iconUrl != null) {
            // Есть предзаданная иконка по ссылке
            setIconUrl(iconUrl)
            preassignedIcon = true
        }
        if (!preassignedIcon) {
            // Нет иконки, переводим макет в режим без иконки
            requestLayoutWithoutIcon()
        }
    }

    /**
     * Получить вью для отображения даты.
     */
    fun getDateView(): NotificationDateView {
        return dateView
    }

    /**
     * Установка текста заголовка.
     */
    fun setHeaderText(headerText: CharSequence?) {
        headerView.text = headerText
    }

    /**
     * Установка цвета текста заголовка
     */
    fun setHeaderTextColorAttr(@AttrRes colorAttr: Int) {
        headerView.setTextColor(context.getThemeColorInt(colorAttr))
    }

    /**
     * Установка размера текста заголовка
     */
    fun setHeaderTextSizeAttr(@AttrRes sizeAttr: Int) {
        headerView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getDimen(sizeAttr))
    }

    private fun requestLayoutAfterIconChange(iconData: Any?) {
        if (!layoutWithIcon && iconData != null) {
            requestLayoutWithIcon()
        } else if (layoutWithIcon && iconData == null) {
            requestLayoutWithoutIcon()
        }
    }

    /**
     * Установка текстовой икноки для отображения. В случае, если текст иконки null,
     * [iconView] будет скрыта из макета.
     */
    fun setIconText(icon: String?) {
        if (iconType == ICON_TYPE_TEXT) {
            requestLayoutAfterIconChange(icon)
            (iconView as SbisTextView).text = icon
        } else {
            throw IllegalStateException("Illegal icon type $iconType")
        }
    }

    /**
     * Установка цвета текста для текстовой иконки.
     */
    fun setIconColor(color: Int) {
        if (iconType == ICON_TYPE_TEXT) {
            (iconView as SbisTextView).setTextColor(color)
        } else {
            throw IllegalStateException("Illegal icon type $iconType")
        }
    }

    /**
     * Установка ссылки на иконку для отображения. В случае, если url null,
     * [iconView] будет скрыта из макета.
     */
    fun setIconUrl(url: String?) {
        if (iconType == ICON_TYPE_IMAGE) {
            requestLayoutAfterIconChange(url)
            (iconView as SimpleDraweeView).setImageURI(url)
        } else {
            throw IllegalStateException("Illegal icon type $iconType")
        }
    }

    /**
     * Установка картинки из ресурсов. Картинка применяется только в том случае,
     * если значение [iconType] установлено в [ICON_TYPE_STATIC_IMAGE].
     */
    fun setIconStaticImage(@DrawableRes resourceId: Int) {
        if (iconType == ICON_TYPE_STATIC_IMAGE) {
            requestLayoutAfterIconChange(if (resourceId == 0) null else resourceId)
            (iconView as? SimpleDraweeView)?.let { simpleDraweeView ->
                val imageRequest = ImageRequestBuilder.newBuilderWithResourceId(resourceId).build()
                val controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(imageRequest)
                    .setAutoPlayAnimations(true)
                    .build()
                simpleDraweeView.controller = controller
            }
        }
    }

    /**
     * Установка картинки-заглушки. Картинка применяется только в том случае,
     * если значение [iconType] установлено в [ICON_TYPE_IMAGE].
     */
    fun setIconPlaceholder(@DrawableRes resourceId: Int) {
        if (iconType == ICON_TYPE_IMAGE) {
            (iconView as SimpleDraweeView).hierarchy.setPlaceholderImage(resourceId)
        }
    }

    /**
     * Установка сформированной вью модели данных
     */
    fun setHeaderVM(headerVM: NotificationHeaderVM?) {
        visibility = if (headerVM != null) {
            setHeaderText(headerVM.headerText)
            setHeaderTextSizeAttr(headerVM.headerSizeAttr)
            setHeaderTextColorAttr(headerVM.headerColorAttr)
            getDateView().setDate(headerVM.formattedDateText)
            getDateView().setIsRead(headerVM.isReaded)
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    /**
     * Перевести макет в режим разметки с иконкой.
     */
    private fun requestLayoutWithIcon() {
        if (iconType == ICON_TYPE_NONE || iconView == null) {
            throw IllegalStateException("Attempt to request layout with icon for icon type NONE")
        }
        headerView.setPadding(
            0,
            0,
            0,
            0
        ) // В случае наличия иконки у заголовка нет отступов, текст выравнивается по центру
        (headerView.layoutParams as LayoutParams).apply {
            // Выравниваем заголовок по высоте с верхом и низом иконки
            addRule(ALIGN_TOP, iconView.id)
            addRule(ALIGN_BOTTOM, iconView.id)
        }
        iconView.visibility = View.VISIBLE

        layoutWithIcon = true
    }

    /**
     * Перевести макет в режим разметки без иконки.
     */
    private fun requestLayoutWithoutIcon() {
        (headerView.layoutParams as LayoutParams).apply {
            // Убираем выравнивание заголовка по высоте
            removeRule(ALIGN_TOP)
            removeRule(ALIGN_BOTTOM)
        }
        iconView?.visibility = View.GONE

        layoutWithIcon = false
    }
}