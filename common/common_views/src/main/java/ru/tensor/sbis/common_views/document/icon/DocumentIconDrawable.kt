package ru.tensor.sbis.common_views.document.icon

import android.content.Context
import android.graphics.Canvas
import com.mikepenz.iconics.IconicsDrawable
import ru.tensor.sbis.common.util.getNewFileIconByType
import ru.tensor.sbis.common.util.getNewUnknownFileIcon

/**
 * Класс иконки документа в виде Drawable
 * Иконка строится на основании параметров [DocumentIconParams]
 * Построить параметры можно через [DocumentIconParamsBuilding]
 *
 * Для распространённых форматов имеются нарисованные иконки (см. [getNewFileIconByType]),
 * Для всех остальных форматов рисуется расширение либо внутри иконки файла неизвестного типа [getNewUnknownFileIcon],
 * либо без неё
 *
 * DocumentIconDrawable имеет корретный equals, два DocumentIconDrawable равны, если равны их параметры
 *
 * @author sa.nikitin
 */
class DocumentIconDrawable @JvmOverloads constructor(
    context: Context,
    params: DocumentIconParams? = null
) : IconicsDrawable(context) {

    private var params: DocumentIconParams? = null
    private var extensionDocumentIconDrawer: ExtensionDocumentIconDrawer? = null

    init {
        if (params != null) {
            setParams(params)
        }
    }

    /**
     * Получить параметры иконки
     *
     * @return Параметры иконки или null, если они не были заданы
     */
    fun getParams(): DocumentIconParams? = params

    /**
     * Утсановить параметры иконки
     *
     * @param params Параметры иконки
     */
    fun setParams(params: DocumentIconParams?) {
        this.params = params
        onParamsChanged()
    }

    /**
     * Параметры иконки были изменены
     */
    fun onParamsChanged() {
        params?.let { params ->
            when (params) {
                is DefaultDocumentIconParams -> {
                    extensionDocumentIconDrawer = null
                    applyDefaultParams(params)
                }
                is ExtensionDocumentIconParams -> applyExtensionParams(params)
            }
        }
    }

    /**
     * Устанавливает размер иконки для отрисовки.
     * Необходимо использовать в случае динамического расчета размеров
     */
    fun setDrawerSize(size: Int) {
        if (intrinsicWidth != size) {
            extensionDocumentIconDrawer?.updateSize(size)
            sizePx(size)
        }
    }

    private fun applyDefaultParams(params: DefaultDocumentIconParams) {
        icon(params.icon).color(params.color)
        if (params.sizePx > 0) {
            sizePx(params.sizePx)
        }
    }

    private fun applyExtensionParams(params: ExtensionDocumentIconParams) {
        extensionDocumentIconDrawer = params.drawer
        if (params.drawer.boundingIconParams != null) {
            applyDefaultParams(params.drawer.boundingIconParams!!)
        } else {
            icon(' ').color(0)
            if (params.sizePx > 0) {
                sizePx(params.sizePx)
            }
        }
        invalidateSelf()
    }

    //region Drawable
    override fun draw(canvas: Canvas) {
        if (params != null) {
            super.draw(canvas)
            extensionDocumentIconDrawer?.draw(canvas, bounds)
        }
    }
    //endregion

    //region equals & hashCode
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as DocumentIconDrawable
        if (params != other.params) return false
        return true
    }

    override fun hashCode(): Int {
        return params?.hashCode() ?: 0
    }
    //endregion
}