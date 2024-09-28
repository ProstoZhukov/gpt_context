package ru.tensor.sbis.common_views.document.thumbnail

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import ru.tensor.sbis.common.util.FileUriUtil
import ru.tensor.sbis.common.util.FileUtil
import ru.tensor.sbis.common_views.document.icon.DocumentIconDrawable
import ru.tensor.sbis.common_views.document.icon.DocumentIconParams
import ru.tensor.sbis.common_views.document.icon.DocumentIconParamsBuilder
import ru.tensor.sbis.common_views.document.icon.DocumentIconParamsBuilding

/**
 * Строитель параметров миниатюры документа
 *
 * @author sa.nikitin
 */
class DocumentThumbnailParamsBuilder(private val context: Context) {

    private var icon: Drawable? = null
    private var uri: Uri? = null

    /**
     * Задать иконку документа через строителя
     *
     * @param iconParamsBuilding    Лямбда-функция построения параметров иконки документа.
     *                              На вход принимает экземпляр основного шага построения параметров иконки документов
     */
    fun icon(iconParamsBuilding: (DocumentIconParamsBuilding.MainBuildingStep) -> DocumentIconParams): DocumentThumbnailParamsBuilder =
        apply {
            this.icon = DocumentIconDrawable(context, iconParamsBuilding(DocumentIconParamsBuilder.create(context)))
        }

    /**
     * Задать иконку документа через ссылку на drawable-ресурс
     */
    fun icon(@DrawableRes iconRes: Int): DocumentThumbnailParamsBuilder = apply {
        this.icon = ContextCompat.getDrawable(context, iconRes)
    }

    /**
     * Задать иконку документа в виде Drawable
     */
    fun icon(icon: Drawable): DocumentThumbnailParamsBuilder = apply {
        this.icon = icon
    }

    /**
     * Задать ссылку на миниатюру документа
     *
     * @param uri Ссылка на миниатюру документа
     */
    fun uri(uri: String): DocumentThumbnailParamsBuilder = apply {
        this.uri = FileUriUtil.parseUri(uri)
    }

    /**
     * Построить параметры миниатюры документа
     */
    fun build(): DocumentThumbnailParams {
        if (icon == null) {
            icon { iconBuilder -> iconBuilder.fromType(FileUtil.FileType.UNKNOWN).build() }
        }
        return DocumentThumbnailParams(icon!!, uri)
    }

    /**
     * Сбросить состояние строителя
     */
    fun reset(): DocumentThumbnailParamsBuilder = apply {
        icon = null
        uri = null
    }
}