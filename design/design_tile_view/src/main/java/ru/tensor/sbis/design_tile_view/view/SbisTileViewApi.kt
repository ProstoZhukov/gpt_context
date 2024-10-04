package ru.tensor.sbis.design_tile_view.view

import android.view.View
import androidx.annotation.ColorInt
import ru.tensor.sbis.design.theme.VerticalAlignment
import ru.tensor.sbis.design_tile_view.R
import ru.tensor.sbis.design_tile_view.Rectangle
import ru.tensor.sbis.design_tile_view.SbisTileViewImageModel
import ru.tensor.sbis.design_tile_view.SbisTileViewPlaceholder

/**
 * API компонента Плитка
 *
 * @author us.bessonov
 */
interface SbisTileViewApi {

    /**
     * Задаёт модель изображения
     */
    fun setImageModel(model: SbisTileViewImageModel)

    /**
     * Устанавливает заглушку при отсутствии изображения
     */
    fun setPlaceholder(placeholder: SbisTileViewPlaceholder)

    /**
     * Задаёт выравнивание контейнера с содержимым
     */
    fun setContentAlignment(alignment: VerticalAlignment)

    /**
     * Задаёт радиус скругления плитки
     */
    fun setCornerRadius(radius: Float)

    /**
     * Задаёт наличие тени у плитки
     */
    fun setNeedSetupShadow(needShadow: Boolean)

    /**
     * Задаёт начальный цвет фона для градиента
     */
    fun setStartBackgroundColor(@ColorInt color: Int)

    /**
     * Задаёт конечный цвет фона для градиента
     */
    fun setEndBackgroundColor(@ColorInt color: Int)

    /**
     * Включить или выключить обводку у изображения.
     * Применяется только если форма изображения ([SbisTileViewImageModel.shape]) предусматривает наличие отступа от
     * краёв плитки (для всех форм, кроме [Rectangle]). По умолчанию обводка отсутствует.
     *
     * @see [R.attr.SbisTileView_enableImageBorder]
     */
    fun setImageBorderEnabled(isEnabled: Boolean)

    /**
     * Задаёт [View] с содержимым плитки
     */
    fun setContentView(view: View)

    /**
     * Задаёт [View] контейнера с содержимым, отображаемым поверх изображения (шильдики и т.п.).
     * При использовании с [setBottomView] занимает верхнюю часть изображения, иначе изображение целиком
     */
    fun setTopView(view: View)

    /**
     * Задаёт [View] контейнера с содержимым, отображаемым поверх изображения (шильдики и т.п.).
     * При использовании с [setTopView] занимает нижнюю часть изображения, иначе изображение целиком
     */
    fun setBottomView(view: View)
}