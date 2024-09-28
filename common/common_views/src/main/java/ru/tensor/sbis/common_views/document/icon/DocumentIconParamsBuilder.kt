package ru.tensor.sbis.common_views.document.icon

import android.content.Context
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.core.content.ContextCompat
import ru.tensor.sbis.common.util.FileUtil
import ru.tensor.sbis.common.util.getNewFileIconByType
import ru.tensor.sbis.common.util.getNewFileIconColorAttrByType
import ru.tensor.sbis.common.util.getNewFileIconColorResByType
import ru.tensor.sbis.common.util.getNewUnknownFileIcon
import ru.tensor.sbis.common.util.getNewUnknownFileIconColorAttr
import ru.tensor.sbis.common.util.getNewUnknownFileIconColorRes
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.utils.extentions.getColorFromAttr

/**
 * Интерфейс, декларирубщий механизм построения параметров иконки документа
 *
 * @author sa.nikitin
 */
interface DocumentIconParamsBuilding {

    /**
     * Интерфейс, декларирующий основной шаг построения параметров иконки документа
     */
    interface MainBuildingStep {

        /**
         * Начать построение параметров иконки документа на основании расширения
         *
         * @param extension         Расширение документа
         * @param insideIcon        Рисовать расширение внутри иконки файла неизвестного типа, если формат документа не распространённый
         * @param clipExtension     Обрезать расширение до 4 символов, если их более 4 (так спроектировано)
         */
        fun fromExtension(
            extension: String,
            insideIcon: Boolean = true,
            clipExtension: Boolean = insideIcon
        ): AdditionalBuildingStep

        /**
         * Начать построение параметров иконки документа на основании имени
         *
         * @param name              Имя документа
         * @param insideIcon        Рисовать расширение внутри иконки файла неизвестного типа, если формат документа не распространённый
         * @param clipExtension     Обрезать расширение до 4 символов, если их более 4 (так спроектировано)
         */
        fun fromName(
            name: String,
            insideIcon: Boolean = true,
            clipExtension: Boolean = insideIcon
        ): AdditionalBuildingStep

        /**
         * Начать построение параметров иконки документа на основании типа
         *
         * @param type Тип документа
         */
        fun fromType(type: FileUtil.FileType): AdditionalBuildingStep

        /**
         * Начать построение параметров иконки документа на основании [SbisMobileIcon.Icon]
         *
         * @param icon Иконка в виде [SbisMobileIcon.Icon]
         */
        fun fromIcon(icon: SbisMobileIcon.Icon): AdditionalBuildingStep
    }

    /**
     * Интерфейс, декларирующий дополнительный шаг построения параметров иконки документа
     */
    interface AdditionalBuildingStep : LastBuildingStep {

        /**
         * Задать размер иконки в пикселях
         */
        fun sizePx(sizePx: Int): AdditionalBuildingStep

        /**
         * Задать размер иконки через dimen-ресурс
         */
        fun sizeRes(@DimenRes sizeRes: Int): AdditionalBuildingStep

        /**
         * Задать цвет иконки через color-ресурс
         */
        fun colorRes(@ColorRes colorRes: Int): AdditionalBuildingStep

        /**
         * Задать цвет иконки
         */
        fun color(@ColorInt color: Int): AdditionalBuildingStep

        /**
         * Задать цвет иконки через артрибут
         */
        fun colorAttr(@AttrRes colorAttrRest: Int): AdditionalBuildingStep
    }

    /**
     * Интерфейс, декларирующий завершающий шаг построения параметров иконки документа
     */
    interface LastBuildingStep {

        /**
         * Построить параметры иконки документа
         */
        fun build(): DocumentIconParams

        /**
         * Построить иконку документа
         */
        fun buildIconDrawable(): DocumentIconDrawable
    }
}

class DocumentIconParamsBuilder private constructor(
    private val context: Context
) : DocumentIconParamsBuilding.MainBuildingStep,
    DocumentIconParamsBuilding.AdditionalBuildingStep,
    DocumentIconParamsBuilding.LastBuildingStep {

    companion object {

        /**
         * Создать экземпляр основного шага построения параметров иконки документа
         */
        @JvmStatic
        fun create(context: Context): DocumentIconParamsBuilding.MainBuildingStep = DocumentIconParamsBuilder(context)

        /**
         * Создать параметры иконки документа на основании расширения
         *
         * @param context           Контекст
         * @param extension         Расширение документа
         * @param insideIcon        Рисовать расширение внутри иконки файла неизвестного типа, если формат документа не распространённый
         * @param clipExtension     Обрезать расширение до 4 символов, если их более 4 (так спроектировано)
         */
        @JvmStatic
        @JvmOverloads
        fun buildParamsFromExtension(
            context: Context,
            extension: String,
            insideIcon: Boolean = true,
            clipExtension: Boolean = insideIcon
        ): DocumentIconParams {
            val type: FileUtil.FileType = FileUtil.detectFileTypeByExtension(extension.toLowerCase())
            val icon: SbisMobileIcon.Icon = getNewFileIconByType(type)
            return if (icon === getNewUnknownFileIcon()) {
                if (extension.isEmpty()) {
                    buildDefaultParamsAttr(
                        context = context,
                        icon = SbisMobileIcon.Icon.smi_DocumentUnknownType,
                        colorAttr = getNewFileIconColorAttrByType(type)
                    )
                } else {
                    buildExtensionParamsAttr(
                        context = context,
                        extension = extension,
                        insideIcon = insideIcon,
                        clipExtension = clipExtension,
                        colorAttr = getNewFileIconColorAttrByType(type)
                    )
                }
            } else {
                buildDefaultParamsAttr(
                    context = context,
                    icon = icon,
                    colorAttr = getNewFileIconColorAttrByType(type)
                )
            }
        }

        @JvmStatic
        @JvmOverloads
        fun buildExtensionParams(
            context: Context,
            extension: String,
            insideIcon: Boolean = true,
            clipExtension: Boolean = insideIcon,
            sizePx: Int = 0,
            @ColorRes colorRes: Int = getNewUnknownFileIconColorRes()
        ) =
            ExtensionDocumentIconParams(extension, insideIcon, clipExtension, sizePx, getColor(context, colorRes))

        @JvmStatic
        fun buildDefaultParams(
            context: Context,
            icon: SbisMobileIcon.Icon,
            sizePx: Int = 0,
            @ColorRes colorRes: Int
        ) =
            DefaultDocumentIconParams(icon, sizePx, getColor(context, colorRes))

        @JvmStatic
        @JvmOverloads
        fun buildExtensionParamsAttr(
            context: Context,
            extension: String,
            insideIcon: Boolean = true,
            clipExtension: Boolean = insideIcon,
            sizePx: Int = 0,
            @AttrRes colorAttr: Int = getNewUnknownFileIconColorAttr()
        ) =
            ExtensionDocumentIconParams(
                extension = extension,
                insideIcon = insideIcon,
                clipExtension = clipExtension,
                sizePx = sizePx,
                color = context.getColorFromAttr(colorAttr)
            )

        @JvmStatic
        fun buildDefaultParamsAttr(
            context: Context,
            icon: SbisMobileIcon.Icon,
            sizePx: Int = 0,
            @AttrRes colorAttr: Int
        ) =
            DefaultDocumentIconParams(
                icon = icon,
                sizePx = sizePx,
                color = context.getColorFromAttr(colorAttr)
            )

        private fun getColor(context: Context, @ColorRes colorRes: Int) = ContextCompat.getColor(context, colorRes)
    }

    private var params: DocumentIconParams =
        DefaultDocumentIconParams(getNewUnknownFileIcon(), 0, getColor(context, R.color.palette_color_gray4))

    override fun fromExtension(
        extension: String,
        insideIcon: Boolean,
        clipExtension: Boolean
    ): DocumentIconParamsBuilding.AdditionalBuildingStep =
        apply {
            params = buildParamsFromExtension(context, extension, insideIcon, clipExtension)
        }

    override fun fromName(
        name: String,
        insideIcon: Boolean,
        clipExtension: Boolean
    ): DocumentIconParamsBuilding.AdditionalBuildingStep =
        fromExtension(FileUtil.getFileExtension(name), insideIcon, clipExtension)

    override fun fromType(type: FileUtil.FileType): DocumentIconParamsBuilding.AdditionalBuildingStep =
        apply {
            params = DefaultDocumentIconParams(
                getNewFileIconByType(type),
                0,
                getColor(context, getNewFileIconColorResByType(type))
            )
        }

    override fun fromIcon(icon: SbisMobileIcon.Icon): DocumentIconParamsBuilding.AdditionalBuildingStep =
        apply {
            params = DefaultDocumentIconParams(icon, 0, 0)
        }

    override fun sizePx(sizePx: Int): DocumentIconParamsBuilding.AdditionalBuildingStep =
        apply {
            params.sizePx = sizePx
        }

    override fun sizeRes(@DimenRes sizeRes: Int): DocumentIconParamsBuilding.AdditionalBuildingStep =
        sizePx(context.resources.getDimensionPixelSize(sizeRes))

    override fun colorRes(@ColorRes colorRes: Int): DocumentIconParamsBuilding.AdditionalBuildingStep =
        color(getColor(context, colorRes))

    override fun color(color: Int): DocumentIconParamsBuilding.AdditionalBuildingStep =
        apply {
            params.color = color
        }

    override fun colorAttr(@AttrRes colorAttrRest: Int): DocumentIconParamsBuilding.AdditionalBuildingStep =
        color(context.getColorFromAttr(colorAttrRest))

    override fun build(): DocumentIconParams = params

    override fun buildIconDrawable(): DocumentIconDrawable = DocumentIconDrawable(context, params)
}