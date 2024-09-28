package ru.tensor.sbis.common_views.document.icon

import androidx.annotation.ColorInt
import ru.tensor.sbis.design.SbisMobileIcon

/**
 * Базовые параметры иконки документа
 *
 * @property sizePx Размер иконки в пикселях
 * @property color Цвет иконки
 *
 * @author sa.nikitin
 */
sealed class DocumentIconParams(
    sizePx: Int,
    @ColorInt color: Int
) {
    var sizePx: Int = sizePx
        set(value) {
            val changed = field != value
            field = value
            if (changed) {
                onSizeChanged()
            }
        }

    @ColorInt
    var color: Int = color
        set(value) {
            val changed = field != value
            field = value
            if (changed) {
                onColorChanged()
            }
        }

    protected open fun onSizeChanged() {
    }

    protected open fun onColorChanged() {
    }

    //region equals & hashCode
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as DocumentIconParams
        if (sizePx != other.sizePx) return false
        if (color != other.color) return false
        return true
    }

    override fun hashCode(): Int {
        var result = sizePx
        result = 31 * result + color
        return result
    }
    //endregion
}

/**
 * Параметры иконки документа, основанные на [SbisMobileIcon.Icon]
 *
 * @property icon Иконка документа в виде [SbisMobileIcon.Icon]
 */
class DefaultDocumentIconParams internal constructor(
    var icon: SbisMobileIcon.Icon,
    sizePx: Int,
    @ColorInt color: Int
) : DocumentIconParams(sizePx, color) {

    //region equals & hashCode
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false
        other as DefaultDocumentIconParams
        if (icon != other.icon) return false
        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + icon.hashCode()
        return result
    }
    //endregion
}

/**
 * Параметры иконки документа, основанные на расширении документа
 *
 * @property extension          Расширение документа
 * @property insideIcon         Признак, обозначающий, следует ли рисовать расширение внутри иконки
 * @property clipExtension      Признак, обозначающий, следует ли обрезать расширение, если оно длинное
 */
class ExtensionDocumentIconParams internal constructor(
    private var extension: String,
    private var insideIcon: Boolean = true,
    private var clipExtension: Boolean = insideIcon,
    sizePx: Int,
    @ColorInt color: Int
) : DocumentIconParams(sizePx, color) {

    val drawer = ExtensionDocumentIconDrawer(extension, insideIcon, clipExtension, sizePx, color)

    internal fun update(extension: String, insideIcon: Boolean = true, clipExtension: Boolean = insideIcon) {
        if (this.extension != extension || this.insideIcon != insideIcon || this.clipExtension != clipExtension) {
            drawer.update(extension, insideIcon, clipExtension)
        }
        this.extension = extension
        this.insideIcon = insideIcon
        this.clipExtension = clipExtension
    }

    override fun onSizeChanged() {
        super.onSizeChanged()
        drawer.updateSize(sizePx)
    }

    override fun onColorChanged() {
        super.onColorChanged()
        drawer.updateColor(color)
    }

    //region equals & hashCode
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false
        other as ExtensionDocumentIconParams
        if (extension != other.extension) return false
        if (insideIcon != other.insideIcon) return false
        if (clipExtension != other.clipExtension) return false
        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + extension.hashCode()
        result = 31 * result + insideIcon.hashCode()
        result = 31 * result + clipExtension.hashCode()
        return result
    }
    //endregion
}