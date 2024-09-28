package ru.tensor.sbis.common_views.document.icon

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.common.util.FileUtil
import ru.tensor.sbis.common.util.getNewFileIconByType
import ru.tensor.sbis.common.util.getNewFileIconColorResByType
import ru.tensor.sbis.common.util.getNewUnknownFileIcon
import ru.tensor.sbis.common_views.R
import ru.tensor.sbis.design.SbisMobileIcon

/**
 * Класс вьюшки для отображения иконки документа
 * Через атрибуты можно задать цвет, размер и расширение, на основании которого строится иконка
 *
 * @author sa.nikitin
 */
class DocumentIconView : AppCompatImageView {

    private var documentIconDrawable: DocumentIconDrawable
    private var iconSizePx: Int = -1

    @ColorInt
    private var iconColor: Int = -1

    //region constructor
    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        scaleType = ScaleType.CENTER
        documentIconDrawable = DocumentIconDrawable(context)
        setImageDrawable(documentIconDrawable)
        if (attrs != null) {
            val attrsArray: TypedArray =
                getContext().obtainStyledAttributes(attrs, R.styleable.CommonViewsDocumentIconView, 0, 0)
            try {
                processAttrs(attrsArray)
            } finally {
                attrsArray.recycle()
            }
        }
    }

    private fun processAttrs(attrs: TypedArray) {
        iconSizePx = attrs.getDimensionPixelSize(R.styleable.CommonViewsDocumentIconView_DocumentIconView_iconSize, -1)
        iconColor = attrs.getColor(R.styleable.CommonViewsDocumentIconView_DocumentIconView_iconColor, -1)
        val documentExtension =
            attrs.getString(R.styleable.CommonViewsDocumentIconView_DocumentIconView_extension) ?: StringUtils.EMPTY
        if (iconSizePx != -1 && documentExtension.isNotEmpty()) {
            setDocumentExtension(documentExtension)
        }
    }
    //endregion

    /**
     * Установить расширение документа, на основании которого построятся параметры иконки
     *
     * @param documentExtension Расширение документа
     */
    fun setDocumentExtension(documentExtension: String) {
        val type: FileUtil.FileType = FileUtil.detectFileTypeByExtension(documentExtension.toLowerCase())
        val icon: SbisMobileIcon.Icon = getNewFileIconByType(type)
        if (icon == getNewUnknownFileIcon()) {
            onNewExtensionDocumentIconParams(documentExtension)
        } else {
            onNewDefaultDocumentIconParams(type, icon)
        }
    }

    private fun onNewExtensionDocumentIconParams(documentExtension: String) {
        val currentParams = documentIconDrawable.getParams()
        if (currentParams is ExtensionDocumentIconParams) {
            currentParams.update(documentExtension)
            documentIconDrawable.onParamsChanged()
        } else {
            setDocumentIconParams(DocumentIconParamsBuilder.buildExtensionParams(context, documentExtension))
        }
    }

    private fun onNewDefaultDocumentIconParams(type: FileUtil.FileType, icon: SbisMobileIcon.Icon) {
        val currentParams = documentIconDrawable.getParams()
        if (currentParams is DefaultDocumentIconParams) {
            currentParams.icon = icon
            currentParams.color =
                if (iconColor != -1) iconColor else ContextCompat.getColor(context, getNewFileIconColorResByType(type))
            documentIconDrawable.onParamsChanged()
        } else {
            setDocumentIconParams(
                DocumentIconParamsBuilder.buildDefaultParams(
                    context,
                    icon,
                    colorRes = getNewFileIconColorResByType(type)
                )
            )
        }
    }

    /**
     * Установить параметры иконки документа
     *
     * @param documentIconParams Параметры иконки документа
     */
    fun setDocumentIconParams(documentIconParams: DocumentIconParams) {
        if (iconSizePx != -1) {
            documentIconParams.sizePx = iconSizePx
        }
        if (iconColor != -1) {
            documentIconParams.color = iconColor
        }
        documentIconDrawable.setParams(documentIconParams)
    }

    /**
     * Установить иконку документа
     *
     * @param documentIconDrawable Иконка документа
     */
    fun setDocumentIcon(documentIconDrawable: DocumentIconDrawable) {
        this.documentIconDrawable = documentIconDrawable
        setImageDrawable(documentIconDrawable)
    }
}